package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cost Explorer-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CostExplorerConfig config = CostExplorerConfig.builder()
 *     .creditUsdMonthly(100.0)
 *     .build();
 * }</pre>
 */
public class CostExplorerConfig extends AbstractServiceConfig<CostExplorerConfig.Builder> {

    private static final double DEFAULT_CREDIT_USD_MONTHLY = 0.0;

    private final double creditUsdMonthly;

    private CostExplorerConfig(Builder builder) {
        super(builder.enabled);
        this.creditUsdMonthly = builder.creditUsdMonthly;
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

    /**
     * Returns the synthetic monthly USD credit applied as a {@code Credit} {@code RECORD_TYPE} row
     * in {@code GetCostAndUsage} responses.
     *
     * @return the monthly credit in USD (default {@value DEFAULT_CREDIT_USD_MONTHLY})
     */
    public double getCreditUsdMonthly() {
        return creditUsdMonthly;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CE_CREDIT_USD_MONTHLY", String.valueOf(creditUsdMonthly));
        }
    }

    /**
     * Builder for {@link CostExplorerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CostExplorerConfig> {

        private double creditUsdMonthly = DEFAULT_CREDIT_USD_MONTHLY;

        private Builder() {
            // Allow instantiation only via CostExplorerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CostExplorerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CostExplorerConfig instance) {
            super(instance);
            this.creditUsdMonthly = instance.getCreditUsdMonthly();
        }

        /**
         * Sets the synthetic monthly USD credit applied as a {@code Credit} {@code RECORD_TYPE} row
         * in {@code GetCostAndUsage} responses. The emitted credit is capped at the synthesized
         * monthly usage so net cost never goes below zero.
         *
         * @param creditUsdMonthly the monthly credit in USD (default {@value DEFAULT_CREDIT_USD_MONTHLY})
         * @return this builder
         */
        public Builder creditUsdMonthly(double creditUsdMonthly) {
            this.creditUsdMonthly = creditUsdMonthly;
            return this;
        }

        /**
         * Creates an immutable {@link CostExplorerConfig} from this builder.
         *
         * @return the Cost Explorer configuration
         */
        @Override
        public CostExplorerConfig build() {
            return new CostExplorerConfig(this);
        }
    }
}
