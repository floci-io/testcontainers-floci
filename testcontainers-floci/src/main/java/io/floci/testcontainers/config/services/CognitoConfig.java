package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cognito-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CognitoConfig config = CognitoConfig.builder()
 *     .build();
 * }</pre>
 */
public class CognitoConfig extends AbstractServiceConfig<CognitoConfig.Builder> {


    private CognitoConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_COGNITO_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CognitoConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CognitoConfig> {


        private Builder() {
            // Allow instantiation only via CognitoConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CognitoConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CognitoConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CognitoConfig} from this builder.
         *
         * @return the Cognito configuration
         */
        public CognitoConfig build() {
            return new CognitoConfig(this);
        }
    }
}
