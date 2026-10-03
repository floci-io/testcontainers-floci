package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for SSO OIDC (IAM Identity Center OIDC)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SsoOidcConfig config = SsoOidcConfig.builder()
 *     .build();
 * }</pre>
 */
public class SsoOidcConfig extends AbstractServiceConfig<SsoOidcConfig.Builder> {

    private final String localPrincipalId;

    private SsoOidcConfig(Builder builder) {
        super(builder.enabled);
        this.localPrincipalId = builder.localPrincipalId;
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
     * Returns the Identity Store principal the local authorization helpers (device and authorization code
     * flows) are bound to.
     *
     * <p>Floci never trusts a caller-selected identity: a mismatched {@code principal_id} is rejected, and
     * without a local principal the resulting OIDC session is not associated with an Identity Center portal
     * identity.
     *
     * @return the Identity Store principal the local authorization helpers (device and authorization code flows) are bound to, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getLocalPrincipalId() {
        return Optional.ofNullable(localPrincipalId);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SSOOIDC_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (localPrincipalId != null) {
                container.withEnv("FLOCI_SERVICES_SSOOIDC_LOCAL_PRINCIPAL_ID", localPrincipalId);
            }
        }
    }

    /**
     * Builder for {@link SsoOidcConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SsoOidcConfig> {

        private String localPrincipalId;

        private Builder() {
            // Allow instantiation only via SsoOidcConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SsoOidcConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SsoOidcConfig instance) {
            super(instance);
            this.localPrincipalId = instance.getLocalPrincipalId().orElse(null);
        }

        /**
         * Sets the Identity Store principal the local authorization helpers (device and authorization code
         * flows) are bound to.
         *
         * <p>Floci never trusts a caller-selected identity: a mismatched {@code principal_id} is rejected,
         * and without a local principal the resulting OIDC session is not associated with an Identity Center
         * portal identity.
         *
         * @param localPrincipalId the Identity Store principal the local authorization helpers (device and authorization code flows) are bound to, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder localPrincipalId(String localPrincipalId) {
            this.localPrincipalId = localPrincipalId;
            return this;
        }

        /**
         * Creates an immutable {@link SsoOidcConfig} from this builder.
         *
         * @return the SSO OIDC (IAM Identity Center OIDC) configuration
         */
        @Override
        public SsoOidcConfig build() {
            return new SsoOidcConfig(this);
        }
    }
}
