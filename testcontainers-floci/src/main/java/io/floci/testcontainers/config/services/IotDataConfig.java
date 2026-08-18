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
public class IotDataConfig extends AbstractServiceConfig<IotDataConfig.Builder> {

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
        container.withEnv("FLOCI_SERVICES_IOTDATA_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link IotDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IotDataConfig> {


        private Builder() {
            // Allow instantiation only via IotDataConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IotDataConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IotDataConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link IotDataConfig} from this builder.
         *
         * @return the IoT Data Plane configuration
         */
        @Override
        public IotDataConfig build() {
            return new IotDataConfig(this);
        }
    }
}
