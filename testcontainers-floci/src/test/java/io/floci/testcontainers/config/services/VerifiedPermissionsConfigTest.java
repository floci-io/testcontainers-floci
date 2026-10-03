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
    }

    @Test
    void shouldApplyCustomVerifiedPermissionsConfig() {
        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        VerifiedPermissionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        VerifiedPermissionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder()
                .enabled(false)
                .build();
        VerifiedPermissionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyCedarUrl() {
        VerifiedPermissionsConfig defaults = VerifiedPermissionsConfig.builder().build();
        assertThat(defaults.getCedarUrl()).isEmpty();

        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder().cedarUrl("http://cedar-sidecar:8080").build();
        assertThat(config.getCedarUrl()).contains("http://cedar-sidecar:8080");
        assertThat(config.toBuilder().build().getCedarUrl()).contains("http://cedar-sidecar:8080");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL", "http://cedar-sidecar:8080");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL");
    }

    @Test
    void shouldApplyCedarImage() {
        VerifiedPermissionsConfig defaults = VerifiedPermissionsConfig.builder().build();
        assertThat(defaults.getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.1.0");

        VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder().cedarImage("floci/floci-sidecar-cedar:1.2.0").build();
        assertThat(config.getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.2.0");
        assertThat(config.toBuilder().build().getCedarImage()).isEqualTo("floci/floci-sidecar-cedar:1.2.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE", "floci/floci-sidecar-cedar:1.2.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE", "floci/floci-sidecar-cedar:1.1.0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE");
    }

    @Test
    void shouldRequireDockerSocketUnlessExternalCedarIsConfigured() {
        assertThat(VerifiedPermissionsConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(VerifiedPermissionsConfig.builder().cedarUrl("http://cedar:8080").build().requiresDockerSocket()).isFalse();
        assertThat(VerifiedPermissionsConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
