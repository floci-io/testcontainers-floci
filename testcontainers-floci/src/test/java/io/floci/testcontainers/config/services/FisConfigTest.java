package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FisConfigTest {

    @Test
    void shouldApplyDefaultFisConfig() {
        FisConfig config = FisConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomFisConfig() {
        FisConfig config = FisConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FisConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_FIS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FisConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_FIS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FisConfig config = FisConfig.builder()
                .enabled(false)
                .build();
        FisConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
