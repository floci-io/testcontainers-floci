package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MonitoringConfigTest {

    @Test
    void shouldApplyDefaultMonitoringConfig() {
        MonitoringConfig config = MonitoringConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomMonitoringConfig() {
        MonitoringConfig config = MonitoringConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitoringConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_MONITORING_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitoringConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_MONITORING_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitoringConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_MONITORING_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MonitoringConfig config = MonitoringConfig.builder()
                .enabled(false)
                .build();
        MonitoringConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
