package io.floci.testcontainers;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class FlociContainerPublishedPortsTest {

    @Test
    void shouldUseS3AndRemoveContainerWithOnlyApiPortPublished() {
        String containerId;
        try (FlociContainer container = new FlociContainer(TestImages.pinnedImage(FlociContainer.DESCRIPTOR))
                .withPublishedPorts()
                .withDockerSocket(false)
                .withS3Config(c -> c.enabled(true))) {
            container.start();
            containerId = container.getContainerId();

            assertThat(container.getContainerInfo().getNetworkSettings().getPorts().getBindings())
                    .containsOnlyKeys(ExposedPort.tcp(FlociContainer.PORT))
                    .allSatisfy((port, bindings) -> assertThat(bindings).isNotEmpty());

            try (S3Client s3 = S3Client.builder()
                    .endpointOverride(URI.create(container.getEndpoint()))
                    .region(Region.of(container.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(container.getAccessKey(), container.getSecretKey())))
                    .forcePathStyle(true)
                    .build()) {
                String bucket = "published-ports-" + UUID.randomUUID();
                s3.createBucket(b -> b.bucket(bucket));
                s3.putObject(b -> b.bucket(bucket).key("hello.txt"), RequestBody.fromString("hello"));
                assertThat(s3.getObjectAsBytes(b -> b.bucket(bucket).key("hello.txt")).asUtf8String())
                        .isEqualTo("hello");
                s3.deleteObject(b -> b.bucket(bucket).key("hello.txt"));
                s3.deleteBucket(b -> b.bucket(bucket));
            }
        }

        assertThatThrownBy(() -> DockerClientFactory.instance().client().inspectContainerCmd(containerId).exec())
                .isInstanceOf(NotFoundException.class);
    }
}
