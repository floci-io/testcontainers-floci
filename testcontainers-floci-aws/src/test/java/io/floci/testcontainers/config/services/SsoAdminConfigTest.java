package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SsoAdminConfigTest {

    @Test
    void shouldApplyDefaultSsoAdminConfig() {
        SsoAdminConfig config = SsoAdminConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomSsoAdminConfig() {
        SsoAdminConfig config = SsoAdminConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoAdminConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOADMIN_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SsoAdminConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SSOADMIN_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SsoAdminConfig config = SsoAdminConfig.builder()
                .enabled(false)
                .build();
        SsoAdminConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
