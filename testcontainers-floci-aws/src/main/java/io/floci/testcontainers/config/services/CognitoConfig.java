package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cognito-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CognitoConfig config = CognitoConfig.builder()
 *     .enforceTokenValidityLimits(false)
 *     .build();
 * }</pre>
 */
public class CognitoConfig extends AbstractServiceConfig<CognitoConfig.Builder> {

    private static final boolean DEFAULT_ENFORCE_TOKEN_VALIDITY_LIMITS = true;
    private static final String DEFAULT_GOOGLE_ISSUER = "https://accounts.google.com";

    private final boolean enforceTokenValidityLimits;
    private final String googleIssuer;

    private CognitoConfig(Builder builder) {
        super(builder);
        this.enforceTokenValidityLimits = builder.enforceTokenValidityLimits;
        this.googleIssuer = builder.googleIssuer;
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
     * Returns whether app clients whose token validity is outside AWS's limits are rejected.
     *
     * @return {@code true} if the token validity limits are enforced
     */
    public boolean isEnforceTokenValidityLimits() {
        return enforceTokenValidityLimits;
    }

    /**
     * Returns the issuer of Google identity provider federation.
     *
     * <p>Override it to use a local OIDC provider for testing.
     *
     * @return the issuer of Google identity provider federation
     */
    public String getGoogleIssuer() {
        return googleIssuer;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_COGNITO_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_COGNITO_ENFORCE_TOKEN_VALIDITY_LIMITS", String.valueOf(enforceTokenValidityLimits));
            container.withEnv("FLOCI_SERVICES_COGNITO_GOOGLE_ISSUER", googleIssuer);
        }
    }

    /**
     * Builder for {@link CognitoConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CognitoConfig> {

        private boolean enforceTokenValidityLimits = DEFAULT_ENFORCE_TOKEN_VALIDITY_LIMITS;
        private String googleIssuer = DEFAULT_GOOGLE_ISSUER;

        private Builder() {
            // Allow instantiation only via CognitoConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CognitoConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CognitoConfig instance) {
            super(instance);
            this.enforceTokenValidityLimits = instance.isEnforceTokenValidityLimits();
            this.googleIssuer = instance.getGoogleIssuer();
        }

        /**
         * Sets whether app clients whose token validity is outside AWS's limits are rejected: access
         * and ID tokens between 5 minutes and 1 day, refresh tokens between 60 minutes and 3,650 days.
         * On by default, because a client that Floci accepts and AWS refuses only fails when it is
         * deployed. Turn it off to use tokens shorter than AWS allows, e.g. a one-minute access token
         * to test what happens when it expires.
         *
         * @param enforceTokenValidityLimits {@code true} to enforce the token validity limits (default {@value DEFAULT_ENFORCE_TOKEN_VALIDITY_LIMITS})
         * @return this builder
         */
        public Builder enforceTokenValidityLimits(boolean enforceTokenValidityLimits) {
            this.enforceTokenValidityLimits = enforceTokenValidityLimits;
            return this;
        }

        /**
         * Sets the issuer of Google identity provider federation.
         *
         * <p>Override it to use a local OIDC provider for testing.
         *
         * @param googleIssuer the issuer of Google identity provider federation (default {@value DEFAULT_GOOGLE_ISSUER})
         * @return this builder
         */
        public Builder googleIssuer(String googleIssuer) {
            this.googleIssuer = googleIssuer;
            return this;
        }

        /**
         * Creates an immutable {@link CognitoConfig} from this builder.
         *
         * @return the Cognito configuration
         */
        @Override
        public CognitoConfig build() {
            return new CognitoConfig(this);
        }
    }
}
