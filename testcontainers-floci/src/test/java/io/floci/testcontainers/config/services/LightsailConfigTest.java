package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class LightsailConfigTest {

    @Test
    void shouldApplyDefaultLightsailConfig() {
        LightsailConfig config = LightsailConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomLightsailConfig() {
        LightsailConfig config = LightsailConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LightsailConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LIGHTSAIL_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        LightsailConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LIGHTSAIL_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        LightsailConfig config = LightsailConfig.builder()
                .enabled(false)
                .build();
        LightsailConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
