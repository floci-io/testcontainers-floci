package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BigQueryConfigTest {

    private static BigQueryConfig.Builder customBuilder() {
        return BigQueryConfig.builder()
                .mock(true)
                .uploadSessionIdleTimeoutSeconds(3600)
                .uploadSessionSweepIntervalSeconds(60)
                .duckUrl("http://floci-duck:3000")
                .duckCallbackUrl("http://floci-gcp:4588")
                .duckDefaultImage("floci/floci-duck:1.0.0");
    }

    private static void assertCustomValues(BigQueryConfig config) {
        assertThat(config.isMock()).isTrue();
        assertThat(config.getUploadSessionIdleTimeoutSeconds()).isEqualTo(3600);
        assertThat(config.getUploadSessionSweepIntervalSeconds()).isEqualTo(60);
        assertThat(config.getDuckUrl()).contains("http://floci-duck:3000");
        assertThat(config.getDuckCallbackUrl()).contains("http://floci-gcp:4588");
        assertThat(config.getDuckDefaultImage()).isEqualTo("floci/floci-duck:1.0.0");
    }

    @Test
    void shouldApplyDefaultBigQueryConfig() {
        BigQueryConfig config = BigQueryConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getUploadSessionIdleTimeoutSeconds()).isEqualTo(604800);
        assertThat(config.getUploadSessionSweepIntervalSeconds()).isEqualTo(3600);
        assertThat(config.getDuckUrl()).isEmpty();
        assertThat(config.getDuckCallbackUrl()).isEmpty();
        assertThat(config.getDuckDefaultImage()).isEqualTo("floci/floci-duck:latest");
    }

    @Test
    void shouldApplyCustomBigQueryConfig() {
        BigQueryConfig config = customBuilder().enabled(false).build();
        assertThat(config.isEnabled()).isFalse();
        assertCustomValues(config);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BigQueryConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS", "604800")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS", "3600")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_URL")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_CALLBACK_URL")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_DEFAULT_IMAGE", "floci/floci-duck:latest");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS", "3600")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS", "60")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_URL", "http://floci-duck:3000")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_CALLBACK_URL", "http://floci-gcp:4588")
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_DEFAULT_IMAGE", "floci/floci-duck:1.0.0");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_BIGQUERY_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_URL")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_CALLBACK_URL")
                .doesNotContainKey("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_DEFAULT_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BigQueryConfig copy = customBuilder().enabled(false).build().toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertCustomValues(copy);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMockedWithoutDuckUrl() {
        assertThat(BigQueryConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(BigQueryConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(BigQueryConfig.builder().duckUrl("http://floci-duck:3000").build().requiresDockerSocket()).isFalse();
        assertThat(BigQueryConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }
}
