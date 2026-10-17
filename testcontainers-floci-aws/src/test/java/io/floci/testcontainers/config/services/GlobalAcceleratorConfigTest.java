package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalAcceleratorConfigTest {

    @Test
    void shouldApplyDefaultGlobalAcceleratorConfig() {
        GlobalAcceleratorConfig config = GlobalAcceleratorConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomGlobalAcceleratorConfig() {
        GlobalAcceleratorConfig config = GlobalAcceleratorConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GlobalAcceleratorConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLOBALACCELERATOR_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GlobalAcceleratorConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLOBALACCELERATOR_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GlobalAcceleratorConfig config = GlobalAcceleratorConfig.builder()
                .enabled(false)
                .build();
        GlobalAcceleratorConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
