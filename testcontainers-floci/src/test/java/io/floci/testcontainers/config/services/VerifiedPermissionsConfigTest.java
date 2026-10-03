package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class VerifiedPermissionsConfigTest {

    @Test
    void shouldApplyDefaultVerifiedPermissionsConfig() {
        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getCedarUrl()).isEmpty();
        assertThat(config.getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.1.0");
    }

    @Test
    void shouldApplyCustomVerifiedPermissionsConfig() {
        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder()
                .enabled(false)
                .cedarUrl("http://cedar-sidecar:8080")
                .cedarImage("floci/floci-sidecar-cedar:1.2.0")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getCedarUrl()).contains("http://cedar-sidecar:8080");
        assertThat(config.getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.2.0");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        VerifiedPermissionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL")
                .containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE", "floci/floci-sidecar-cedar:1.1.0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        VerifiedPermissionsConfig.builder()
                .cedarUrl("http://cedar-sidecar:8080")
                .cedarImage("floci/floci-sidecar-cedar:1.2.0")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL", "http://cedar-sidecar:8080")
                .containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE", "floci/floci-sidecar-cedar:1.2.0");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        VerifiedPermissionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL")
                .doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder()
                .enabled(false)
                .cedarUrl("http://cedar-sidecar:8080")
                .cedarImage("floci/floci-sidecar-cedar:1.2.0")
                .build();
        VerifiedPermissionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getCedarUrl()).contains("http://cedar-sidecar:8080");
        assertThat(copy.getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.2.0");
    }

    @Test
    void shouldRequireDockerSocketUnlessExternalCedarIsConfigured() {
        assertThat(VerifiedPermissionsConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(VerifiedPermissionsConfig.builder().cedarUrl("http://cedar:8080").build().requiresDockerSocket()).isFalse();
        assertThat(VerifiedPermissionsConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
