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
 *     .build();
 * }</pre>
 */
public class RdsDataConfig extends AbstractServiceConfig<RdsDataConfig.Builder> {

    private static final long DEFAULT_TRANSACTION_TTL_SECONDS = 180;

    private final long transactionTtlSeconds;

    private RdsDataConfig(Builder builder) {
        super(builder.enabled);
        this.transactionTtlSeconds = builder.transactionTtlSeconds;
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_RDS_DATA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", String.valueOf(transactionTtlSeconds));
        }
    }

    /**
     * Builder for {@link RdsDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RdsDataConfig> {

        private long transactionTtlSeconds = DEFAULT_TRANSACTION_TTL_SECONDS;

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
