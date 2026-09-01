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
public class S3Config extends AbstractServiceConfig<S3Config.Builder> {

    private static final int DEFAULT_PRESIGN_EXPIRY_SECONDS = 3600;
    private static final boolean DEFAULT_ENFORCE_AUTH = false;
    private static final boolean DEFAULT_GLOBAL_BUCKET_NAMESPACE = false;

    private final int defaultPresignExpirySeconds;
    private final boolean enforceAuth;
    private final boolean globalBucketNamespace;

    private S3Config(Builder builder) {
        super(builder.enabled);
        this.defaultPresignExpirySeconds = builder.defaultPresignExpirySeconds;
        this.enforceAuth = builder.enforceAuth;
        this.globalBucketNamespace = builder.globalBucketNamespace;
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

    /**
     * Returns whether S3 bucket/object existence resolution spans every account's partition,
     * modelling AWS's globally-unique bucket namespace. Listing operations stay owner-scoped.
     *
     * @return {@code true} if the global bucket namespace is enabled
     */
    public boolean isGlobalBucketNamespace() {
        return globalBucketNamespace;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_S3_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", String.valueOf(defaultPresignExpirySeconds));
            container.withEnv("FLOCI_SERVICES_S3_ENFORCE_AUTH", String.valueOf(enforceAuth));
            container.withEnv("FLOCI_SERVICES_S3_GLOBAL_BUCKET_NAMESPACE", String.valueOf(globalBucketNamespace));
        }
    }

    /**
     * Builder for {@link S3Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, S3Config> {

        private int defaultPresignExpirySeconds = DEFAULT_PRESIGN_EXPIRY_SECONDS;
        private boolean enforceAuth = DEFAULT_ENFORCE_AUTH;
        private boolean globalBucketNamespace = DEFAULT_GLOBAL_BUCKET_NAMESPACE;

        private Builder() {
            // Allow instantiation only via S3Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link S3Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(S3Config instance) {
            super(instance);
            this.defaultPresignExpirySeconds = instance.getDefaultPresignExpirySeconds();
            this.enforceAuth = instance.isEnforceAuth();
            this.globalBucketNamespace = instance.isGlobalBucketNamespace();
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
         * Sets whether S3 bucket/object existence resolution spans every account's partition,
         * modelling AWS's globally-unique bucket namespace so that a bucket owned by one account
         * is reachable cross-account. Listing operations stay owner-scoped, and mutations are
         * written back to the bucket's owning account. Leave off when the per-account partition
         * is the isolation you are relying on.
         *
         * @param globalBucketNamespace {@code true} to enable the global bucket namespace (default {@value DEFAULT_GLOBAL_BUCKET_NAMESPACE})
         * @return this builder
         */
        public Builder globalBucketNamespace(boolean globalBucketNamespace) {
            this.globalBucketNamespace = globalBucketNamespace;
            return this;
        }

        /**
         * Creates an immutable {@link S3Config} from this builder.
         *
         * @return the S3 configuration
         */
        @Override
        public S3Config build() {
            return new S3Config(this);
        }
    }
}
