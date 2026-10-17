package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EventBridgeConfigTest {

    @Test
    void shouldApplyDefaultEventBridgeConfig() {
        EventBridgeConfig config = EventBridgeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomEventBridgeConfig() {
        EventBridgeConfig config = EventBridgeConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventBridgeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EVENTBRIDGE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EventBridgeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EVENTBRIDGE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EventBridgeConfig config = EventBridgeConfig.builder()
                .enabled(false)
                .build();
        EventBridgeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
