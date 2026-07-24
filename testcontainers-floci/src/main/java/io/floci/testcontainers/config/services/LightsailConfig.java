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
public class LightsailConfig extends AbstractServiceConfig {

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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_LIGHTSAIL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link LightsailConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via LightsailConfig.builder()
        }

        /**
         * Enables or disables the Lightsail service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
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
