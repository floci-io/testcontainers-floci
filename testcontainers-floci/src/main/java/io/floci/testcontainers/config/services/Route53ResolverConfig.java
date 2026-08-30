package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Route 53 Resolver-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Route53ResolverConfig config = Route53ResolverConfig.builder()
 *     .build();
 * }</pre>
 */
public class Route53ResolverConfig extends AbstractServiceConfig<Route53ResolverConfig.Builder> {

    private Route53ResolverConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_ROUTE53RESOLVER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link Route53ResolverConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Route53ResolverConfig> {

        private Builder() {
            // Allow instantiation only via Route53ResolverConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Route53ResolverConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Route53ResolverConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link Route53ResolverConfig} from this builder.
         *
         * @return the Route 53 Resolver configuration
         */
        @Override
        public Route53ResolverConfig build() {
            return new Route53ResolverConfig(this);
        }
    }
}
