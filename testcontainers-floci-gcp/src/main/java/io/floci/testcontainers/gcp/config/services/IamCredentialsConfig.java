package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for IAM Service Account Credentials of Floci GCP.
 *
 * <p>The IAM Service Account Credentials API ({@code iamcredentials.googleapis.com}) issues access tokens,
 * ID tokens and signatures for service accounts.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IamCredentialsConfig config = IamCredentialsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class IamCredentialsConfig extends AbstractServiceConfig<IamCredentialsConfig.Builder> {

    private IamCredentialsConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_GCP_SERVICES_IAMCREDENTIALS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link IamCredentialsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IamCredentialsConfig> {

        private Builder() {
            // Allow instantiation only via IamCredentialsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IamCredentialsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IamCredentialsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link IamCredentialsConfig} from this builder.
         *
         * @return the IAM Service Account Credentials configuration
         */
        @Override
        public IamCredentialsConfig build() {
            return new IamCredentialsConfig(this);
        }
    }
}
