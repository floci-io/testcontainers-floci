package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Organizations (Tenant Manager control plane) of Floci OCI.
 *
 * <p>Floci OCI serves organizations, child tenancies and their links. A child tenancy gets its own home region,
 * which must be a region the parent tenancy is subscribed to; requests signed with the child tenancy see only its
 * own resources.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * OrganizationsConfig config = OrganizationsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class OrganizationsConfig extends AbstractServiceConfig<OrganizationsConfig.Builder> {

    private OrganizationsConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_ORGANIZATIONS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link OrganizationsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, OrganizationsConfig> {

        private Builder() {
            // Allow instantiation only via OrganizationsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link OrganizationsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(OrganizationsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link OrganizationsConfig} from this builder.
         *
         * @return the OCI Organizations configuration
         */
        @Override
        public OrganizationsConfig build() {
            return new OrganizationsConfig(this);
        }
    }
}
