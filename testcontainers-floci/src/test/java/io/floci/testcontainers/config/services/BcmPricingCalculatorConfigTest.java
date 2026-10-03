package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BcmPricingCalculatorConfigTest {

    @Test
    void shouldApplyDefaultBcmPricingCalculatorConfig() {
        BcmPricingCalculatorConfig config = BcmPricingCalculatorConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomBcmPricingCalculatorConfig() {
        BcmPricingCalculatorConfig config = BcmPricingCalculatorConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BcmPricingCalculatorConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BCM_PRICING_CALCULATOR_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BcmPricingCalculatorConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BCM_PRICING_CALCULATOR_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BcmPricingCalculatorConfig config = BcmPricingCalculatorConfig.builder()
                .enabled(false)
                .build();
        BcmPricingCalculatorConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
