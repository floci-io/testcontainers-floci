package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Control API-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudControlConfig config = CloudControlConfig.builder()
 *     .build();
 * }</pre>
 */
public class CloudControlConfig extends AbstractServiceConfig {

    private CloudControlConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudControlConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via CloudControlConfig.builder()
        }

        /**
         * Enables or disables the Cloud Control API service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Creates an immutable {@link CloudControlConfig} from this builder.
         *
         * @return the Cloud Control API configuration
         */
        public CloudControlConfig build() {
            return new CloudControlConfig(this);
        }
    }
}
