package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationAutoScalingConfigTest {

    @Test
    void shouldApplyDefaultApplicationAutoScalingConfig() {
        ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomApplicationAutoScalingConfig() {
        ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApplicationAutoScalingConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ApplicationAutoScalingConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder()
                .enabled(false)
                .build();
        ApplicationAutoScalingConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
