package io.floci.testcontainers.services;

import com.github.dockerjava.api.DockerClient;
import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

/**
 * Tests the provider-independent lifecycle features of the core against Floci: {@code reset()} and the removal of the
 * sibling containers and volumes labelled with the container's resource namespace.
 */
class LifecycleServiceTest extends AbstractServiceTest {

    private static final String NAMESPACE_LABEL = "floci_namespace";

    @Test
    void shouldRemoveResourcesOnReset() {
        String bucket = "reset-" + UUID.randomUUID();
        try (S3Client s3 = client(S3Client.builder().forcePathStyle(true))) {
            s3.createBucket(request -> request.bucket(bucket));
            assertThat(s3.listBuckets().buckets()).anyMatch(b -> b.name().equals(bucket));

            floci.reset();

            assertThatThrownBy(() -> s3.headBucket(request -> request.bucket(bucket)))
                    .isInstanceOf(NoSuchBucketException.class);
        }
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        try (FlociContainer container = new FlociContainer(floci.getDockerImageName())
                .disableAllServices()
                .withRdsConfig(c -> c.enabled(true))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            try (RdsClient rds = RdsClient.builder()
                    .endpointOverride(URI.create(container.getEndpoint()))
                    .region(Region.of(container.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(container.getAccessKey(), container.getSecretKey())))
                    .build()) {
                rds.createDBInstance(request -> request.dbInstanceIdentifier("lifecycle")
                        .engine("postgres").engineVersion("18.4")
                        .dbInstanceClass("db.t3.micro").masterUsername("admin").masterUserPassword("password123"));
            }
            await().atMost(Duration.ofMinutes(2))
                    .untilAsserted(() -> assertThat(siblingContainers(namespace)).isNotEmpty());
        }

        assertThat(siblingContainers(namespace)).isEmpty();
        assertThat(siblingVolumes(namespace)).isEmpty();
    }

    private static List<?> siblingContainers(String namespace) {
        return docker().listContainersCmd().withShowAll(true)
                .withLabelFilter(Map.of(NAMESPACE_LABEL, namespace)).exec();
    }

    private static List<?> siblingVolumes(String namespace) {
        return docker().listVolumesCmd().withFilter("label", List.of(NAMESPACE_LABEL + "=" + namespace))
                .exec().getVolumes();
    }

    private static DockerClient docker() {
        return DockerClientFactory.instance().client();
    }
}
