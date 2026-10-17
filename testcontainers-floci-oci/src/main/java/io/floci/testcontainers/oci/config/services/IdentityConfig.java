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
 *     .strictCompartments(true)
 *     .build();
 * }</pre>
 */
public class IdentityConfig extends AbstractServiceConfig<IdentityConfig.Builder> {

    private static final boolean DEFAULT_STRICT_COMPARTMENTS = false;

    private final boolean strictCompartments;

    private IdentityConfig(Builder builder) {
        super(builder);
        this.strictCompartments = builder.strictCompartments;
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

    /**
     * Returns whether other services reject requests that reference an unknown or non-{@code ACTIVE} compartment.
     *
     * @return {@code true} if compartments are validated strictly
     */
    public boolean isStrictCompartments() {
        return strictCompartments;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_OCI_SERVICES_IDENTITY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_OCI_SERVICES_IDENTITY_STRICT_COMPARTMENTS", String.valueOf(strictCompartments));
        }
    }

    /**
     * Builder for {@link IdentityConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IdentityConfig> {

        private boolean strictCompartments = DEFAULT_STRICT_COMPARTMENTS;

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
            this.strictCompartments = instance.strictCompartments;
        }

        /**
         * Sets whether other services reject requests that reference an unknown or non-{@code ACTIVE} compartment.
         * When disabled, any compartment OCID is accepted.
         *
         * @param strictCompartments {@code true} to validate compartments strictly (default {@value
         *                           DEFAULT_STRICT_COMPARTMENTS})
         * @return this builder
         */
        public Builder strictCompartments(boolean strictCompartments) {
            this.strictCompartments = strictCompartments;
            return this;
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
