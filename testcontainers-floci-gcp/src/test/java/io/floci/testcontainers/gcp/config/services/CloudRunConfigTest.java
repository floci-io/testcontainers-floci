package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudRunConfigTest {

    private static CloudRunConfig.Builder customBuilder() {
        return CloudRunConfig.builder()
                .mock(true)
                .defaultPort(9090)
                .startupTimeout(Duration.ofSeconds(60))
                .requestTimeout(Duration.ofMinutes(1))
                .operationTimeout(Duration.ofSeconds(120))
                .cleanupTimeout(Duration.ofMillis(1500))
                .urlHostSuffix("run.example.com")
                .maxWorkerInstances(3);
    }

    private static void assertCustomValues(CloudRunConfig config) {
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultPort()).isEqualTo(9090);
        assertThat(config.getStartupTimeout()).isEqualTo(Duration.ofSeconds(60));
        assertThat(config.getRequestTimeout()).isEqualTo(Duration.ofSeconds(60));
        assertThat(config.getOperationTimeout()).isEqualTo(Duration.ofSeconds(120));
        assertThat(config.getCleanupTimeout()).isEqualTo(Duration.ofMillis(1500));
        assertThat(config.getUrlHostSuffix()).contains("run.example.com");
        assertThat(config.getMaxWorkerInstances()).isEqualTo(3);
    }

    @Test
    void shouldApplyDefaultCloudRunConfig() {
        CloudRunConfig config = CloudRunConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultPort()).isEqualTo(8080);
        assertThat(config.getStartupTimeout()).isEqualTo(Duration.ofSeconds(240));
        assertThat(config.getRequestTimeout()).isEqualTo(Duration.ofSeconds(300));
        assertThat(config.getOperationTimeout()).isEqualTo(Duration.ofSeconds(300));
        assertThat(config.getCleanupTimeout()).isEqualTo(Duration.ofSeconds(15));
        assertThat(config.getUrlHostSuffix()).isEmpty();
        assertThat(config.getMaxWorkerInstances()).isEqualTo(1);
    }

    @Test
    void shouldApplyCustomCloudRunConfig() {
        CloudRunConfig config = customBuilder().enabled(false).build();
        assertThat(config.isEnabled()).isFalse();
        assertCustomValues(config);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudRunConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_DEFAULT_PORT", "8080")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_STARTUP_TIMEOUT", "240s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_REQUEST_TIMEOUT", "300s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_OPERATION_TIMEOUT", "300s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_CLEANUP_TIMEOUT", "15s")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_URL_HOST_SUFFIX")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_MAX_WORKER_INSTANCES", "1");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_DEFAULT_PORT", "9090")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_STARTUP_TIMEOUT", "60s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_REQUEST_TIMEOUT", "60s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_OPERATION_TIMEOUT", "120s")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_CLEANUP_TIMEOUT", "1500ms")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_URL_HOST_SUFFIX", "run.example.com")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_MAX_WORKER_INSTANCES", "3");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDRUN_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_DEFAULT_PORT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_STARTUP_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_REQUEST_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_OPERATION_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_CLEANUP_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_URL_HOST_SUFFIX")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_MAX_WORKER_INSTANCES");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudRunConfig copy = customBuilder().enabled(false).build().toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertCustomValues(copy);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(CloudRunConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(CloudRunConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(CloudRunConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
