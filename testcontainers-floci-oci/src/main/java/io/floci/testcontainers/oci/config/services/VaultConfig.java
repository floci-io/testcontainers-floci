package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Vault secrets of Floci OCI.
 *
 * <p>Secrets (Vault service) and secret bundles (Secrets service) are served on the Floci OCI endpoint. Secrets are encrypted with keys of the KMS service (see {@link KmsConfig}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * VaultConfig config = VaultConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class VaultConfig extends AbstractServiceConfig<VaultConfig.Builder> {

    private VaultConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_VAULT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link VaultConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, VaultConfig> {

        private Builder() {
            // Allow instantiation only via VaultConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link VaultConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(VaultConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link VaultConfig} from this builder.
         *
         * @return the OCI Vault secrets configuration
         */
        @Override
        public VaultConfig build() {
            return new VaultConfig(this);
        }
    }
}
