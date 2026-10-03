package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AWS Marketplace-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MarketplaceConfig config = MarketplaceConfig.builder()
 *     .build();
 * }</pre>
 */
public class MarketplaceConfig extends AbstractServiceConfig<MarketplaceConfig.Builder> {

    private MarketplaceConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_MARKETPLACE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link MarketplaceConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MarketplaceConfig> {

        private Builder() {
            // Allow instantiation only via MarketplaceConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MarketplaceConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MarketplaceConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link MarketplaceConfig} from this builder.
         *
         * @return the AWS Marketplace configuration
         */
        @Override
        public MarketplaceConfig build() {
            return new MarketplaceConfig(this);
        }
    }
}
