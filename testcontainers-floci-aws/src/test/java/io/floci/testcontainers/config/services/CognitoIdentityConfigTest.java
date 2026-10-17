package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CognitoIdentityConfigTest {

    @Test
    void shouldApplyDefaultCognitoIdentityConfig() {
        CognitoIdentityConfig config = CognitoIdentityConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCognitoIdentityConfig() {
        CognitoIdentityConfig config = CognitoIdentityConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CognitoIdentityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_COGNITOIDENTITY_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CognitoIdentityConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_COGNITOIDENTITY_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CognitoIdentityConfig config = CognitoIdentityConfig.builder()
                .enabled(false)
                .build();
        CognitoIdentityConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
