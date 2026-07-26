package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AWS Config-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ConfigServiceConfig config = ConfigServiceConfig.builder()
 *     .enabled(true)
 *     .build();
 * }</pre>
 */
public class ConfigServiceConfig extends AbstractServiceConfig<ConfigServiceConfig.Builder> {

    private ConfigServiceConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CONFIGSERVICE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ConfigServiceConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ConfigServiceConfig> {


        private Builder() {
            // Allow instantiation only via ConfigServiceConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ConfigServiceConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ConfigServiceConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ConfigServiceConfig} from this builder.
         *
         * @return the AWS Config configuration
         */
        public ConfigServiceConfig build() {
            return new ConfigServiceConfig(this);
        }
    }
}
