package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Key Management (KMS) of Floci OCI.
 *
 * <p>KMS vaults, keys and cryptographic operations are served on the Floci OCI endpoint. The {@code managementEndpoint} and {@code cryptoEndpoint} returned for a vault are Floci OCI's own base URL ({@code http://localhost:4599}), so point the management and crypto clients at {@code FlociOciContainer#getEndpoint()} instead.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KmsConfig config = KmsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class KmsConfig extends AbstractServiceConfig<KmsConfig.Builder> {

    private KmsConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_KMS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link KmsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KmsConfig> {

        private Builder() {
            // Allow instantiation only via KmsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KmsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KmsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link KmsConfig} from this builder.
         *
         * @return the OCI Key Management (KMS) configuration
         */
        @Override
        public KmsConfig build() {
            return new KmsConfig(this);
        }
    }
}
