package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftConfigTest {

    @Test
    void shouldApplyDefaultRedshiftConfig() {
        RedshiftConfig config = RedshiftConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultPort()).isEqualTo(5439);
        assertThat(config.getImageVersion()).isEqualTo("postgres:15-alpine");
        assertThat(config.getDockerNetwork()).isNull();
        assertThat(config.getPollIntervalMs()).isEqualTo(1000L);
        assertThat(config.getDefaultCredentialDurationSeconds()).isEqualTo(900);
        assertThat(config.getProxyHandshakeTimeoutMillis()).isEqualTo(10000);
        assertThat(config.getProxyBackendConnectTimeoutMillis()).isEqualTo(5000);
        assertThat(config.getProxyMaxConnections()).isEqualTo(2000);
        assertThat(config.getSpectrumMaxRows()).isEqualTo(1000000L);
        assertThat(config.isAutomatedSnapshotsEnabled()).isTrue();
        assertThat(config.isCloudwatchMetricsEnabled()).isTrue();
        assertThat(config.getSnapshotSweepIntervalSeconds()).isEqualTo(60L);
        assertThat(config.isSnapshotCopyEnabled()).isTrue();
        assertThat(config.getSnapshotCopySweepIntervalSeconds()).isEqualTo(60L);
        assertThat(config.isScheduledActionsEnabled()).isTrue();
        assertThat(config.getScheduledActionSweepIntervalSeconds()).isEqualTo(60L);
        assertThat(config.isSpectrumLakeFormationEnforcement()).isFalse();
    }

    @Test
    void shouldApplyCustomRedshiftConfig() {
        RedshiftConfig config = RedshiftConfig.builder()
                .enabled(false)
                .defaultPort(5000)
                .imageVersion("postgres:16-alpine")
                .dockerNetwork("my-redshift-network")
                .pollIntervalMs(250L)
                .defaultCredentialDurationSeconds(3600)
                .proxyHandshakeTimeoutMillis(2000)
                .proxyBackendConnectTimeoutMillis(1000)
                .proxyMaxConnections(20)
                .spectrumMaxRows(500L)
                .automatedSnapshotsEnabled(false)
                .cloudwatchMetricsEnabled(false)
                .snapshotSweepIntervalSeconds(5L)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6L)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7L)
                .spectrumLakeFormationEnforcement(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultPort()).isEqualTo(5000);
        assertThat(config.getImageVersion()).isEqualTo("postgres:16-alpine");
        assertThat(config.getDockerNetwork()).isEqualTo("my-redshift-network");
        assertThat(config.getPollIntervalMs()).isEqualTo(250L);
        assertThat(config.getDefaultCredentialDurationSeconds()).isEqualTo(3600);
        assertThat(config.getProxyHandshakeTimeoutMillis()).isEqualTo(2000);
        assertThat(config.getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);
        assertThat(config.getProxyMaxConnections()).isEqualTo(20);
        assertThat(config.getSpectrumMaxRows()).isEqualTo(500L);
        assertThat(config.isAutomatedSnapshotsEnabled()).isFalse();
        assertThat(config.isCloudwatchMetricsEnabled()).isFalse();
        assertThat(config.getSnapshotSweepIntervalSeconds()).isEqualTo(5L);
        assertThat(config.isSnapshotCopyEnabled()).isFalse();
        assertThat(config.getSnapshotCopySweepIntervalSeconds()).isEqualTo(6L);
        assertThat(config.isScheduledActionsEnabled()).isFalse();
        assertThat(config.getScheduledActionSweepIntervalSeconds()).isEqualTo(7L);
        assertThat(config.isSpectrumLakeFormationEnforcement()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", "5439")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", "postgres:15-alpine")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS", "1000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS", "900")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "10000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "5000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS", "2000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SPECTRUM_MAX_ROWS", "1000000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_AUTOMATED_SNAPSHOTS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_CLOUDWATCH_METRICS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTIONS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder()
                .defaultPort(5000)
                .imageVersion("postgres:16-alpine")
                .dockerNetwork("my-redshift-network")
                .pollIntervalMs(250L)
                .defaultCredentialDurationSeconds(3600)
                .proxyHandshakeTimeoutMillis(2000)
                .proxyBackendConnectTimeoutMillis(1000)
                .proxyMaxConnections(20)
                .spectrumMaxRows(500L)
                .automatedSnapshotsEnabled(false)
                .cloudwatchMetricsEnabled(false)
                .snapshotSweepIntervalSeconds(5L)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6L)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7L)
                .spectrumLakeFormationEnforcement(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", "5000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", "postgres:16-alpine")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK", "my-redshift-network")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS", "250")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS", "3600")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "2000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "1000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS", "20")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SPECTRUM_MAX_ROWS", "500")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_AUTOMATED_SNAPSHOTS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_CLOUDWATCH_METRICS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_SWEEP_INTERVAL_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", "6")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTIONS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", "7")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SPECTRUM_MAX_ROWS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_AUTOMATED_SNAPSHOTS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_CLOUDWATCH_METRICS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTIONS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftConfig config = RedshiftConfig.builder()
                .enabled(false)
                .defaultPort(5000)
                .imageVersion("test-image")
                .dockerNetwork("test-network")
                .pollIntervalMs(250L)
                .defaultCredentialDurationSeconds(3600)
                .proxyHandshakeTimeoutMillis(2000)
                .proxyBackendConnectTimeoutMillis(1000)
                .proxyMaxConnections(20)
                .spectrumMaxRows(500L)
                .automatedSnapshotsEnabled(false)
                .cloudwatchMetricsEnabled(false)
                .snapshotSweepIntervalSeconds(5L)
                .snapshotCopyEnabled(false)
                .snapshotCopySweepIntervalSeconds(6L)
                .scheduledActionsEnabled(false)
                .scheduledActionSweepIntervalSeconds(7L)
                .spectrumLakeFormationEnforcement(true)
                .build();
        RedshiftConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultPort()).isEqualTo(5000);
        assertThat(copy.getImageVersion()).isEqualTo("test-image");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getPollIntervalMs()).isEqualTo(250L);
        assertThat(copy.getDefaultCredentialDurationSeconds()).isEqualTo(3600);
        assertThat(copy.getProxyHandshakeTimeoutMillis()).isEqualTo(2000);
        assertThat(copy.getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);
        assertThat(copy.getProxyMaxConnections()).isEqualTo(20);
        assertThat(copy.getSpectrumMaxRows()).isEqualTo(500L);
        assertThat(copy.isAutomatedSnapshotsEnabled()).isFalse();
        assertThat(copy.isCloudwatchMetricsEnabled()).isFalse();
        assertThat(copy.getSnapshotSweepIntervalSeconds()).isEqualTo(5L);
        assertThat(copy.isSnapshotCopyEnabled()).isFalse();
        assertThat(copy.getSnapshotCopySweepIntervalSeconds()).isEqualTo(6L);
        assertThat(copy.isScheduledActionsEnabled()).isFalse();
        assertThat(copy.getScheduledActionSweepIntervalSeconds()).isEqualTo(7L);
        assertThat(copy.isSpectrumLakeFormationEnforcement()).isTrue();
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabled() {
        assertThat(RedshiftConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(RedshiftConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
