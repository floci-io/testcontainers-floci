package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for S3-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * S3Config config = S3Config.builder()
 *     .defaultPresignExpirySeconds(7200)
 *     .enforceAuth(true)
 *     .build();
 * }</pre>
 */
public class S3Config extends AbstractServiceConfig {

    private static final int DEFAULT_PRESIGN_EXPIRY_SECONDS = 3600;
    private static final boolean DEFAULT_ENFORCE_AUTH = false;

    private final int defaultPresignExpirySeconds;
    private final boolean enforceAuth;

    private S3Config(Builder builder) {
        super(builder.enabled);
        this.defaultPresignExpirySeconds = builder.defaultPresignExpirySeconds;
        this.enforceAuth = builder.enforceAuth;
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
     * Returns the default presign expiry in seconds.
     *
     * @return the default presign expiry in seconds
     */
    public int getDefaultPresignExpirySeconds() {
        return defaultPresignExpirySeconds;
    }

    /**
     * Returns whether S3 requests must be authenticated with valid AWS SigV4 credentials.
     *
     * @return {@code true} if authentication is enforced
     */
    public boolean isEnforceAuth() {
        return enforceAuth;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_S3_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", String.valueOf(defaultPresignExpirySeconds));
            container.withEnv("FLOCI_SERVICES_S3_ENFORCE_AUTH", String.valueOf(enforceAuth));
        }
    }

    /**
     * Builder for {@link S3Config}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private int defaultPresignExpirySeconds = DEFAULT_PRESIGN_EXPIRY_SECONDS;
        private boolean enforceAuth = DEFAULT_ENFORCE_AUTH;

        private Builder() {
            // Allow instantiation only via S3Config.builder()
        }

        /**
         * Enables or disables the S3 service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Sets the default expiry time for presigned URLs in seconds.
         *
         * @param defaultPresignExpirySeconds the default expiry time for presigned URLs in seconds (default {@value DEFAULT_PRESIGN_EXPIRY_SECONDS})
         * @return this builder
         */
        public Builder defaultPresignExpirySeconds(int defaultPresignExpirySeconds) {
            this.defaultPresignExpirySeconds = defaultPresignExpirySeconds;
            return this;
        }

        /**
         * Sets whether S3 requests must be authenticated with valid AWS SigV4 credentials.
         *
         * @param enforceAuth {@code true} to enforce authentication (default {@value DEFAULT_ENFORCE_AUTH})
         * @return this builder
         */
        public Builder enforceAuth(boolean enforceAuth) {
            this.enforceAuth = enforceAuth;
            return this;
        }

        /**
         * Creates an immutable {@link S3Config} from this builder.
         *
         * @return the S3 configuration
         */
        public S3Config build() {
            return new S3Config(this);
        }
    }
}
