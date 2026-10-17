package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceExplorer2ConfigTest {

    @Test
    void shouldApplyDefaultResourceExplorer2Config() {
        ResourceExplorer2Config config = ResourceExplorer2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomResourceExplorer2Config() {
        ResourceExplorer2Config config = ResourceExplorer2Config.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceExplorer2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RESOURCEEXPLORER2_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceExplorer2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RESOURCEEXPLORER2_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ResourceExplorer2Config config = ResourceExplorer2Config.builder()
                .enabled(false)
                .build();
        ResourceExplorer2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
