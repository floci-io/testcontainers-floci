package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SecretManagerConfigTest {

    @Test
    void shouldApplyDefaultSecretManagerConfig() {
        SecretManagerConfig config = SecretManagerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomSecretManagerConfig() {
        SecretManagerConfig config = SecretManagerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretManagerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SECRETMANAGER_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretManagerConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SECRETMANAGER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretManagerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SECRETMANAGER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SecretManagerConfig config = SecretManagerConfig.builder()
                .enabled(false)
                .build();
        SecretManagerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
