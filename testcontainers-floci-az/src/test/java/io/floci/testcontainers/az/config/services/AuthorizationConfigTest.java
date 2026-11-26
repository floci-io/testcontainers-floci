package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationConfigTest {

    @Test
    void shouldApplyDefaultAuthorizationConfig() {
        AuthorizationConfig config = AuthorizationConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomAuthorizationConfig() {
        AuthorizationConfig config = AuthorizationConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthorizationConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AUTHORIZATION_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthorizationConfig.builder()
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AUTHORIZATION_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthorizationConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AUTHORIZATION_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AuthorizationConfig config = AuthorizationConfig.builder()
                .enabled(false)
                .build();
        AuthorizationConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
