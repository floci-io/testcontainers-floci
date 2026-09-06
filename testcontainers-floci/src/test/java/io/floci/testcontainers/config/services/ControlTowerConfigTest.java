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
        assertThat(config.hasSeedLandingZone()).isFalse();
    }

    @Test
    void shouldApplyCustomControlTowerConfig() {
        ControlTowerConfig config = ControlTowerConfig.builder()
                .enabled(false)
                .seedLandingZone(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.hasSeedLandingZone()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlTowerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CONTROLTOWER_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CONTROLTOWER_SEED_LANDING_ZONE", "false");
    }

    @Test
    void shouldApplySeedLandingZoneEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlTowerConfig.builder().seedLandingZone(true).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CONTROLTOWER_SEED_LANDING_ZONE", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlTowerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONTROLTOWER_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CONTROLTOWER_SEED_LANDING_ZONE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ControlTowerConfig config = ControlTowerConfig.builder()
                .enabled(false)
                .seedLandingZone(true)
                .build();
        ControlTowerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.hasSeedLandingZone()).isTrue();
    }
}
