package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Secret Manager of Floci GCP.
 *
 * <p>Secret Manager is served via gRPC and REST on the main port.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SecretManagerConfig config = SecretManagerConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class SecretManagerConfig extends AbstractServiceConfig<SecretManagerConfig.Builder> {

    private SecretManagerConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_SECRETMANAGER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link SecretManagerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SecretManagerConfig> {

        private Builder() {
            // Allow instantiation only via SecretManagerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SecretManagerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SecretManagerConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link SecretManagerConfig} from this builder.
         *
         * @return the Secret Manager configuration
         */
        @Override
        public SecretManagerConfig build() {
            return new SecretManagerConfig(this);
        }
    }
}
