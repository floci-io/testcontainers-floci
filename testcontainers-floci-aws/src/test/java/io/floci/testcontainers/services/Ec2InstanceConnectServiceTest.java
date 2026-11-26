package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2instanceconnect.Ec2InstanceConnectClient;
import software.amazon.awssdk.services.ec2instanceconnect.model.Ec2InstanceNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EC2 Instance Connect against an unknown instance. Sending a key to a running instance needs a real EC2 guest
 * container, which {@code Ec2ServiceTest} already covers.
 */
class Ec2InstanceConnectServiceTest extends AbstractServiceTest {

    private static final String ED25519 =
            "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOMqqnkVzrm0SdG6UOoqKLsabgH5C9okWi0dh2l9GKJl user@host";

    static Ec2InstanceConnectClient connect;

    @BeforeAll
    static void setUp() {
        connect = client(Ec2InstanceConnectClient.builder());
    }

    @Test
    void shouldRejectSshPublicKeyForUnknownInstance() {
        assertThatThrownBy(() -> connect.sendSSHPublicKey(b -> b
                .instanceId("i-0000000000000000f")
                .instanceOSUser("ec2-user")
                .sshPublicKey(ED25519)))
                .isInstanceOf(Ec2InstanceNotFoundException.class);
    }
}
