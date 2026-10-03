package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Security Hub-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SecurityHubConfig config = SecurityHubConfig.builder()
 *     .build();
 * }</pre>
 */
public class SecurityHubConfig extends AbstractServiceConfig<SecurityHubConfig.Builder> {

    private SecurityHubConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_SECURITYHUB_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link SecurityHubConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SecurityHubConfig> {

        private Builder() {
            // Allow instantiation only via SecurityHubConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SecurityHubConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SecurityHubConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link SecurityHubConfig} from this builder.
         *
         * @return the Security Hub configuration
         */
        @Override
        public SecurityHubConfig build() {
            return new SecurityHubConfig(this);
        }
    }
}
