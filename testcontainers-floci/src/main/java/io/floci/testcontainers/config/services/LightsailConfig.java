package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Lightsail-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * LightsailConfig config = LightsailConfig.builder()
 *     .build();
 * }</pre>
 */
public class LightsailConfig extends AbstractServiceConfig<LightsailConfig.Builder> {

    private LightsailConfig(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_LIGHTSAIL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link LightsailConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, LightsailConfig> {


        private Builder() {
            // Allow instantiation only via LightsailConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link LightsailConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(LightsailConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link LightsailConfig} from this builder.
         *
         * @return the Lightsail configuration
         */
        public LightsailConfig build() {
            return new LightsailConfig(this);
        }
    }
}
