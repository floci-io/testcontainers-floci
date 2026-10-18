package io.floci.testcontainers.az;

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
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Tests the provider-independent lifecycle features of the core against Floci Azure: {@code reset()} and the removal
 * of the sibling containers and volumes labelled with the container's resource namespace.
 */
@Tag("integration")
class ContainerLifecycleTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociAzContainer.DESCRIPTOR);
    private static final String NAMESPACE_LABEL = "floci_namespace";
    private static final String SUBSCRIPTION_ID = "00000000-0000-0000-0000-000000000001";

    private static final HttpClient REST_CLIENT = HttpClient.newHttpClient();

    @Test
    void shouldRemoveResourcesOnReset() {
        try (FlociAzContainer container = new FlociAzContainer(IMAGE)) {
            container.start();

            String resourceGroup = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg-"
                    + UUID.randomUUID().toString().substring(0, 8) + "?api-version=2021-04-01";
            HttpResponse<String> created = rest(container, "PUT", resourceGroup, "{\"location\":\"eastus\"}");
            assertThat(created.statusCode()).as(created.body()).isBetween(200, 299);
            assertThat(rest(container, "GET", resourceGroup, null).statusCode()).isEqualTo(200);

            container.reset();

            assertThat(rest(container, "GET", resourceGroup, null).statusCode()).isEqualTo(404);
        }
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        try (FlociAzContainer container = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withRedisConfig(c -> c.enabled(true).mocked(false))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            String cache = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg/providers/Microsoft.Cache/redis/cache";
            HttpResponse<String> created = rest(container, "PUT", cache + "?api-version=2024-03-01", """
                    {"location": "eastus", "properties": {"sku": {"name": "Basic", "family": "C", "capacity": 0}}}
                    """);
            assertThat(created.statusCode()).as(created.body()).isBetween(200, 299);
            await().atMost(Duration.ofMinutes(2))
                    .untilAsserted(() -> assertThat(siblingContainers(namespace)).isNotEmpty());
        }

        assertThat(siblingContainers(namespace)).isEmpty();
        assertThat(siblingVolumes(namespace)).isEmpty();
    }

    /**
     * Sends a JSON request to the ARM management plane of the given container.
     */
    private static HttpResponse<String> rest(FlociAzContainer container, String method, String path, String body) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(container.getEndpoint() + path))
                .header("Authorization", "Bearer test-token");
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
