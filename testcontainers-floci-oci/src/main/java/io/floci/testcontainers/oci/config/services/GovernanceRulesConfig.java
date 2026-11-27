package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Governance Rules of Floci OCI.
 *
 * <p>A parent tenancy defines rules (quotas, tags or allowed regions) for the child tenancies of its organization
 * (see {@link OrganizationsConfig}); inclusion criteria pick the child tenancies a rule applies to. All rule types are stored;
 * only {@code ALLOWED_REGIONS} is enforced: Identity refuses a region subscription of an attached child tenancy to a
 * region the rule does not allow.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GovernanceRulesConfig config = GovernanceRulesConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class GovernanceRulesConfig extends AbstractServiceConfig<GovernanceRulesConfig.Builder> {

    private GovernanceRulesConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_GOVERNANCERULES_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link GovernanceRulesConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GovernanceRulesConfig> {

        private Builder() {
            // Allow instantiation only via GovernanceRulesConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GovernanceRulesConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GovernanceRulesConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link GovernanceRulesConfig} from this builder.
         *
         * @return the OCI Governance Rules configuration
         */
        @Override
        public GovernanceRulesConfig build() {
            return new GovernanceRulesConfig(this);
        }
    }
}
