package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EventBridgeV2ConfigTest {

    @Test
    void shouldApplyDefaultEventBridgeV2Config() {
        EventBridgeV2Config config = EventBridgeV2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomEventBridgeV2Config() {
        EventBridgeV2Config config = EventBridgeV2Config.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventBridgeV2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EVENTBRIDGEV2_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EventBridgeV2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EVENTBRIDGEV2_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EventBridgeV2Config config = EventBridgeV2Config.builder()
                .enabled(false)
                .build();
        EventBridgeV2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
