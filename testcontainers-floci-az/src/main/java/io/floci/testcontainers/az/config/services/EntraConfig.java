package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Microsoft Entra ID of Floci Azure.
 *
 * <p>A local OpenID Connect provider that issues signed RS256 tokens and serves JWKS and discovery
 * documents.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EntraConfig config = EntraConfig.builder()
 *     .defaultTenantId("11111111-1111-1111-1111-111111111111")
 *     .issuer("https://login.example.com/tenant/v2.0")
 *     .tokenLifetimeSeconds(600L)
 *     .validateClientCredentials(true)
 *     .build();
 * }</pre>
 */
public class EntraConfig extends AbstractServiceConfig<EntraConfig.Builder> {

    private static final String DEFAULT_DEFAULT_TENANT_ID = "00000000-0000-0000-0000-000000000002";
    private static final long DEFAULT_TOKEN_LIFETIME_SECONDS = 3599L;
    private static final boolean DEFAULT_VALIDATE_TOKENS = false;
    private static final boolean DEFAULT_VALIDATE_CLIENT_CREDENTIALS = false;

    private final String defaultTenantId;
    private final String issuer;
    private final long tokenLifetimeSeconds;
    private final boolean validateTokens;
    private final boolean validateClientCredentials;
    private final String signingKeyPath;

    private EntraConfig(Builder builder) {
        super(builder);
        this.defaultTenantId = builder.defaultTenantId;
        this.issuer = builder.issuer;
        this.tokenLifetimeSeconds = builder.tokenLifetimeSeconds;
        this.validateTokens = builder.validateTokens;
        this.validateClientCredentials = builder.validateClientCredentials;
        this.signingKeyPath = builder.signingKeyPath;
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
     * Returns the tenant id returned in the {@code tid} claim and used as default issuer tenant.
     *
     * @return the default tenant id
     */
    public String getDefaultTenantId() {
        return defaultTenantId;
    }

    /**
     * Returns the token issuer ({@code iss}) override. When empty, the issuer is derived from
     * the request base URL as {@code {baseUrl}/{tenantId}/v2.0}.
     *
     * @return the the issuer override, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getIssuer() {
        return Optional.ofNullable(issuer);
    }

    /**
     * Returns the access token lifetime in seconds ({@code expires_in} / {@code exp}).
     *
     * @return the token lifetime in seconds
     */
    public long getTokenLifetimeSeconds() {
        return tokenLifetimeSeconds;
    }

    /**
     * Returns whether bearer tokens are validated (signature and claims) against the local
     * signing key.
     *
     * @return {@code true} if tokens are validated
     */
    public boolean isValidateTokens() {
        return validateTokens;
    }

    /**
     * Returns whether the token endpoint checks the client against the directory: an unregistered
     * {@code client_id} gets {@code 400 unauthorized_client} ({@code AADSTS700016}) and a wrong
     * secret {@code 401 invalid_client} ({@code AADSTS7000215}).
     *
     * @return {@code true} if client credentials are validated
     */
    public boolean isValidateClientCredentials() {
        return validateClientCredentials;
    }

    /**
     * Returns the directory inside the container holding the persisted RSA signing key. When
     * empty, floci-az uses {@code {storage.persistent-path}/entra}.
     *
     * @return the the signing key directory, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getSigningKeyPath() {
        return Optional.ofNullable(signingKeyPath);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_ENTRA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_DEFAULT_TENANT_ID", defaultTenantId);
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_TOKEN_LIFETIME_SECONDS", String.valueOf(tokenLifetimeSeconds));
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS", String.valueOf(validateTokens));
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_CLIENT_CREDENTIALS", String.valueOf(validateClientCredentials));

            if (issuer != null) {
                container.withEnv("FLOCI_AZ_SERVICES_ENTRA_ISSUER", issuer);
            }

            if (signingKeyPath != null) {
                container.withEnv("FLOCI_AZ_SERVICES_ENTRA_SIGNING_KEY_PATH", signingKeyPath);
            }
        }
    }

    /**
     * Builder for {@link EntraConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EntraConfig> {

        private String defaultTenantId = DEFAULT_DEFAULT_TENANT_ID;
        private String issuer;
        private long tokenLifetimeSeconds = DEFAULT_TOKEN_LIFETIME_SECONDS;
        private boolean validateTokens = DEFAULT_VALIDATE_TOKENS;
        private boolean validateClientCredentials = DEFAULT_VALIDATE_CLIENT_CREDENTIALS;
        private String signingKeyPath;

        private Builder() {
            // Allow instantiation only via EntraConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EntraConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EntraConfig instance) {
            super(instance);
            this.defaultTenantId = instance.defaultTenantId;
            this.issuer = instance.issuer;
            this.tokenLifetimeSeconds = instance.tokenLifetimeSeconds;
            this.validateTokens = instance.validateTokens;
            this.validateClientCredentials = instance.validateClientCredentials;
            this.signingKeyPath = instance.signingKeyPath;
        }

        /**
         * Sets the tenant id returned in the {@code tid} claim and used as default issuer tenant.
         *
         * @param defaultTenantId the default tenant id (default {@value DEFAULT_DEFAULT_TENANT_ID})
         * @return this builder
         */
        public Builder defaultTenantId(String defaultTenantId) {
            this.defaultTenantId = defaultTenantId;
            return this;
        }

        /**
         * Overrides the token issuer ({@code iss}). When not set, the issuer is derived from the
         * request base URL as {@code {baseUrl}/{tenantId}/v2.0}.
         *
         * @param issuer the issuer ({@code null} to unset)
         * @return this builder
         */
        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        /**
         * Sets the access token lifetime in seconds ({@code expires_in} / {@code exp}).
         *
         * @param tokenLifetimeSeconds the token lifetime in seconds (default {@value DEFAULT_TOKEN_LIFETIME_SECONDS})
         * @return this builder
         */
        public Builder tokenLifetimeSeconds(long tokenLifetimeSeconds) {
            this.tokenLifetimeSeconds = tokenLifetimeSeconds;
            return this;
        }

        /**
         * Sets whether bearer tokens are validated (signature and claims) against the local signing
         * key. By default any bearer token is accepted.
         *
         * @param validateTokens {@code true} to validate tokens (default {@value DEFAULT_VALIDATE_TOKENS})
         * @return this builder
         */
        public Builder validateTokens(boolean validateTokens) {
            this.validateTokens = validateTokens;
            return this;
        }

        /**
         * Sets whether the token endpoint checks the client against the directory: an unregistered
         * {@code client_id} gets {@code 400 unauthorized_client} ({@code AADSTS700016}) and a wrong
         * secret {@code 401 invalid_client} ({@code AADSTS7000215}). By default any client id and secret are accepted.
         *
         * @param validateClientCredentials {@code true} to validate client credentials
         *                                  (default {@value DEFAULT_VALIDATE_CLIENT_CREDENTIALS})
         * @return this builder
         */
        public Builder validateClientCredentials(boolean validateClientCredentials) {
            this.validateClientCredentials = validateClientCredentials;
            return this;
        }

        /**
         * Sets the directory inside the container holding the persisted RSA signing key. When not
         * set, floci-az uses {@code {storage.persistent-path}/entra}.
         *
         * @param signingKeyPath the signing key directory ({@code null} to unset)
         * @return this builder
         */
        public Builder signingKeyPath(String signingKeyPath) {
            this.signingKeyPath = signingKeyPath;
            return this;
        }

        /**
         * Creates an immutable {@link EntraConfig} from this builder.
         *
         * @return the Entra ID configuration
         */
        @Override
        public EntraConfig build() {
            return new EntraConfig(this);
        }
    }
}
