package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GcsConfigTest {

    @Test
    void shouldApplyDefaultGcsConfig() {
        GcsConfig config = GcsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getUploadSessionIdleTimeoutSeconds()).isEqualTo(604800);
        assertThat(config.getUploadSessionSweepIntervalSeconds()).isEqualTo(3600);
    }

    @Test
    void shouldApplyCustomGcsConfig() {
        GcsConfig config = GcsConfig.builder()
                .enabled(false)
                .uploadSessionIdleTimeoutSeconds(3600)
                .uploadSessionSweepIntervalSeconds(60)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getUploadSessionIdleTimeoutSeconds()).isEqualTo(3600);
        assertThat(config.getUploadSessionSweepIntervalSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GcsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GCS_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS", "604800")
                .containsEntry("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS", "3600");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GcsConfig.builder()
                .uploadSessionIdleTimeoutSeconds(3600)
                .uploadSessionSweepIntervalSeconds(60)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GCS_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS", "3600")
                .containsEntry("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS", "60");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GcsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GCS_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GcsConfig config = GcsConfig.builder()
                .enabled(false)
                .uploadSessionIdleTimeoutSeconds(3600)
                .uploadSessionSweepIntervalSeconds(60)
                .build();
        GcsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getUploadSessionIdleTimeoutSeconds()).isEqualTo(3600);
        assertThat(copy.getUploadSessionSweepIntervalSeconds()).isEqualTo(60);
    }
}
