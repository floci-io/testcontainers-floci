package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Budgets-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BudgetsConfig config = BudgetsConfig.builder()
 *     .build();
 * }</pre>
 */
public class BudgetsConfig extends AbstractServiceConfig<BudgetsConfig.Builder> {

    private BudgetsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_BUDGETS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link BudgetsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BudgetsConfig> {

        private Builder() {
            // Allow instantiation only via BudgetsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BudgetsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BudgetsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link BudgetsConfig} from this builder.
         *
         * @return the Budgets configuration
         */
        @Override
        public BudgetsConfig build() {
            return new BudgetsConfig(this);
        }
    }
}
