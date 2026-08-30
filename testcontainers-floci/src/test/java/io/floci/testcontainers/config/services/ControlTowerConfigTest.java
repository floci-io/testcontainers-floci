package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ControlTowerConfigTest {

    @Test
    void shouldApplyDefaultControlTowerConfig() {
        ControlTowerConfig config = ControlTowerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomControlTowerConfig() {
        ControlTowerConfig config = ControlTowerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlTowerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONTROLTOWER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlTowerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONTROLTOWER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ControlTowerConfig config = ControlTowerConfig.builder()
                .enabled(false)
                .build();
        ControlTowerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
