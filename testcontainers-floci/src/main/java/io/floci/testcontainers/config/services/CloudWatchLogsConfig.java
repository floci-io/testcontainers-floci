package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudWatch Logs-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudWatchLogsConfig config = CloudWatchLogsConfig.builder()
 *     .maxEventsPerQuery(5000)
 *     .build();
 * }</pre>
 */
public class CloudWatchLogsConfig extends AbstractServiceConfig<CloudWatchLogsConfig.Builder> {

    private static final int DEFAULT_MAX_EVENTS_PER_QUERY = 10000;
    private static final long DEFAULT_QUERY_COMPLETION_DELAY_MS = 0;

    private final int maxEventsPerQuery;
    private final long queryCompletionDelayMs;

    private CloudWatchLogsConfig(Builder builder) {
        super(builder.enabled);
        this.maxEventsPerQuery = builder.maxEventsPerQuery;
        this.queryCompletionDelayMs = builder.queryCompletionDelayMs;
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
     * Returns the maximum events per query.
     *
     * @return the maximum events per query
     */
    public int getMaxEventsPerQuery() {
        return maxEventsPerQuery;
    }

    /**
     * Returns the artificial Logs Insights query completion delay, in milliseconds.
     *
     * @return the query completion delay, in milliseconds
     */
    public long getQueryCompletionDelayMs() {
        return queryCompletionDelayMs;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDWATCHLOGS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDWATCHLOGS_MAX_EVENTS_PER_QUERY", String.valueOf(maxEventsPerQuery));
            container.withEnv("FLOCI_SERVICES_CLOUDWATCHLOGS_QUERY_COMPLETION_DELAY_MS", String.valueOf(queryCompletionDelayMs));
        }
    }

    /**
     * Builder for {@link CloudWatchLogsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudWatchLogsConfig> {

        private int maxEventsPerQuery = DEFAULT_MAX_EVENTS_PER_QUERY;
        private long queryCompletionDelayMs = DEFAULT_QUERY_COMPLETION_DELAY_MS;

        private Builder() {
            // Allow instantiation only via CloudWatchLogsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudWatchLogsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudWatchLogsConfig instance) {
            super(instance);
            this.maxEventsPerQuery = instance.getMaxEventsPerQuery();
            this.queryCompletionDelayMs = instance.getQueryCompletionDelayMs();
        }

        /**
         * Sets the maximum number of events returned per log query.
         *
         * @param maxEventsPerQuery the maximum number of events returned per log query (default {@value DEFAULT_MAX_EVENTS_PER_QUERY})
         * @return this builder
         */
        public Builder maxEventsPerQuery(int maxEventsPerQuery) {
            this.maxEventsPerQuery = maxEventsPerQuery;
            return this;
        }

        /**
         * Sets the artificial Logs Insights query completion delay, in milliseconds. With the
         * default 0, queries complete immediately (fast local dev). A positive value emulates the
         * real asynchronous lifecycle — StartQuery → Running → Complete after this delay — which
         * also makes StopQuery on a still-running query return {@code success=true}.
         *
         * @param queryCompletionDelayMs the query completion delay, in milliseconds (default {@value DEFAULT_QUERY_COMPLETION_DELAY_MS})
         * @return this builder
         */
        public Builder queryCompletionDelayMs(long queryCompletionDelayMs) {
            this.queryCompletionDelayMs = queryCompletionDelayMs;
            return this;
        }

        /**
         * Creates an immutable {@link CloudWatchLogsConfig} from this builder.
         *
         * @return the CloudWatch Logs configuration
         */
        @Override
        public CloudWatchLogsConfig build() {
            return new CloudWatchLogsConfig(this);
        }
    }
}
