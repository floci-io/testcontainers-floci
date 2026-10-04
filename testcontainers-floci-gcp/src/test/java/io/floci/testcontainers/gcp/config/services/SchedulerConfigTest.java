package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SchedulerConfigTest {

    @Test
    void shouldApplyDefaultSchedulerConfig() {
        SchedulerConfig config = SchedulerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isInvocationEnabled()).isTrue();
        assertThat(config.getTickIntervalSeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyCustomSchedulerConfig() {
        SchedulerConfig config = SchedulerConfig.builder()
                .enabled(false)
                .invocationEnabled(false)
                .tickIntervalSeconds(1)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isInvocationEnabled()).isFalse();
        assertThat(config.getTickIntervalSeconds()).isEqualTo(1);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SchedulerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_INVOCATION_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_TICK_INTERVAL_SECONDS", "10");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SchedulerConfig.builder()
                .invocationEnabled(false)
                .tickIntervalSeconds(1)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_INVOCATION_ENABLED", "false")
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_TICK_INTERVAL_SECONDS", "1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SchedulerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SCHEDULER_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_SCHEDULER_INVOCATION_ENABLED")
                .doesNotContainKey("FLOCI_GCP_SERVICES_SCHEDULER_TICK_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SchedulerConfig config = SchedulerConfig.builder()
                .enabled(false)
                .invocationEnabled(false)
                .tickIntervalSeconds(1)
                .build();
        SchedulerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isInvocationEnabled()).isFalse();
        assertThat(copy.getTickIntervalSeconds()).isEqualTo(1);
    }
}
