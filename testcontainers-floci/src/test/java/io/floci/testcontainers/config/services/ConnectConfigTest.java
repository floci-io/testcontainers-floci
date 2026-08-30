package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectConfigTest {

    @Test
    void shouldApplyDefaultConnectConfig() {
        ConnectConfig config = ConnectConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomConnectConfig() {
        ConnectConfig config = ConnectConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ConnectConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONNECT_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ConnectConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONNECT_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ConnectConfig config = ConnectConfig.builder()
                .enabled(false)
                .build();
        ConnectConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
