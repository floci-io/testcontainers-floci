package io.floci.testcontainers.services;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.*;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceStateName;
import software.amazon.awssdk.services.ec2.model.Vpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Ec2ServiceTest extends AbstractServiceTest {

    private static final String AMI_ID = "ami-alpine";
    // The Docker image Floci's EC2 image catalog maps AMI_ID to
    private static final String AMI_DOCKER_IMAGE = "public.ecr.aws/docker/library/alpine:latest";

    static Ec2Client ec2;

    static String keyName;
    static byte[] privateKeyPem;
    static String instanceId;


    @BeforeAll
    static void setUp() throws IOException {
        ec2 = client(Ec2Client.builder());
    }

    @Test
    @Order(1)
    void shouldCreateAndDescribeVpc() {
        String vpcId = ec2.createVpc(b -> b.cidrBlock("10.0.0.0/16")).vpc().vpcId();

        assertThat(vpcId).isNotBlank();

        List<Vpc> vpcs = ec2.describeVpcs(b -> b.vpcIds(vpcId)).vpcs();

        assertThat(vpcs).hasSize(1);
        assertThat(vpcs.get(0).cidrBlock()).isEqualTo("10.0.0.0/16");
    }

    @Test
    @Order(2)
    void shouldCreateAndDescribeSecurityGroup() {
        String vpcId = ec2.createVpc(b -> b.cidrBlock("10.1.0.0/16")).vpc().vpcId();

        String groupName = "test-sg-" + System.currentTimeMillis();
        String groupId = ec2.createSecurityGroup(b -> b
                .groupName(groupName)
                .description("Test security group")
                .vpcId(vpcId)).groupId();

        assertThat(groupId).isNotBlank();

        var groups = ec2.describeSecurityGroups(b -> b.groupIds(groupId)).securityGroups();

        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).groupName()).isEqualTo(groupName);
    }

    @Test
    @Order(3)
    void shouldCreateAndImportKeyPair() throws Exception {
        // Generate a real RSA key pair via JSch so we can import the public key and SSH with the private key
        JSch jsch = new JSch();
        KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);

        ByteArrayOutputStream pubKeyStream = new ByteArrayOutputStream();
        kpair.writePublicKey(pubKeyStream, "floci-test");
        String publicKeyOpenSsh = pubKeyStream.toString(StandardCharsets.UTF_8).trim();

        ByteArrayOutputStream privKeyStream = new ByteArrayOutputStream();
        kpair.writePrivateKey(privKeyStream);
        privateKeyPem = privKeyStream.toByteArray();

        keyName = "test-key-ssh-" + System.currentTimeMillis();
        ec2.importKeyPair(b -> b
                .keyName(keyName)
                .publicKeyMaterial(SdkBytes.fromString(publicKeyOpenSsh, StandardCharsets.UTF_8)));

        assertThat(ec2.describeKeyPairs(b -> b.keyNames(keyName))
                .keyPairs()).hasSize(1);
    }

    @Test
    @Order(4)
    void shouldStartInstance() throws Exception {
        assertThat(keyName).as("key pair must have been created by previous test").isNotBlank();

        // Floci pulls the AMI's image as part of the launch and waits up to 5 minutes per pull attempt, so a
        // single stalled pull on a CI runner kept the instance pending past the wait below. Testcontainers
        // aborts and retries stalled pulls, so pull the image up front; Floci then finds it locally.
        new RemoteDockerImage(DockerImageName.parse(AMI_DOCKER_IMAGE)).get();

        instanceId = ec2.runInstances(b -> b
                        .imageId(AMI_ID)
                        .instanceType("t2.micro")
                        .minCount(1)
                        .maxCount(1)
                        .keyName(keyName))
                .instances().get(0).instanceId();

        // A failed launch goes straight to terminated, so wait for the instance to leave pending only
        Instance instance;
        try {
            instance = await().atMost(Duration.ofMinutes(2))
                    .pollInterval(Duration.ofSeconds(1))
                    .until(() -> describeInstance(instanceId), i -> i.state().name() != InstanceStateName.PENDING);
        } catch (ConditionTimeoutException e) {
            throw new AssertionError("EC2 instance " + instanceId + " is still pending. Floci EC2 log:\n"
                    + flociEc2Log(instanceId), e);
        }
        assertThat(instance.state().name())
                .as(() -> "state of EC2 instance " + instanceId + " (" + instance.stateReason()
                        + "). Floci EC2 log:\n" + flociEc2Log(instanceId))
                .isEqualTo(InstanceStateName.RUNNING);
    }

    @Test
    @Order(5)
    void shouldConnectViaSsh() throws Exception {
        assertThat(instanceId).as("instance must have been created by previous test").isNotBlank();

        JSch sshClient = new JSch();
        sshClient.addIdentity("key", privateKeyPem, null, null);

        // The instance may be RUNNING but sshd not yet ready — retry until the connection succeeds
        var sessionRef = new AtomicReference<Session>();
        await().atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofSeconds(2))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    Session s = sshClient.getSession("root", "localhost", floci.getEc2Config().getSshPortRangeStart());
                    s.setConfig("StrictHostKeyChecking", "no");
                    s.connect(5_000);
                    sessionRef.set(s);
                });
        Session session = sessionRef.get();

        try {
            // The SSH session is established — confirm we can actually run a command on the VM
            String kernel = sshExec(session, "uname -s").trim();
            assertThat(kernel).isEqualToIgnoringCase("Linux");
        } finally {
            session.disconnect();
        }
    }

    @Test
    @Order(6)
    void shouldTerminateInstance() {
        assertThat(instanceId).as("instance must have been created by previous test").isNotBlank();

        ec2.terminateInstances(b -> b.instanceIds(instanceId));

        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> ec2.describeInstances(b -> b.instanceIds(instanceId))
                        .reservations().get(0).instances().get(0).state().name() == InstanceStateName.TERMINATED);
    }

    private static Instance describeInstance(String id) {
        return ec2.describeInstances(b -> b.instanceIds(id)).reservations().get(0).instances().get(0);
    }

    /** Floci's log lines about the given EC2 instance and image pulls, to explain a failed launch. */
    private static String flociEc2Log(String id) {
        return floci.getLogs().lines()
                .filter(line -> line.contains(id) || line.contains(".ec2.") || line.contains("ImageCacheService"))
                .collect(Collectors.joining("\n"));
    }

    private static String sshExec(Session session, String command)
            throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        channel.setOutputStream(output);
        channel.setErrStream(output);
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(100);
        }
        channel.disconnect();
        return output.toString(StandardCharsets.UTF_8);
    }

}
