package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SwfConfigTest {

    @Test
    void shouldApplyDefaultSwfConfig() {
        SwfConfig config = SwfConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isTimeoutSweepEnabled()).isTrue();
        assertThat(config.getTimeoutSweepIntervalSeconds()).isEqualTo(1);
    }

    @Test
    void shouldApplyCustomSwfConfig() {
        SwfConfig config = SwfConfig.builder()
                .enabled(false)
                .timeoutSweepEnabled(false)
                .timeoutSweepIntervalSeconds(5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isTimeoutSweepEnabled()).isFalse();
        assertThat(config.getTimeoutSweepIntervalSeconds()).isEqualTo(5);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SwfConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SWF_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_INTERVAL_SECONDS", "1");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SwfConfig.builder()
                .timeoutSweepEnabled(false)
                .timeoutSweepIntervalSeconds(5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_INTERVAL_SECONDS", "5");
    }

    @Test
    void shouldNotApplyServiceEnvVarsWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        SwfConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SWF_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SwfConfig config = SwfConfig.builder()
                .timeoutSweepEnabled(false)
                .timeoutSweepIntervalSeconds(5)
                .build();

        SwfConfig copy = config.toBuilder().build();

        assertThat(copy.isTimeoutSweepEnabled()).isFalse();
        assertThat(copy.getTimeoutSweepIntervalSeconds()).isEqualTo(5);
    }

}
