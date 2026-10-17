package io.floci.testcontainers.gcp.services;

import com.github.dockerjava.api.DockerClient;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the provider-independent lifecycle features of the core against Floci GCP: {@code reset()} and the removal of
 * the sibling containers and volumes labelled with the container's resource namespace.
 */
class LifecycleServiceTest extends AbstractServiceTest {

    private static final String NAMESPACE_LABEL = "floci_namespace";

    @Test
    void shouldRemoveResourcesOnReset() {
        String topic = "/v1/projects/" + projectId() + "/topics/" + uniqueName("reset");
        RestResponse created = rest("PUT", topic, "application/json", "{}");
        assertThat(created.status()).as(created.body()).isEqualTo(200);

        floci.reset();

        assertThat(rest("GET", topic, null, null).status()).isEqualTo(404);
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        try (FlociGcpContainer container = new FlociGcpContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withCloudSqlConfig(c -> c.enabled(true))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            RestResponse created = rest(container, "POST", "/sql/v1beta4/projects/" + container.getProjectId() + "/instances",
                    "application/json", """
                            {
                              "name": "lifecycle",
                              "region": "us-central1",
                              "databaseVersion": "POSTGRES_17",
                              "settings": {"tier": "db-custom-1-3840"}
                            }
                            """);
            assertThat(created.status()).as(created.body()).isEqualTo(200);
            assertThat(siblingContainers(namespace)).isNotEmpty();
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
