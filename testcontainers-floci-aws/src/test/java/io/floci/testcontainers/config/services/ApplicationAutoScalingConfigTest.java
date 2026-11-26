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
        assertThat(config.getScheduledActionIntervalSeconds()).isEqualTo(1L);
    }

    @Test
    void shouldApplyCustomApplicationAutoScalingConfig() {
        ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder()
                .enabled(false)
                .scheduledActionIntervalSeconds(5L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getScheduledActionIntervalSeconds()).isEqualTo(5L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApplicationAutoScalingConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_SCHEDULED_ACTION_INTERVAL_SECONDS", "1");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApplicationAutoScalingConfig.builder()
                .scheduledActionIntervalSeconds(5L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_SCHEDULED_ACTION_INTERVAL_SECONDS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ApplicationAutoScalingConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_APPLICATIONAUTOSCALING_SCHEDULED_ACTION_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder()
                .enabled(false)
                .scheduledActionIntervalSeconds(5L)
                .build();
        ApplicationAutoScalingConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getScheduledActionIntervalSeconds()).isEqualTo(5L);
    }

}
