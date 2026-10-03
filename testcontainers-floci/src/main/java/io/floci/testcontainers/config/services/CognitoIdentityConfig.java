package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cognito Identity-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CognitoIdentityConfig config = CognitoIdentityConfig.builder()
 *     .build();
 * }</pre>
 */
public class CognitoIdentityConfig extends AbstractServiceConfig<CognitoIdentityConfig.Builder> {

    private CognitoIdentityConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_COGNITOIDENTITY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CognitoIdentityConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CognitoIdentityConfig> {

        private Builder() {
            // Allow instantiation only via CognitoIdentityConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CognitoIdentityConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CognitoIdentityConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CognitoIdentityConfig} from this builder.
         *
         * @return the Cognito Identity configuration
         */
        @Override
        public CognitoIdentityConfig build() {
            return new CognitoIdentityConfig(this);
        }
    }
}
