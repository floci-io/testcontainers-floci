package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SsoOidcConfigTest {

    @Test
    void shouldApplyDefaultSsoOidcConfig() {
        SsoOidcConfig config = SsoOidcConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomSsoOidcConfig() {
        SsoOidcConfig config = SsoOidcConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoOidcConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOOIDC_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoOidcConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOOIDC_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SsoOidcConfig config = SsoOidcConfig.builder()
                .enabled(false)
                .build();
        SsoOidcConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyLocalPrincipalId() {
        SsoOidcConfig defaults = SsoOidcConfig.builder().build();
        assertThat(defaults.getLocalPrincipalId()).isEmpty();

        SsoOidcConfig config = SsoOidcConfig.builder().localPrincipalId("user-1234").build();
        assertThat(config.getLocalPrincipalId()).contains("user-1234");
        assertThat(config.toBuilder().build().getLocalPrincipalId()).contains("user-1234");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID", "user-1234");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID");
    }

}
