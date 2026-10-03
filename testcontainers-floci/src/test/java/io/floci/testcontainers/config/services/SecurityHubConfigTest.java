package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SecurityHubConfigTest {

    @Test
    void shouldApplyDefaultSecurityHubConfig() {
        SecurityHubConfig config = SecurityHubConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomSecurityHubConfig() {
        SecurityHubConfig config = SecurityHubConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecurityHubConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECURITYHUB_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SecurityHubConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECURITYHUB_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SecurityHubConfig config = SecurityHubConfig.builder()
                .enabled(false)
                .build();
        SecurityHubConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
