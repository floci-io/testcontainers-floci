package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceGraphConfigTest {

    @Test
    void shouldApplyDefaultResourceGraphConfig() {
        ResourceGraphConfig config = ResourceGraphConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomResourceGraphConfig() {
        ResourceGraphConfig config = ResourceGraphConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGraphConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_RESOURCE_GRAPH_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGraphConfig.builder()
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_RESOURCE_GRAPH_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGraphConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_RESOURCE_GRAPH_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ResourceGraphConfig config = ResourceGraphConfig.builder()
                .enabled(false)
                .build();
        ResourceGraphConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
