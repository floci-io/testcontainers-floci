package io.floci.testcontainers.services;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.eks.model.Cluster;
import software.amazon.awssdk.services.eks.model.ClusterStatus;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AccessKey;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class EksServiceTest extends AbstractServiceTest {

    static final String NAMESPACE_NAME = "floci-test";

    static EksClient eks;
    static String clusterName;
    static ApiClient k8sApiClient;
    static CoreV1Api coreApi;
    static String eksTokenAccessKeyId;
    static String eksTokenSecretAccessKey;

    @BeforeAll
    static void setUp() {
        clusterName = "eks-k8s-" + System.currentTimeMillis();
        eks = client(EksClient.builder());

        // Floci's EKS token validator rejects the seeded "test"/"test" root credentials outright
        // (a publicly known secret must not be able to mint cluster-admin tokens), so mint a
        // dedicated IAM access key to sign the EKS bearer token with instead.
        IamClient iam = client(IamClient.builder().region(Region.AWS_GLOBAL));
        String userName = "eks-test-user-" + System.currentTimeMillis();
        iam.createUser(b -> b.userName(userName));
        AccessKey accessKey = iam.createAccessKey(b -> b.userName(userName)).accessKey();
        eksTokenAccessKeyId = accessKey.accessKeyId();
        eksTokenSecretAccessKey = accessKey.secretAccessKey();
    }

    @Test
    @Order(1)
    void shouldCreateCluster() {
        eks.createCluster(b -> b
                .name(clusterName)
                .roleArn("arn:aws:iam::000000000000:role/eks-role"));

        List<String> names = eks.listClusters(b -> {}).clusters();
        assertThat(names).contains(clusterName);
    }

    @Test
    @Order(2)
    void shouldWaitForClusterActiveAndConnect() {
        await().atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    Cluster c = eks.describeCluster(b -> b.name(clusterName)).cluster();
                    assertThat(c.status()).isEqualTo(ClusterStatus.ACTIVE);
                });

        Cluster cluster = eks.describeCluster(b -> b.name(clusterName)).cluster();
        String endpoint = cluster.endpoint();
        assertThat(endpoint).isNotBlank();

        // Build Kubernetes API client pointing at the k3s API server
        k8sApiClient = new ApiClient();
        k8sApiClient.setBasePath(endpoint);
        k8sApiClient.setVerifyingSsl(false);

        // Use cluster CA certificate if provided
        if (cluster.certificateAuthority() != null && cluster.certificateAuthority().data() != null) {
            byte[] caCert = Base64.getDecoder().decode(cluster.certificateAuthority().data());
            k8sApiClient.setSslCaCert(new ByteArrayInputStream(caCert));
        }

        coreApi = new CoreV1Api(k8sApiClient);
        refreshEksToken();

        // Wait for the Kubernetes API server to be fully ready
        await().atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofSeconds(2))
                .ignoreExceptions()
                .untilAsserted(() -> coreApi.listNamespace().execute());
    }

    @Test
    @Order(3)
    void shouldCreateNamespace() throws Exception {
        refreshEksToken();

        V1Namespace namespace = new V1Namespace()
                .metadata(new V1ObjectMeta().name(NAMESPACE_NAME));

        V1Namespace created = coreApi.createNamespace(namespace).execute();
        assertThat(created.getMetadata().getName()).isEqualTo(NAMESPACE_NAME);

        V1NamespaceList nsList = coreApi.listNamespace().execute();
        List<String> nsNames = nsList.getItems().stream()
                .map(ns -> ns.getMetadata().getName())
                .toList();
        assertThat(nsNames).contains(NAMESPACE_NAME, "default");
    }

    @Test
    @Order(4)
    void shouldCreateAndReadConfigMap() throws Exception {
        refreshEksToken();

        V1ConfigMap configMap = new V1ConfigMap()
                .metadata(new V1ObjectMeta().name("test-config"))
                .data(Map.of("greeting", "hello-from-floci", "version", "1.0"));

        V1ConfigMap created = coreApi.createNamespacedConfigMap(NAMESPACE_NAME, configMap).execute();
        assertThat(created.getMetadata().getName()).isEqualTo("test-config");

        V1ConfigMap read = coreApi.readNamespacedConfigMap("test-config", NAMESPACE_NAME).execute();
        assertThat(read.getData())
                .containsEntry("greeting", "hello-from-floci")
                .containsEntry("version", "1.0");
    }

    @Test
    @Order(5)
    void shouldDeleteCluster() {
        eks.deleteCluster(b -> b.name(clusterName));

        List<String> names = eks.listClusters(b -> {}).clusters();
        assertThat(names).doesNotContain(clusterName);
    }

    /**
     * (Re-)generates the EKS bearer token (equivalent to {@code aws eks get-token}) and installs
     * it on {@link #k8sApiClient}.
     *
     * <p>Floci validates the presigned STS {@code GetCallerIdentity} URL embedded in the token —
     * signed with {@link #eksTokenAccessKeyId}/{@link #eksTokenSecretAccessKey} rather than the
     * container's seeded root credentials, which it rejects outright — and rejects a token with a
     * presign expiry over 60 seconds as forged, matching real {@code aws-iam-authenticator}
     * behaviour. Real tooling (kubectl's exec-credential plugin) mints a fresh token per invocation
     * rather than reusing one, so tests do the same before each Kubernetes API interaction instead
     * of caching a single token across test methods.
     *
     * <p>The presigned URI must have an explicit {@code "/"} path: Floci's validator parses the
     * decoded request URI and requires {@code getRawPath()} to equal {@code "/"} exactly, which
     * {@code floci.getEndpoint()}'s bare {@code host:port} form (no path) does not satisfy.
     */
    private static void refreshEksToken() {
        AwsCredentialsIdentity credentials = AwsCredentialsIdentity.create(eksTokenAccessKeyId, eksTokenSecretAccessKey);
        SdkHttpRequest requestToSign = SdkHttpRequest.builder()
                .method(SdkHttpMethod.GET)
                .uri(URI.create(floci.getEndpoint() + "/"))
                .appendHeader("x-k8s-aws-id", clusterName)
                .appendRawQueryParameter("Action", "GetCallerIdentity")
                .appendRawQueryParameter("Version", "2011-06-15")
                .build();
        SignedRequest signedRequest = AwsV4HttpSigner.create().sign(r -> r
                .request(requestToSign)
                .identity(credentials)
                .putProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, "sts")
                .putProperty(AwsV4HttpSigner.REGION_NAME, floci.getRegion())
                .putProperty(AwsV4FamilyHttpSigner.AUTH_LOCATION, AwsV4FamilyHttpSigner.AuthLocation.QUERY_STRING)
                .putProperty(AwsV4FamilyHttpSigner.EXPIRATION_DURATION, Duration.ofSeconds(60)));
        String eksToken = "k8s-aws-v1." + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signedRequest.request().getUri().toString().getBytes(StandardCharsets.UTF_8));
        k8sApiClient.addDefaultHeader("Authorization", "Bearer " + eksToken);
    }

}
