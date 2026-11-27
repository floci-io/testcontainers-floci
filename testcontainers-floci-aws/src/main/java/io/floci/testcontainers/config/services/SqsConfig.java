package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Locale;

/**
 * Configuration for SQS-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SqsConfig config = SqsConfig.builder()
 *     .defaultVisibilityTimeout(60)
 *     .maxMessageSize(131072)
 *     .clearFifoDeduplicationCacheOnPurge(false)
 *     .build();
 * }</pre>
 */
public class SqsConfig extends AbstractServiceConfig<SqsConfig.Builder> {

    private static final int DEFAULT_VISIBILITY_TIMEOUT = 30;
    private static final int DEFAULT_MAX_MESSAGE_SIZE = 1048576;
    private static final boolean DEFAULT_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE = true;
    private static final String DEFAULT_RECEIPT_HANDLE_SECRET = "local-emulator-secret";
    private static final boolean DEFAULT_CLOUDWATCH_METRICS_ENABLED = true;
    private static final EndpointStrategy DEFAULT_ENDPOINT_STRATEGY = EndpointStrategy.FLOCI;

    private final int defaultVisibilityTimeout;
    private final int maxMessageSize;
    private final boolean clearFifoDeduplicationCacheOnPurge;
    private final String receiptHandleSecret;
    private final boolean cloudwatchMetricsEnabled;
    private final EndpointStrategy endpointStrategy;

    private SqsConfig(Builder builder) {
        super(builder);
        this.defaultVisibilityTimeout = builder.defaultVisibilityTimeout;
        this.maxMessageSize = builder.maxMessageSize;
        this.clearFifoDeduplicationCacheOnPurge = builder.clearFifoDeduplicationCacheOnPurge;
        this.receiptHandleSecret = builder.receiptHandleSecret;
        this.cloudwatchMetricsEnabled = builder.cloudwatchMetricsEnabled;
        this.endpointStrategy = builder.endpointStrategy;
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
     * Returns the default visibility timeout in seconds.
     *
     * @return the default visibility timeout in seconds
     */
    public int getDefaultVisibilityTimeout() {
        return defaultVisibilityTimeout;
    }

    /**
     * Returns the maximum message size in bytes.
     *
     * @return the maximum message size in bytes
     */
    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    /**
     * Returns whether the FIFO deduplication cache is cleared on queue purge.
     *
     * @return {@code true} if the FIFO deduplication cache is cleared on purge
     */
    public boolean isClearFifoDeduplicationCacheOnPurge() {
        return clearFifoDeduplicationCacheOnPurge;
    }

    /**
     * Returns the secret used to sign the receipt handles issued by ReceiveMessage.
     *
     * <p>Floci uses it to recognize stale, gone and invalid receipt handles the way AWS does.
     *
     * @return the secret used to sign the receipt handles issued by ReceiveMessage
     */
    public String getReceiptHandleSecret() {
        return receiptHandleSecret;
    }

    /**
     * Returns whether Floci publishes the {@code AWS/SQS} CloudWatch metrics of every active queue once a minute, as SQS does.
     *
     * @return whether Floci publishes the {@code AWS/SQS} CloudWatch metrics of every active queue once a minute, as SQS does
     */
    public boolean isCloudwatchMetricsEnabled() {
        return cloudwatchMetricsEnabled;
    }

    /**
     * Returns the shape of the queue URL returned by CreateQueue, GetQueueUrl, ListQueues and ListDeadLetterSourceQueues.
     *
     * <p>Either form is accepted on input under either setting, since Floci looks queues up by the URL path and ignores the host.
     *
     * @return the shape of the queue URL returned by CreateQueue, GetQueueUrl, ListQueues and ListDeadLetterSourceQueues
     */
    public EndpointStrategy getEndpointStrategy() {
        return endpointStrategy;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SQS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", String.valueOf(defaultVisibilityTimeout));
            container.withEnv("FLOCI_SERVICES_SQS_MAX_MESSAGE_SIZE", String.valueOf(maxMessageSize));
            container.withEnv("FLOCI_SERVICES_SQS_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE", String.valueOf(clearFifoDeduplicationCacheOnPurge));
            container.withEnv("FLOCI_SERVICES_SQS_RECEIPT_HANDLE_SECRET", receiptHandleSecret);
            container.withEnv("FLOCI_SERVICES_SQS_CLOUDWATCH_METRICS_ENABLED", String.valueOf(cloudwatchMetricsEnabled));
            container.withEnv("FLOCI_SERVICES_SQS_ENDPOINT_STRATEGY", endpointStrategy.name().toLowerCase(Locale.ROOT));
        }
    }

    /**
     * Shape of the queue URLs Floci returns, see {@link Builder#endpointStrategy(EndpointStrategy)}.
     */
    public enum EndpointStrategy {
        /** Floci's own endpoint form, {@code <base-url>/<account>/<name>}. */
        FLOCI,
        /**
         * The AWS form, {@code https://sqs.<region>.<dnsSuffix>/<account>/<name>}, with the DNS suffix of the region's
         * partition.
         */
        AWS
    }

    /**
     * Builder for {@link SqsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SqsConfig> {

        private int defaultVisibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;
        private int maxMessageSize = DEFAULT_MAX_MESSAGE_SIZE;
        private boolean clearFifoDeduplicationCacheOnPurge = DEFAULT_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE;
        private String receiptHandleSecret = DEFAULT_RECEIPT_HANDLE_SECRET;
        private boolean cloudwatchMetricsEnabled = DEFAULT_CLOUDWATCH_METRICS_ENABLED;
        private EndpointStrategy endpointStrategy = DEFAULT_ENDPOINT_STRATEGY;

        private Builder() {
            // Allow instantiation only via SqsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SqsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SqsConfig instance) {
            super(instance);
            this.defaultVisibilityTimeout = instance.getDefaultVisibilityTimeout();
            this.maxMessageSize = instance.getMaxMessageSize();
            this.clearFifoDeduplicationCacheOnPurge = instance.isClearFifoDeduplicationCacheOnPurge();
            this.receiptHandleSecret = instance.getReceiptHandleSecret();
            this.cloudwatchMetricsEnabled = instance.isCloudwatchMetricsEnabled();
            this.endpointStrategy = instance.getEndpointStrategy();
        }

        /**
         * Sets the default visibility timeout for messages in seconds.
         *
         * @param defaultVisibilityTimeout the default visibility timeout for messages in seconds (default {@value DEFAULT_VISIBILITY_TIMEOUT})
         * @return this builder
         */
        public Builder defaultVisibilityTimeout(int defaultVisibilityTimeout) {
            this.defaultVisibilityTimeout = defaultVisibilityTimeout;
            return this;
        }

        /**
         * Sets the maximum message size in bytes.
         *
         * @param maxMessageSize the maximum message size in bytes (default {@value DEFAULT_MAX_MESSAGE_SIZE})
         * @return this builder
         */
        public Builder maxMessageSize(int maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
            return this;
        }

        /**
         * Sets whether the FIFO deduplication cache should be cleared on queue purge.
         *
         * @param clearFifoDeduplicationCacheOnPurge {@code true} to clear the cache on purge (default {@value DEFAULT_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE})
         * @return this builder
         */
        public Builder clearFifoDeduplicationCacheOnPurge(boolean clearFifoDeduplicationCacheOnPurge) {
            this.clearFifoDeduplicationCacheOnPurge = clearFifoDeduplicationCacheOnPurge;
            return this;
        }

        /**
         * Sets the secret used to sign the receipt handles issued by ReceiveMessage.
         *
         * <p>Floci uses it to recognize stale, gone and invalid receipt handles the way AWS does.
         *
         * @param receiptHandleSecret the secret used to sign the receipt handles issued by ReceiveMessage (default {@value DEFAULT_RECEIPT_HANDLE_SECRET})
         * @return this builder
         */
        public Builder receiptHandleSecret(String receiptHandleSecret) {
            this.receiptHandleSecret = receiptHandleSecret;
            return this;
        }

        /**
         * Sets whether Floci publishes the {@code AWS/SQS} CloudWatch metrics of every active queue once a minute, as SQS does.
         *
         * @param cloudwatchMetricsEnabled whether Floci publishes the {@code AWS/SQS} CloudWatch metrics of every active queue once a minute, as SQS does (default {@value DEFAULT_CLOUDWATCH_METRICS_ENABLED})
         * @return this builder
         */
        public Builder cloudwatchMetricsEnabled(boolean cloudwatchMetricsEnabled) {
            this.cloudwatchMetricsEnabled = cloudwatchMetricsEnabled;
            return this;
        }

        /**
         * Sets the shape of the queue URL returned by CreateQueue, GetQueueUrl, ListQueues and ListDeadLetterSourceQueues.
         *
         * <p>Either form is accepted on input under either setting, since Floci looks queues up by the URL path and ignores the host.
         *
         * @param endpointStrategy the shape of the queue URL returned by CreateQueue, GetQueueUrl, ListQueues and ListDeadLetterSourceQueues (default {@link EndpointStrategy#FLOCI})
         * @return this builder
         */
        public Builder endpointStrategy(EndpointStrategy endpointStrategy) {
            this.endpointStrategy = endpointStrategy;
            return this;
        }

        /**
         * Creates an immutable {@link SqsConfig} from this builder.
         *
         * @return the SQS configuration
         */
        @Override
        public SqsConfig build() {
            return new SqsConfig(this);
        }
    }
}
