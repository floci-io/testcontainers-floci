package io.floci.testcontainers.gcp;

import com.github.dockerjava.api.DockerClient;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the provider-independent lifecycle features of the core against Floci GCP: {@code reset()} and the removal of
 * the sibling containers and volumes labelled with the container's resource namespace.
 */
@Tag("integration")
class ContainerLifecycleTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociGcpContainer.DESCRIPTOR);
    private static final String NAMESPACE_LABEL = "floci_namespace";

    private static final HttpClient REST_CLIENT = HttpClient.newHttpClient();

    @Test
    void shouldRemoveResourcesOnReset() {
        try (FlociGcpContainer container = new FlociGcpContainer(IMAGE)) {
            container.start();

            String topic = "/v1/projects/" + container.getProjectId() + "/topics/reset-"
                    + UUID.randomUUID().toString().substring(0, 8);
            HttpResponse<String> created = rest(container, "PUT", topic, "{}");
            assertThat(created.statusCode()).as(created.body()).isEqualTo(200);

            container.reset();

            assertThat(rest(container, "GET", topic, null).statusCode()).isEqualTo(404);
        }
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        try (FlociGcpContainer container = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withCloudSqlConfig(c -> c.enabled(true))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            HttpResponse<String> created = rest(container, "POST",
                    "/sql/v1beta4/projects/" + container.getProjectId() + "/instances", """
                            {
                              "name": "lifecycle",
                              "region": "us-central1",
                              "databaseVersion": "POSTGRES_17",
                              "settings": {"tier": "db-custom-1-3840"}
                            }
                            """);
            assertThat(created.statusCode()).as(created.body()).isEqualTo(200);
            assertThat(siblingContainers(namespace)).isNotEmpty();
        }

        assertThat(siblingContainers(namespace)).isEmpty();
        assertThat(siblingVolumes(namespace)).isEmpty();
    }

    /**
     * Sends a REST request with an optional JSON body to the given container.
     */
    private static HttpResponse<String> rest(FlociGcpContainer container, String method, String path, String body) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(container.getEndpoint() + path));
        if (body != null) {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }

        try {
            return REST_CLIENT.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("REST call " + method + " " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("REST call " + method + " " + path + " interrupted", e);
        }
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
