package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for IoT Data Plane-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IotDataConfig config = IotDataConfig.builder()
 *     .build();
 * }</pre>
 */
public class IotDataConfig extends AbstractServiceConfig {

    private IotDataConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_IOTDATA_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link IotDataConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via IotDataConfig.builder()
        }

        /**
         * Enables or disables the IoT Data Plane service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Creates an immutable {@link IotDataConfig} from this builder.
         *
         * @return the IoT Data Plane configuration
         */
        public IotDataConfig build() {
            return new IotDataConfig(this);
        }
    }
}
