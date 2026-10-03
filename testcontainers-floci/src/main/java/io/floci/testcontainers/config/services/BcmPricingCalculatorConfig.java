package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for BCM Pricing Calculator-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BcmPricingCalculatorConfig config = BcmPricingCalculatorConfig.builder()
 *     .build();
 * }</pre>
 */
public class BcmPricingCalculatorConfig extends AbstractServiceConfig<BcmPricingCalculatorConfig.Builder> {

    private BcmPricingCalculatorConfig(Builder builder) {
        super(builder.enabled);
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BCM_PRICING_CALCULATOR_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link BcmPricingCalculatorConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BcmPricingCalculatorConfig> {

        private Builder() {
            // Allow instantiation only via BcmPricingCalculatorConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BcmPricingCalculatorConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BcmPricingCalculatorConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link BcmPricingCalculatorConfig} from this builder.
         *
         * @return the BCM Pricing Calculator configuration
         */
        @Override
        public BcmPricingCalculatorConfig build() {
            return new BcmPricingCalculatorConfig(this);
        }
    }
}
