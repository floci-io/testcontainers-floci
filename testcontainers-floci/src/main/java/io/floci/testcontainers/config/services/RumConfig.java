package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudWatch RUM (Real User Monitoring)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RumConfig config = RumConfig.builder()
 *     .build();
 * }</pre>
 */
public class RumConfig extends AbstractServiceConfig<RumConfig.Builder> {

    private RumConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_RUM_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link RumConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RumConfig> {

        private Builder() {
            // Allow instantiation only via RumConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RumConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RumConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link RumConfig} from this builder.
         *
         * @return the RUM configuration
         */
        @Override
        public RumConfig build() {
            return new RumConfig(this);
        }
    }
}
