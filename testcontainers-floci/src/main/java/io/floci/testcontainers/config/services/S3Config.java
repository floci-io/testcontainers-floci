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
    private static final boolean DEFAULT_LIFECYCLE_SWEEP_ENABLED = true;
    private static final long DEFAULT_LIFECYCLE_SWEEP_INTERVAL_SECONDS = 60;

    private final int defaultPresignExpirySeconds;
    private final boolean enforceAuth;
    private final boolean globalBucketNamespace;
    private final boolean lifecycleSweepEnabled;
    private final long lifecycleSweepIntervalSeconds;

    private S3Config(Builder builder) {
        super(builder.enabled);
        this.defaultPresignExpirySeconds = builder.defaultPresignExpirySeconds;
        this.enforceAuth = builder.enforceAuth;
        this.globalBucketNamespace = builder.globalBucketNamespace;
        this.lifecycleSweepEnabled = builder.lifecycleSweepEnabled;
        this.lifecycleSweepIntervalSeconds = builder.lifecycleSweepIntervalSeconds;
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

    /**
     * Returns whether the background sweep applying bucket lifecycle expiration rules
     * (Expiration, NoncurrentVersionExpiration, AbortIncompleteMultipartUpload) runs.
     *
     * @return {@code true} if the lifecycle sweep is enabled
     */
    public boolean isLifecycleSweepEnabled() {
        return lifecycleSweepEnabled;
    }

    /**
     * Returns how often the lifecycle sweep runs, in seconds.
     *
     * @return the lifecycle sweep interval in seconds
     */
    public long getLifecycleSweepIntervalSeconds() {
        return lifecycleSweepIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_S3_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", String.valueOf(defaultPresignExpirySeconds));
            container.withEnv("FLOCI_SERVICES_S3_ENFORCE_AUTH", String.valueOf(enforceAuth));
            container.withEnv("FLOCI_SERVICES_S3_GLOBAL_BUCKET_NAMESPACE", String.valueOf(globalBucketNamespace));
            container.withEnv("FLOCI_SERVICES_S3_LIFECYCLE_SWEEP_ENABLED", String.valueOf(lifecycleSweepEnabled));
            container.withEnv("FLOCI_SERVICES_S3_LIFECYCLE_SWEEP_INTERVAL_SECONDS", String.valueOf(lifecycleSweepIntervalSeconds));
        }
    }

    /**
     * Builder for {@link S3Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, S3Config> {

        private int defaultPresignExpirySeconds = DEFAULT_PRESIGN_EXPIRY_SECONDS;
        private boolean enforceAuth = DEFAULT_ENFORCE_AUTH;
        private boolean globalBucketNamespace = DEFAULT_GLOBAL_BUCKET_NAMESPACE;
        private boolean lifecycleSweepEnabled = DEFAULT_LIFECYCLE_SWEEP_ENABLED;
        private long lifecycleSweepIntervalSeconds = DEFAULT_LIFECYCLE_SWEEP_INTERVAL_SECONDS;

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
            this.lifecycleSweepEnabled = instance.isLifecycleSweepEnabled();
            this.lifecycleSweepIntervalSeconds = instance.getLifecycleSweepIntervalSeconds();
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
         * Sets whether the background sweep applying bucket lifecycle expiration rules runs:
         * Expiration, NoncurrentVersionExpiration and AbortIncompleteMultipartUpload. When off,
         * rules are stored and reported in {@code x-amz-expiration} but nothing expires.
         *
         * @param lifecycleSweepEnabled {@code true} to run the lifecycle sweep (default {@value DEFAULT_LIFECYCLE_SWEEP_ENABLED})
         * @return this builder
         */
        public Builder lifecycleSweepEnabled(boolean lifecycleSweepEnabled) {
            this.lifecycleSweepEnabled = lifecycleSweepEnabled;
            return this;
        }

        /**
         * Sets how often the lifecycle sweep runs, in seconds. AWS applies lifecycle rules about
         * once a day, to expiry times rounded to midnight UTC, so the interval only bounds how late
         * Floci expires objects.
         *
         * @param lifecycleSweepIntervalSeconds the lifecycle sweep interval in seconds (default {@value DEFAULT_LIFECYCLE_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder lifecycleSweepIntervalSeconds(long lifecycleSweepIntervalSeconds) {
            this.lifecycleSweepIntervalSeconds = lifecycleSweepIntervalSeconds;
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
