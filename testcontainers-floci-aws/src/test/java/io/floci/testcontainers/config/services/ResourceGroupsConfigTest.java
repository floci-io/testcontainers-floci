package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceGroupsConfigTest {

    @Test
    void shouldApplyDefaultResourceGroupsConfig() {
        ResourceGroupsConfig config = ResourceGroupsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomResourceGroupsConfig() {
        ResourceGroupsConfig config = ResourceGroupsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGroupsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RESOURCE_GROUPS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGroupsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RESOURCE_GROUPS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ResourceGroupsConfig config = ResourceGroupsConfig.builder()
                .enabled(false)
                .build();
        ResourceGroupsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
