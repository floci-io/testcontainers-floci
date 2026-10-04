package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Identity and Access Management (IAM) of Floci OCI.
 *
 * <p>Compartments, users, groups and policies of the emulated tenancy are served on the Floci OCI endpoint.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IdentityConfig config = IdentityConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class IdentityConfig extends AbstractServiceConfig<IdentityConfig.Builder> {

    private IdentityConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_IDENTITY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link IdentityConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IdentityConfig> {

        private Builder() {
            // Allow instantiation only via IdentityConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IdentityConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IdentityConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link IdentityConfig} from this builder.
         *
         * @return the OCI Identity and Access Management (IAM) configuration
         */
        @Override
        public IdentityConfig build() {
            return new IdentityConfig(this);
        }
    }
}
