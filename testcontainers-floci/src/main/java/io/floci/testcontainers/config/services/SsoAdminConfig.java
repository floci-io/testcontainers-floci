package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for SSO Admin (IAM Identity Center)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SsoAdminConfig config = SsoAdminConfig.builder()
 *     .build();
 * }</pre>
 */
public class SsoAdminConfig extends AbstractServiceConfig<SsoAdminConfig.Builder> {

    private SsoAdminConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_SSOADMIN_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link SsoAdminConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SsoAdminConfig> {

        private Builder() {
            // Allow instantiation only via SsoAdminConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SsoAdminConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SsoAdminConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link SsoAdminConfig} from this builder.
         *
         * @return the SSO Admin (IAM Identity Center) configuration
         */
        @Override
        public SsoAdminConfig build() {
            return new SsoAdminConfig(this);
        }
    }
}
