package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AppIntegrationsConfigTest {

    @Test
    void shouldApplyDefaultAppIntegrationsConfig() {
        AppIntegrationsConfig config = AppIntegrationsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomAppIntegrationsConfig() {
        AppIntegrationsConfig config = AppIntegrationsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppIntegrationsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPINTEGRATIONS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AppIntegrationsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPINTEGRATIONS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AppIntegrationsConfig config = AppIntegrationsConfig.builder()
                .enabled(false)
                .build();
        AppIntegrationsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
