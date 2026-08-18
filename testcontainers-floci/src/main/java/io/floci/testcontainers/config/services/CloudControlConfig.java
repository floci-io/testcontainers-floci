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
public class CloudControlConfig extends AbstractServiceConfig<CloudControlConfig.Builder> {

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
        container.withEnv("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudControlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudControlConfig> {


        private Builder() {
            // Allow instantiation only via CloudControlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudControlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudControlConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudControlConfig} from this builder.
         *
         * @return the Cloud Control API configuration
         */
        @Override
        public CloudControlConfig build() {
            return new CloudControlConfig(this);
        }
    }
}
