package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CognitoConfigTest {

    @Test
    void shouldApplyDefaultCognitoConfig() {
        CognitoConfig config = CognitoConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isEnforceTokenValidityLimits()).isTrue();
    }

    @Test
    void shouldApplyCustomCognitoConfig() {
        CognitoConfig config = CognitoConfig.builder()
                .enabled(false)
                .enforceTokenValidityLimits(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isEnforceTokenValidityLimits()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CognitoConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_COGNITO_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_COGNITO_ENFORCE_TOKEN_VALIDITY_LIMITS", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CognitoConfig.builder()
                .enforceTokenValidityLimits(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_COGNITO_ENFORCE_TOKEN_VALIDITY_LIMITS", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CognitoConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_COGNITO_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_COGNITO_ENFORCE_TOKEN_VALIDITY_LIMITS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CognitoConfig config = CognitoConfig.builder()
                .enabled(false)
                .enforceTokenValidityLimits(false)
                .build();
        CognitoConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isEnforceTokenValidityLimits()).isFalse();
    }

}
