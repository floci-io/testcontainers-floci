package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class OkeConfigTest {

    @Test
    void shouldApplyDefaultOkeConfig() {
        OkeConfig config = OkeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:v1.30.1-k3s1");
        assertThat(config.getApiServerBasePort()).isEqualTo(6443);
        assertThat(config.getApiServerPortsCount()).isEqualTo(10);
        assertThat(config.getApiServerMaxPort()).isEqualTo(6452);
    }

    @Test
    void shouldApplyCustomOkeConfig() {
        OkeConfig config = OkeConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:v1.31.4-k3s1");
        assertThat(config.getApiServerBasePort()).isEqualTo(16443);
        assertThat(config.getApiServerPortsCount()).isEqualTo(5);
        assertThat(config.getApiServerMaxPort()).isEqualTo(16447);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        OkeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OKE_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_MOCK", "false")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_DEFAULT_IMAGE", "rancher/k3s:v1.30.1-k3s1")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_API_SERVER_BASE_PORT", "6443")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_API_SERVER_MAX_PORT", "6452");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        OkeConfig.builder()
                .mock(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OKE_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_MOCK", "true")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_DEFAULT_IMAGE", "rancher/k3s:v1.31.4-k3s1")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_API_SERVER_BASE_PORT", "16443")
                .containsEntry("FLOCI_OCI_SERVICES_OKE_API_SERVER_MAX_PORT", "16447");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        OkeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OKE_ENABLED", "false")
                .doesNotContainKey("FLOCI_OCI_SERVICES_OKE_MOCK")
                .doesNotContainKey("FLOCI_OCI_SERVICES_OKE_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_OCI_SERVICES_OKE_API_SERVER_BASE_PORT")
                .doesNotContainKey("FLOCI_OCI_SERVICES_OKE_API_SERVER_MAX_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        OkeConfig config = OkeConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .build();
        OkeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("rancher/k3s:v1.31.4-k3s1");
        assertThat(copy.getApiServerBasePort()).isEqualTo(16443);
        assertThat(copy.getApiServerPortsCount()).isEqualTo(5);
        assertThat(copy.getApiServerMaxPort()).isEqualTo(16447);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(OkeConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(OkeConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(OkeConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
