package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MarketplaceConfigTest {

    @Test
    void shouldApplyDefaultMarketplaceConfig() {
        MarketplaceConfig config = MarketplaceConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomMarketplaceConfig() {
        MarketplaceConfig config = MarketplaceConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MarketplaceConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_MARKETPLACE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MarketplaceConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_MARKETPLACE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MarketplaceConfig config = MarketplaceConfig.builder()
                .enabled(false)
                .build();
        MarketplaceConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
