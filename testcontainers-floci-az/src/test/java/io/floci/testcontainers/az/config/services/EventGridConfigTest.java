package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EventGridConfigTest {

    @Test
    void shouldApplyDefaultEventGridConfig() {
        EventGridConfig config = EventGridConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultRegion()).isEmpty();
        assertThat(config.getMaxDeliveryAttempts()).isEqualTo(30);
    }

    @Test
    void shouldApplyCustomEventGridConfig() {
        EventGridConfig config = EventGridConfig.builder()
                .enabled(false)
                .defaultRegion("westeurope")
                .maxDeliveryAttempts(5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultRegion()).contains("westeurope");
        assertThat(config.getMaxDeliveryAttempts()).isEqualTo(5);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventGridConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_ENABLED", "true")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_GRID_DEFAULT_REGION")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_MAX_DELIVERY_ATTEMPTS", "30");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventGridConfig.builder()
                .defaultRegion("westeurope")
                .maxDeliveryAttempts(5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_DEFAULT_REGION", "westeurope")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_MAX_DELIVERY_ATTEMPTS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EventGridConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_GRID_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_GRID_DEFAULT_REGION")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_GRID_MAX_DELIVERY_ATTEMPTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EventGridConfig config = EventGridConfig.builder()
                .enabled(false)
                .defaultRegion("westeurope")
                .maxDeliveryAttempts(5)
                .build();
        EventGridConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultRegion()).contains("westeurope");
        assertThat(copy.getMaxDeliveryAttempts()).isEqualTo(5);
    }
}
