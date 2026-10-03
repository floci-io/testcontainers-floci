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
        assertThat(config.getLocalPrincipalId()).isEmpty();
    }

    @Test
    void shouldApplyCustomSsoOidcConfig() {
        SsoOidcConfig config = SsoOidcConfig.builder()
                .enabled(false)
                .localPrincipalId("user-1234")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getLocalPrincipalId()).contains("user-1234");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoOidcConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SSOOIDC_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoOidcConfig.builder()
                .localPrincipalId("user-1234")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID", "user-1234");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoOidcConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SSOOIDC_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SsoOidcConfig config = SsoOidcConfig.builder()
                .enabled(false)
                .localPrincipalId("user-1234")
                .build();
        SsoOidcConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getLocalPrincipalId()).contains("user-1234");
    }

}
