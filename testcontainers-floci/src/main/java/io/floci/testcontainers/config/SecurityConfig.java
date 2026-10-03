package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

import java.util.List;
import java.util.Optional;

/**
 * Security-related configuration for the Floci server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SecurityConfig config = SecurityConfig.builder()
 *     .extraCorsAllowedOrigins(List.of("https://example.com"))
 *     .disableCorsHeaders(true)
 *     .build();
 * }</pre>
 */
public class SecurityConfig {

    private static final boolean DEFAULT_DISABLE_CORS_HEADERS = false;
    private static final boolean DEFAULT_ALLOW_PRIVATE_JWT_TARGETS = false;
    private static final boolean DEFAULT_CORS_ALLOW_PRIVATE_NETWORK = false;

    private final List<String> extraCorsAllowedOrigins;
    private final List<String> extraCorsAllowedHeaders;
    private final List<String> extraCorsExposeHeaders;
    private final boolean disableCorsHeaders;
    private final boolean allowPrivateJwtTargets;
    private final boolean corsAllowPrivateNetwork;
    private final Boolean allowUnsafeNetworkExposure;

    private SecurityConfig(Builder builder) {
        this.extraCorsAllowedOrigins = builder.extraCorsAllowedOrigins;
        this.extraCorsAllowedHeaders = builder.extraCorsAllowedHeaders;
        this.extraCorsExposeHeaders = builder.extraCorsExposeHeaders;
        this.disableCorsHeaders = builder.disableCorsHeaders;
        this.allowPrivateJwtTargets = builder.allowPrivateJwtTargets;
        this.corsAllowPrivateNetwork = builder.corsAllowPrivateNetwork;
        this.allowUnsafeNetworkExposure = builder.allowUnsafeNetworkExposure;
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the extra CORS allowed origins, or {@link Optional#empty()} if not configured.
     *
     * @return the extra CORS allowed origins, or {@link Optional#empty()}
     */
    public Optional<List<String>> getExtraCorsAllowedOrigins() {
        return Optional.ofNullable(extraCorsAllowedOrigins);
    }

    /**
     * Returns the extra CORS allowed headers, or {@link Optional#empty()} if not configured.
     *
     * @return the extra CORS allowed headers, or {@link Optional#empty()}
     */
    public Optional<List<String>> getExtraCorsAllowedHeaders() {
        return Optional.ofNullable(extraCorsAllowedHeaders);
    }

    /**
     * Returns the extra CORS expose headers, or {@link Optional#empty()} if not configured.
     *
     * @return the extra CORS expose headers, or {@link Optional#empty()}
     */
    public Optional<List<String>> getExtraCorsExposeHeaders() {
        return Optional.ofNullable(extraCorsExposeHeaders);
    }

    /**
     * Returns whether CORS headers are disabled.
     *
     * @return {@code true} if CORS headers are disabled
     */
    public boolean isDisableCorsHeaders() {
        return disableCorsHeaders;
    }

    /**
     * Returns whether JWT issuer discovery and JWKS requests (API Gateway HTTP API JWT authorizers and
     * AppSync OIDC providers) may target private or loopback addresses.
     *
     * @return {@code true} if private JWT targets are allowed
     */
    public boolean isAllowPrivateJwtTargets() {
        return allowPrivateJwtTargets;
    }

    /**
     * Returns whether Private Network Access preflights are granted (responds with
     * {@code Access-Control-Allow-Private-Network: true}) once the origin already
     * passes the CORS allow-list.
     *
     * @return {@code true} if Private Network Access preflights are granted
     */
    public boolean isCorsAllowPrivateNetwork() {
        return corsAllowPrivateNetwork;
    }

    /**
     * Returns whether Floci may listen outside loopback, or {@link Optional#empty()} if not configured,
     * in which case the Floci image's own setting applies.
     *
     * @return whether unsafe network exposure is allowed, or {@link Optional#empty()}
     */
    public Optional<Boolean> getAllowUnsafeNetworkExposure() {
        return Optional.ofNullable(allowUnsafeNetworkExposure);
    }

    /**
     * Applies this security configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SECURITY_DISABLE_CORS_HEADERS", String.valueOf(disableCorsHeaders));
        container.withEnv("FLOCI_SECURITY_ALLOW_PRIVATE_JWT_TARGETS", String.valueOf(allowPrivateJwtTargets));
        container.withEnv("FLOCI_SECURITY_CORS_ALLOW_PRIVATE_NETWORK", String.valueOf(corsAllowPrivateNetwork));
        if (allowUnsafeNetworkExposure != null) {
            container.withEnv("FLOCI_SECURITY_ALLOW_UNSAFE_NETWORK_EXPOSURE", String.valueOf(allowUnsafeNetworkExposure));
        }

        if (extraCorsAllowedOrigins != null) {
            container.withEnv("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_ORIGINS", String.join(",", extraCorsAllowedOrigins));
        }
        if (extraCorsAllowedHeaders != null) {
            container.withEnv("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_HEADERS", String.join(",", extraCorsAllowedHeaders));
        }
        if (extraCorsExposeHeaders != null) {
            container.withEnv("FLOCI_SECURITY_EXTRA_CORS_EXPOSE_HEADERS", String.join(",", extraCorsExposeHeaders));
        }
    }

    /**
     * Builder for {@link SecurityConfig}.
     */
    public static class Builder {

        private List<String> extraCorsAllowedOrigins = null;
        private List<String> extraCorsAllowedHeaders = null;
        private List<String> extraCorsExposeHeaders = null;
        private boolean disableCorsHeaders = DEFAULT_DISABLE_CORS_HEADERS;
        private boolean allowPrivateJwtTargets = DEFAULT_ALLOW_PRIVATE_JWT_TARGETS;
        private boolean corsAllowPrivateNetwork = DEFAULT_CORS_ALLOW_PRIVATE_NETWORK;
        private Boolean allowUnsafeNetworkExposure = null;

        private Builder() {
            // Allow instantiation only via SecurityConfig.builder()
        }

        private Builder(SecurityConfig instance) {
            this.extraCorsAllowedOrigins = instance.extraCorsAllowedOrigins;
            this.extraCorsAllowedHeaders = instance.extraCorsAllowedHeaders;
            this.extraCorsExposeHeaders = instance.extraCorsExposeHeaders;
            this.disableCorsHeaders = instance.disableCorsHeaders;
            this.allowPrivateJwtTargets = instance.allowPrivateJwtTargets;
            this.corsAllowPrivateNetwork = instance.corsAllowPrivateNetwork;
            this.allowUnsafeNetworkExposure = instance.allowUnsafeNetworkExposure;
        }

        /**
         * Sets the extra CORS allowed origins.
         *
         * @param extraCorsAllowedOrigins the list of allowed origins, or {@code null} to clear
         * @return this builder
         */
        public Builder extraCorsAllowedOrigins(List<String> extraCorsAllowedOrigins) {
            this.extraCorsAllowedOrigins = extraCorsAllowedOrigins;
            return this;
        }

        /**
         * Sets the extra CORS allowed origins.
         *
         * @param extraCorsAllowedOrigins the allowed origins
         * @return this builder
         */
        public Builder extraCorsAllowedOrigins(String... extraCorsAllowedOrigins) {
            this.extraCorsAllowedOrigins = List.of(extraCorsAllowedOrigins);
            return this;
        }

        /**
         * Sets the extra CORS allowed headers.
         *
         * @param extraCorsAllowedHeaders the list of allowed headers, or {@code null} to clear
         * @return this builder
         */
        public Builder extraCorsAllowedHeaders(List<String> extraCorsAllowedHeaders) {
            this.extraCorsAllowedHeaders = extraCorsAllowedHeaders;
            return this;
        }

        /**
         * Sets the extra CORS allowed headers.
         *
         * @param extraCorsAllowedHeaders the allowed headers
         * @return this builder
         */
        public Builder extraCorsAllowedHeaders(String... extraCorsAllowedHeaders) {
            this.extraCorsAllowedHeaders = List.of(extraCorsAllowedHeaders);
            return this;
        }

        /**
         * Sets the extra CORS expose headers.
         *
         * @param extraCorsExposeHeaders the list of expose headers, or {@code null} to clear
         * @return this builder
         */
        public Builder extraCorsExposeHeaders(List<String> extraCorsExposeHeaders) {
            this.extraCorsExposeHeaders = extraCorsExposeHeaders;
            return this;
        }

        /**
         * Sets the extra CORS expose headers.
         *
         * @param extraCorsExposeHeaders the expose headers
         * @return this builder
         */
        public Builder extraCorsExposeHeaders(String... extraCorsExposeHeaders) {
            this.extraCorsExposeHeaders = List.of(extraCorsExposeHeaders);
            return this;
        }

        /**
         * Sets whether CORS headers are disabled.
         *
         * @param disableCorsHeaders {@code true} to disable CORS headers (default {@value DEFAULT_DISABLE_CORS_HEADERS})
         * @return this builder
         */
        public Builder disableCorsHeaders(boolean disableCorsHeaders) {
            this.disableCorsHeaders = disableCorsHeaders;
            return this;
        }

        /**
         * Sets whether JWT issuer discovery and JWKS requests (API Gateway HTTP API JWT authorizers and
         * AppSync OIDC providers) may target private addresses. By default Floci rejects HTTP issuers and
         * destinations resolving to local, link-local, private or other non-public addresses, so an
         * authorizer configuration cannot be turned into an SSRF path. When allowed, private HTTPS targets
         * and HTTP URLs using a literal private or loopback address are permitted, public HTTP targets
         * still are not. Useful for a local fixture issuer in an isolated environment.
         *
         * @param allowPrivateJwtTargets {@code true} to allow private JWT targets
         *                               (default {@value DEFAULT_ALLOW_PRIVATE_JWT_TARGETS})
         * @return this builder
         */
        public Builder allowPrivateJwtTargets(boolean allowPrivateJwtTargets) {
            this.allowPrivateJwtTargets = allowPrivateJwtTargets;
            return this;
        }

        /**
         * Sets whether to grant Private Network Access preflights (respond with
         * {@code Access-Control-Allow-Private-Network: true}) when the browser asks.
         * Only takes effect after the origin already passes the CORS allow-list, so a
         * page served from a public/secure origin can reach this loopback backend.
         *
         * <p>Off by default: it lets a public origin reach the private network, so it
         * must be opted into explicitly.</p>
         *
         * @param corsAllowPrivateNetwork {@code true} to grant Private Network Access preflights
         *                                (default {@value DEFAULT_CORS_ALLOW_PRIVATE_NETWORK})
         * @return this builder
         */
        public Builder corsAllowPrivateNetwork(boolean corsAllowPrivateNetwork) {
            this.corsAllowPrivateNetwork = corsAllowPrivateNetwork;
            return this;
        }

        /**
         * Sets whether Floci may listen outside loopback ({@code 127.0.0.0/8}, {@code ::1},
         * {@code localhost}). Anyone who can reach such an address can call Floci's APIs, so Floci refuses
         * to start on one unless this is allowed.
         *
         * <p>Unset by default, so the Floci image's own setting applies: the official image listens on
         * {@code 0.0.0.0} and already allows this via a {@code -Dfloci.security.allow-unsafe-network-exposure=true}
         * system property on its command line, which takes precedence over the environment variable. Setting
         * this only has an effect with an image or command that does not pass that property.
         *
         * @param allowUnsafeNetworkExposure {@code true} to allow listening outside loopback, or {@code null}
         *                                   to keep the image's setting (default)
         * @return this builder
         */
        public Builder allowUnsafeNetworkExposure(Boolean allowUnsafeNetworkExposure) {
            this.allowUnsafeNetworkExposure = allowUnsafeNetworkExposure;
            return this;
        }

        /**
         * Creates an immutable {@link SecurityConfig} from this builder.
         *
         * @return the security configuration
         */
        public SecurityConfig build() {
            return new SecurityConfig(this);
        }
    }
}
