package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceManagerConfigTest {

    @Test
    void shouldApplyDefaultResourceManagerConfig() {
        ResourceManagerConfig config = ResourceManagerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomResourceManagerConfig() {
        ResourceManagerConfig config = ResourceManagerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceManagerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_RESOURCEMANAGER_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceManagerConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_RESOURCEMANAGER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceManagerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_RESOURCEMANAGER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ResourceManagerConfig config = ResourceManagerConfig.builder()
                .enabled(false)
                .build();
        ResourceManagerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
