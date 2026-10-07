package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GkeConfigTest {

    private static GkeConfig.Builder customBuilder() {
        return GkeConfig.builder()
                .mock(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16550, 5)
                .keepRunningOnShutdown(true)
                .endpointMode("network")
                .dockerNetwork("gke-net");
    }

    private static void assertCustomValues(GkeConfig config) {
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:v1.31.4-k3s1");
        assertThat(config.getApiServerBasePort()).isEqualTo(16550);
        assertThat(config.getApiServerPortsCount()).isEqualTo(5);
        assertThat(config.getApiServerMaxPort()).isEqualTo(16554);
        assertThat(config.isKeepRunningOnShutdown()).isTrue();
        assertThat(config.getEndpointMode()).isEqualTo("network");
        assertThat(config.getDockerNetwork()).contains("gke-net");
    }

    @Test
    void shouldApplyDefaultGkeConfig() {
        GkeConfig config = GkeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:latest");
        assertThat(config.getApiServerBasePort()).isEqualTo(6550);
        assertThat(config.getApiServerPortsCount()).isEqualTo(10);
        assertThat(config.getApiServerMaxPort()).isEqualTo(6559);
        assertThat(config.isKeepRunningOnShutdown()).isFalse();
        assertThat(config.getEndpointMode()).isEqualTo("host");
        assertThat(config.getDockerNetwork()).isEmpty();
    }

    @Test
    void shouldApplyCustomGkeConfig() {
        GkeConfig config = customBuilder().enabled(false).build();
        assertThat(config.isEnabled()).isFalse();
        assertCustomValues(config);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GkeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GKE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_DEFAULT_IMAGE", "rancher/k3s:latest")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_API_SERVER_BASE_PORT", "6550")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_API_SERVER_MAX_PORT", "6559")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_KEEP_RUNNING_ON_SHUTDOWN", "false")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_ENDPOINT_MODE", "host")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GKE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_DEFAULT_IMAGE", "rancher/k3s:v1.31.4-k3s1")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_API_SERVER_BASE_PORT", "16550")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_API_SERVER_MAX_PORT", "16554")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_KEEP_RUNNING_ON_SHUTDOWN", "true")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_ENDPOINT_MODE", "network")
                .containsEntry("FLOCI_GCP_SERVICES_GKE_DOCKER_NETWORK", "gke-net");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        customBuilder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_GKE_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_API_SERVER_BASE_PORT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_API_SERVER_MAX_PORT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_KEEP_RUNNING_ON_SHUTDOWN")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_ENDPOINT_MODE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_GKE_DOCKER_NETWORK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GkeConfig copy = customBuilder().enabled(false).build().toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertCustomValues(copy);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(GkeConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(GkeConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(GkeConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
