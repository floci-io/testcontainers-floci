package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;
import org.testcontainers.images.builder.Transferable;

import java.util.Optional;
import java.util.UUID;

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
 *     .validateUserCredentials(true)
 *     .seed("{\"users\": [{\"id\": \"u1\", \"userPrincipalName\": \"reader@example.test\"}]}")
 *     .build();
 * }</pre>
 */
public class EntraConfig extends AbstractServiceConfig<EntraConfig.Builder> {

    private static final String DEFAULT_DEFAULT_TENANT_ID = "00000000-0000-0000-0000-000000000002";
    private static final long DEFAULT_TOKEN_LIFETIME_SECONDS = 3599L;
    private static final boolean DEFAULT_VALIDATE_TOKENS = false;
    private static final boolean DEFAULT_VALIDATE_CLIENT_CREDENTIALS = false;
    private static final boolean DEFAULT_VALIDATE_USER_CREDENTIALS = false;
    private static final String SEED_FILE_PREFIX = "/tmp/floci-az-entra-seed-";
    private static final String SEED_FILE_SUFFIX = ".json";

    private final String defaultTenantId;
    private final String issuer;
    private final long tokenLifetimeSeconds;
    private final boolean validateTokens;
    private final boolean validateClientCredentials;
    private final boolean validateUserCredentials;
    private final String signingKeyPath;
    private final String seedFile;
    private final String seed;

    private EntraConfig(Builder builder) {
        super(builder);
        this.defaultTenantId = builder.defaultTenantId;
        this.issuer = builder.issuer;
        this.tokenLifetimeSeconds = builder.tokenLifetimeSeconds;
        this.validateTokens = builder.validateTokens;
        this.validateClientCredentials = builder.validateClientCredentials;
        this.validateUserCredentials = builder.validateUserCredentials;
        this.signingKeyPath = builder.signingKeyPath;
        this.seedFile = builder.seedFile;
        this.seed = builder.seed;
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
     * Returns whether ARM requests whose Bearer token is missing, not signed by the Entra signing key,
     * expired, or issued for another audience are refused with {@code 401}. This is another way to turn
     * on the strict-tokens parity check; an explicit
     * {@link io.floci.testcontainers.az.config.ParityConfig#getStrictTokens() strictTokens} override wins.
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
     * Returns whether password and refresh-token grants are checked against the directory: an unknown
     * user or a wrong password gets {@code 400 invalid_grant} ({@code AADSTS50126}), a disabled account
     * {@code AADSTS50057}. Independent of {@link #isValidateClientCredentials()}.
     *
     * @return {@code true} if user credentials are validated
     */
    public boolean isValidateUserCredentials() {
        return validateUserCredentials;
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

    /**
     * Returns the path, inside the container, of the JSON seed file that adds applications, users,
     * groups and memberships to the directory at startup.
     *
     * <p>The path is either the one passed to {@link Builder#seedFile(String)} verbatim, or a generated
     * path pointing at the file whose content was passed to {@link Builder#seed(String)}.
     *
     * @return the container path of the seed file, or {@link Optional#empty()} if none is configured
     */
    public Optional<String> getSeedFile() {
        return Optional.ofNullable(seedFile);
    }

    /**
     * Returns the raw seed file content supplied via {@link Builder#seed(String)}, if any. When present,
     * this content is copied into the container at {@link #getSeedFile()}.
     *
     * @return the seed content, or {@link Optional#empty()} if the seed was not configured by content
     */
    public Optional<String> getSeed() {
        return Optional.ofNullable(seed);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_ENTRA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_DEFAULT_TENANT_ID", defaultTenantId);
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_TOKEN_LIFETIME_SECONDS", String.valueOf(tokenLifetimeSeconds));
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS", String.valueOf(validateTokens));
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_CLIENT_CREDENTIALS", String.valueOf(validateClientCredentials));
            container.withEnv("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_USER_CREDENTIALS", String.valueOf(validateUserCredentials));

            if (issuer != null) {
                container.withEnv("FLOCI_AZ_SERVICES_ENTRA_ISSUER", issuer);
            }

            if (signingKeyPath != null) {
                container.withEnv("FLOCI_AZ_SERVICES_ENTRA_SIGNING_KEY_PATH", signingKeyPath);
            }

            if (seedFile != null) {
                container.withEnv("FLOCI_AZ_SERVICES_ENTRA_SEED_FILE", seedFile);
            }
        }
    }

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && seedFile != null && seed != null) {
            container.withCopyToContainer(Transferable.of(seed), seedFile);
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
        private boolean validateUserCredentials = DEFAULT_VALIDATE_USER_CREDENTIALS;
        private String signingKeyPath;
        private String seedFile;
        private String seed;

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
            this.validateUserCredentials = instance.validateUserCredentials;
            this.signingKeyPath = instance.signingKeyPath;
            this.seedFile = instance.seedFile;
            this.seed = instance.seed;
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
         * Sets whether ARM requests whose Bearer token is missing, not signed by the Entra signing key,
         * expired, or issued for another audience are refused with {@code 401}. This is another way to
         * turn on the strict-tokens parity check (on from the {@code STRICT} parity level); an explicit
         * {@link io.floci.testcontainers.az.config.ParityConfig.Builder#strictTokens(Boolean) strictTokens}
         * parity override wins. By default any bearer token is accepted.
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
         * Sets whether password and refresh-token grants are checked against the directory: an unknown
         * user or a wrong password gets {@code 400 invalid_grant} ({@code AADSTS50126}), a disabled
         * account {@code AADSTS50057}. Independent of {@link #validateClientCredentials(boolean)}. By
         * default any user name and password are accepted.
         *
         * @param validateUserCredentials {@code true} to validate user credentials
         *                                (default {@value DEFAULT_VALIDATE_USER_CREDENTIALS})
         * @return this builder
         */
        public Builder validateUserCredentials(boolean validateUserCredentials) {
            this.validateUserCredentials = validateUserCredentials;
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
         * Sets the path, inside the container, of a JSON seed file that already exists in the container
         * (for example one added through a volume or another {@code withCopy*} call). At startup, Floci
         * Azure adds the file's applications, users, groups and memberships to the directory; existing
         * objects are not overwritten. An invalid file fails startup.
         *
         * <p>Use {@link #seed(String)} instead to hand over just the file content and let
         * {@link EntraConfig} take care of placing the file into the container.
         *
         * <p>Calling this method clears any content previously set via {@link #seed(String)}.
         *
         * @param seedFile the container path of the seed file, or {@code null} for none
         * @return this builder
         */
        public Builder seedFile(String seedFile) {
            this.seedFile = seedFile;
            this.seed = null;
            return this;
        }

        /**
         * Sets the content of a JSON seed file ({@code applications}, {@code users} and {@code groups}
         * arrays) that Floci Azure adds to the directory at startup; existing objects are not
         * overwritten. An invalid file fails startup.
         *
         * <p>The content is copied into the container under a generated, randomized path, which is then
         * used as {@link EntraConfig#getSeedFile()}. Callers therefore do not need to manage any files
         * themselves.
         *
         * <p>Calling this method clears any path previously set via {@link #seedFile(String)}.
         *
         * @param seed the seed file content, or {@code null} for none
         * @return this builder
         */
        public Builder seed(String seed) {
            this.seed = seed;
            this.seedFile = null;
            return this;
        }

        /**
         * Creates an immutable {@link EntraConfig} from this builder.
         *
         * @return the Entra ID configuration
         */
        @Override
        public EntraConfig build() {
            if (seed != null && seedFile == null) {
                this.seedFile = SEED_FILE_PREFIX + UUID.randomUUID() + SEED_FILE_SUFFIX;
            }
            return new EntraConfig(this);
        }
    }
}
