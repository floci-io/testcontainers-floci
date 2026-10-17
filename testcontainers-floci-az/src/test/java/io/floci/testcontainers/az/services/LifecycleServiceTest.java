package io.floci.testcontainers.az.services;

import com.github.dockerjava.api.DockerClient;
import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Tests the provider-independent lifecycle features of the core against Floci Azure: {@code reset()} and the removal
 * of the sibling containers and volumes labelled with the container's resource namespace.
 */
class LifecycleServiceTest extends AbstractServiceTest {

    private static final String NAMESPACE_LABEL = "floci_namespace";

    @Test
    void shouldRemoveResourcesOnReset() {
        String resourceGroup = createResourceGroup();
        assertThat(rest("GET", resourceGroup + "?api-version=2021-04-01", null).status()).isEqualTo(200);

        floci.reset();

        assertThat(rest("GET", resourceGroup + "?api-version=2021-04-01", null).status()).isEqualTo(404);
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        try (FlociAzContainer container = new FlociAzContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withRedisConfig(c -> c.enabled(true).mocked(false))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            String cache = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg/providers/Microsoft.Cache/redis/cache";
            RestResponse created = rest(container, "PUT", cache + "?api-version=2024-03-01", """
                    {"location": "eastus", "properties": {"sku": {"name": "Basic", "family": "C", "capacity": 0}}}
                    """);
            assertThat(created.isSuccessful()).as(created.toString()).isTrue();
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
