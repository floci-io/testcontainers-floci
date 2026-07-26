package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AppConfig Data-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppConfigDataConfig config = AppConfigDataConfig.builder()
 *     .build();
 * }</pre>
 */
public class AppConfigDataConfig extends AbstractServiceConfig<AppConfigDataConfig.Builder> {


    private AppConfigDataConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_APPCONFIGDATA_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AppConfigDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AppConfigDataConfig> {


        private Builder() {
            // Allow instantiation only via AppConfigDataConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AppConfigDataConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AppConfigDataConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AppConfigDataConfig} from this builder.
         *
         * @return the AppConfig Data configuration
         */
        public AppConfigDataConfig build() {
            return new AppConfigDataConfig(this);
        }
    }
}
