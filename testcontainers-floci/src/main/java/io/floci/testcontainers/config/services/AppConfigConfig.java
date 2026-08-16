package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AppConfig-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppConfigConfig config = AppConfigConfig.builder()
 *     .build();
 * }</pre>
 */
public class AppConfigConfig extends AbstractServiceConfig<AppConfigConfig.Builder> {


    private AppConfigConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_APPCONFIG_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AppConfigConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AppConfigConfig> {


        private Builder() {
            // Allow instantiation only via AppConfigConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AppConfigConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AppConfigConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AppConfigConfig} from this builder.
         *
         * @return the AppConfig configuration
         */
        @Override
        public AppConfigConfig build() {
            return new AppConfigConfig(this);
        }
    }
}
