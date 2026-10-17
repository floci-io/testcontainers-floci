package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for RDS Data API-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RdsDataConfig config = RdsDataConfig.builder()
 *     .enabled(true)
 *     .transactionTtlSeconds(300)
 *     .databaseResumingExceptionEnabled(true)
 *     .build();
 * }</pre>
 */
public class RdsDataConfig extends AbstractServiceConfig<RdsDataConfig.Builder> {

    private static final long DEFAULT_TRANSACTION_TTL_SECONDS = 180;
    private static final boolean DEFAULT_DATABASE_RESUMING_EXCEPTION_ENABLED = false;

    private final long transactionTtlSeconds;
    private final boolean databaseResumingExceptionEnabled;

    private RdsDataConfig(Builder builder) {
        super(builder);
        this.transactionTtlSeconds = builder.transactionTtlSeconds;
        this.databaseResumingExceptionEnabled = builder.databaseResumingExceptionEnabled;
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
     * Returns the TTL in seconds after which an idle transaction is automatically rolled back.
     *
     * @return the transaction TTL in seconds
     */
    public long getTransactionTtlSeconds() {
        return transactionTtlSeconds;
    }

    /**
     * Returns whether a retryable {@code DatabaseResumingException} (HTTP 400) is returned while an
     * auto-paused Aurora backend resumes, instead of waiting for it.
     *
     * @return {@code true} if the resuming exception is returned
     */
    public boolean isDatabaseResumingExceptionEnabled() {
        return databaseResumingExceptionEnabled;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_RDS_DATA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", String.valueOf(transactionTtlSeconds));
            container.withEnv("FLOCI_SERVICES_RDS_DATA_DATABASE_RESUMING_EXCEPTION_ENABLED", String.valueOf(databaseResumingExceptionEnabled));
        }
    }

    /**
     * Builder for {@link RdsDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RdsDataConfig> {

        private long transactionTtlSeconds = DEFAULT_TRANSACTION_TTL_SECONDS;
        private boolean databaseResumingExceptionEnabled = DEFAULT_DATABASE_RESUMING_EXCEPTION_ENABLED;

        private Builder() {
            // Allow instantiation only via RdsDataConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RdsDataConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RdsDataConfig instance) {
            super(instance);
            this.transactionTtlSeconds = instance.getTransactionTtlSeconds();
            this.databaseResumingExceptionEnabled = instance.isDatabaseResumingExceptionEnabled();
        }

        /**
         * Sets the TTL in seconds after which an idle transaction is automatically rolled back.
         *
         * @param transactionTtlSeconds the transaction TTL in seconds (default {@value DEFAULT_TRANSACTION_TTL_SECONDS})
         * @return this builder
         */
        public Builder transactionTtlSeconds(long transactionTtlSeconds) {
            this.transactionTtlSeconds = transactionTtlSeconds;
            return this;
        }

        /**
         * Sets whether a retryable {@code DatabaseResumingException} (HTTP 400) is returned while an
         * auto-paused Aurora backend resumes. When enabled, {@code ExecuteStatement},
         * {@code BatchExecuteStatement} and {@code BeginTransaction} start the resume in the background
         * and return the error until the backend is ready, as AWS does; when disabled, they block until
         * the resume completes.
         *
         * @param databaseResumingExceptionEnabled {@code true} to return the resuming exception (default {@value DEFAULT_DATABASE_RESUMING_EXCEPTION_ENABLED})
         * @return this builder
         */
        public Builder databaseResumingExceptionEnabled(boolean databaseResumingExceptionEnabled) {
            this.databaseResumingExceptionEnabled = databaseResumingExceptionEnabled;
            return this;
        }

        /**
         * Creates an immutable {@link RdsDataConfig} from this builder.
         *
         * @return the RDS Data configuration
         */
        @Override
        public RdsDataConfig build() {
            return new RdsDataConfig(this);
        }
    }
}
