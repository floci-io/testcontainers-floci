package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MonitorConfigTest {

    @Test
    void shouldApplyDefaultMonitorConfig() {
        MonitorConfig config = MonitorConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isActivityLogEnabled()).isTrue();
        assertThat(config.getActivityLogCapacity()).isEqualTo(10000);
        assertThat(config.getActivityLogPageSize()).isEqualTo(1000);
    }

    @Test
    void shouldApplyCustomMonitorConfig() {
        MonitorConfig config = MonitorConfig.builder()
                .enabled(false)
                .activityLogEnabled(false)
                .activityLogCapacity(500)
                .activityLogPageSize(50)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isActivityLogEnabled()).isFalse();
        assertThat(config.getActivityLogCapacity()).isEqualTo(500);
        assertThat(config.getActivityLogPageSize()).isEqualTo(50);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitorConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_CAPACITY", "10000")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_PAGE_SIZE", "1000");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitorConfig.builder()
                .activityLogEnabled(false)
                .activityLogCapacity(500)
                .activityLogPageSize(50)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_ENABLED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_CAPACITY", "500")
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_PAGE_SIZE", "50");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MonitorConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MONITOR_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_ENABLED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_CAPACITY")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_PAGE_SIZE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MonitorConfig config = MonitorConfig.builder()
                .enabled(false)
                .activityLogEnabled(false)
                .activityLogCapacity(500)
                .activityLogPageSize(50)
                .build();
        MonitorConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isActivityLogEnabled()).isFalse();
        assertThat(copy.getActivityLogCapacity()).isEqualTo(500);
        assertThat(copy.getActivityLogPageSize()).isEqualTo(50);
    }
}
