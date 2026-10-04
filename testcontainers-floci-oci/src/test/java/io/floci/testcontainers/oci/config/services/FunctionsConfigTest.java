package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FunctionsConfigTest {

    @Test
    void shouldApplyDefaultFunctionsConfig() {
        FunctionsConfig config = FunctionsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getServerImage()).isEqualTo("fnproject/fnserver:latest");
        assertThat(config.getServerBasePort()).isEqualTo(8085);
        assertThat(config.getServerPortsCount()).isEqualTo(10);
        assertThat(config.getServerMaxPort()).isEqualTo(8094);
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
    }

    @Test
    void shouldApplyCustomFunctionsConfig() {
        FunctionsConfig config = FunctionsConfig.builder()
                .enabled(false)
                .mock(true)
                .serverImage("fnproject/fnserver:0.3.749")
                .serverPortRange(18085, 5)
                .startupTimeoutSeconds(30)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getServerImage()).isEqualTo("fnproject/fnserver:0.3.749");
        assertThat(config.getServerBasePort()).isEqualTo(18085);
        assertThat(config.getServerPortsCount()).isEqualTo(5);
        assertThat(config.getServerMaxPort()).isEqualTo(18089);
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_MOCK", "false")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_IMAGE", "fnproject/fnserver:latest")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_BASE_PORT", "8085")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_MAX_PORT", "8094")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_STARTUP_TIMEOUT_SECONDS", "120");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder()
                .mock(true)
                .serverImage("fnproject/fnserver:0.3.749")
                .serverPortRange(18085, 5)
                .startupTimeoutSeconds(30)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_MOCK", "true")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_IMAGE", "fnproject/fnserver:0.3.749")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_BASE_PORT", "18085")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_MAX_PORT", "18089")
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_STARTUP_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_FUNCTIONS_ENABLED", "false")
                .doesNotContainKey("FLOCI_OCI_SERVICES_FUNCTIONS_MOCK")
                .doesNotContainKey("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_IMAGE")
                .doesNotContainKey("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_BASE_PORT")
                .doesNotContainKey("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_MAX_PORT")
                .doesNotContainKey("FLOCI_OCI_SERVICES_FUNCTIONS_STARTUP_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FunctionsConfig config = FunctionsConfig.builder()
                .enabled(false)
                .mock(true)
                .serverImage("fnproject/fnserver:0.3.749")
                .serverPortRange(18085, 5)
                .startupTimeoutSeconds(30)
                .build();
        FunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getServerImage()).isEqualTo("fnproject/fnserver:0.3.749");
        assertThat(copy.getServerBasePort()).isEqualTo(18085);
        assertThat(copy.getServerPortsCount()).isEqualTo(5);
        assertThat(copy.getServerMaxPort()).isEqualTo(18089);
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(FunctionsConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(FunctionsConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(FunctionsConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
