package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AppIntegrations-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppIntegrationsConfig config = AppIntegrationsConfig.builder()
 *     .build();
 * }</pre>
 */
public class AppIntegrationsConfig extends AbstractServiceConfig<AppIntegrationsConfig.Builder> {

    private AppIntegrationsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_APPINTEGRATIONS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AppIntegrationsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AppIntegrationsConfig> {

        private Builder() {
            // Allow instantiation only via AppIntegrationsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AppIntegrationsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AppIntegrationsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AppIntegrationsConfig} from this builder.
         *
         * @return the AppIntegrations configuration
         */
        @Override
        public AppIntegrationsConfig build() {
            return new AppIntegrationsConfig(this);
        }
    }
}
