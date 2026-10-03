package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class OamConfigTest {

    @Test
    void shouldApplyDefaultOamConfig() {
        OamConfig config = OamConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomOamConfig() {
        OamConfig config = OamConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        OamConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_OAM_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        OamConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_OAM_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        OamConfig config = OamConfig.builder()
                .enabled(false)
                .build();
        OamConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
