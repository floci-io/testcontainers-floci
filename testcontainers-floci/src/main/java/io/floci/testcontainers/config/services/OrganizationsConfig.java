package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Organizations-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * OrganizationsConfig config = OrganizationsConfig.builder()
 *     .scpEnforcementEnabled(true)
 *     .managementAccountEmail("root@example.com")
 *     .build();
 * }</pre>
 */
public class OrganizationsConfig extends AbstractServiceConfig<OrganizationsConfig.Builder> {

    private static final boolean DEFAULT_SCP_ENFORCEMENT_ENABLED = false;

    private final boolean scpEnforcementEnabled;
    private final String managementAccountEmail;

    private OrganizationsConfig(Builder builder) {
        super(builder.enabled);
        this.scpEnforcementEnabled = builder.scpEnforcementEnabled;
        this.managementAccountEmail = builder.managementAccountEmail;
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
     * Returns whether service control policy (SCP) enforcement is enabled.
     *
     * @return {@code true} if SCP enforcement is enabled
     */
    public boolean isScpEnforcementEnabled() {
        return scpEnforcementEnabled;
    }

    /**
     * Returns the email address of the organization's management account, or {@code null} if not set.
     *
     * @return the management account email, or {@code null}
     */
    public String getManagementAccountEmail() {
        return managementAccountEmail;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ORGANIZATIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ORGANIZATIONS_SCP_ENFORCEMENT_ENABLED",
                    String.valueOf(scpEnforcementEnabled));

            if (managementAccountEmail != null) {
                container.withEnv("FLOCI_SERVICES_ORGANIZATIONS_MANAGEMENT_ACCOUNT_EMAIL", managementAccountEmail);
            }
        }
    }

    /**
     * Builder for {@link OrganizationsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, OrganizationsConfig> {

        private boolean scpEnforcementEnabled = DEFAULT_SCP_ENFORCEMENT_ENABLED;
        private String managementAccountEmail;

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
            this.scpEnforcementEnabled = instance.isScpEnforcementEnabled();
            this.managementAccountEmail = instance.getManagementAccountEmail();
        }

        /**
         * Sets whether service control policy (SCP) enforcement is enabled.
         *
         * @param scpEnforcementEnabled {@code true} to enable SCP enforcement
         *                              (default {@value DEFAULT_SCP_ENFORCEMENT_ENABLED})
         * @return this builder
         */
        public Builder scpEnforcementEnabled(boolean scpEnforcementEnabled) {
            this.scpEnforcementEnabled = scpEnforcementEnabled;
            return this;
        }

        /**
         * Sets the email address of the organization's management account.
         *
         * @param managementAccountEmail the management account email, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder managementAccountEmail(String managementAccountEmail) {
            this.managementAccountEmail = managementAccountEmail;
            return this;
        }

        /**
         * Creates an immutable {@link OrganizationsConfig} from this builder.
         *
         * @return the Organizations configuration
         */
        @Override
        public OrganizationsConfig build() {
            return new OrganizationsConfig(this);
        }
    }
}
