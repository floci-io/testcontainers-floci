package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftServerlessConfigTest {

    @Test
    void shouldApplyDefaultRedshiftServerlessConfig() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isSnapshotExpirationEnabled()).isTrue();
        assertThat(config.getSnapshotExpirationSweepIntervalSeconds()).isEqualTo(60);
        assertThat(config.isSnapshotCopyEnabled()).isTrue();
        assertThat(config.getSnapshotCopySweepIntervalSeconds()).isEqualTo(60);
        assertThat(config.isScheduledActionsEnabled()).isTrue();
        assertThat(config.getScheduledActionSweepIntervalSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyCustomRedshiftServerlessConfig() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
                .enabled(false)
                .snapshotExpirationEnabled(false)
                .snapshotExpirationSweepIntervalSeconds(5)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isSnapshotExpirationEnabled()).isFalse();
        assertThat(config.getSnapshotExpirationSweepIntervalSeconds()).isEqualTo(5);
        assertThat(config.isSnapshotCopyEnabled()).isFalse();
        assertThat(config.getSnapshotCopySweepIntervalSeconds()).isEqualTo(6);
        assertThat(config.isScheduledActionsEnabled()).isFalse();
        assertThat(config.getScheduledActionSweepIntervalSeconds()).isEqualTo(7);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftServerlessConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTIONS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftServerlessConfig.builder()
                .snapshotExpirationEnabled(false)
                .snapshotExpirationSweepIntervalSeconds(5)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", "6")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTIONS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", "7");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftServerlessConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTIONS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
                .enabled(false)
                .snapshotExpirationEnabled(false)
                .snapshotExpirationSweepIntervalSeconds(5)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7)
                .build();
        RedshiftServerlessConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isSnapshotExpirationEnabled()).isFalse();
        assertThat(copy.getSnapshotExpirationSweepIntervalSeconds()).isEqualTo(5);
        assertThat(copy.isSnapshotCopyEnabled()).isFalse();
        assertThat(copy.getSnapshotCopySweepIntervalSeconds()).isEqualTo(6);
        assertThat(copy.isScheduledActionsEnabled()).isFalse();
        assertThat(copy.getScheduledActionSweepIntervalSeconds()).isEqualTo(7);
    }

}
