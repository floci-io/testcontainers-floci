package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Redshift Data API-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedshiftDataConfig config = RedshiftDataConfig.builder()
 *     .resultTtlHours(1)
 *     .maxConcurrentStatements(4)
 *     .build();
 * }</pre>
 */
public class RedshiftDataConfig extends AbstractServiceConfig<RedshiftDataConfig.Builder> {

    private static final int DEFAULT_RESULT_TTL_HOURS = 24;
    private static final int DEFAULT_MAX_CONCURRENT_STATEMENTS = 10;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final int resultTtlHours;
    private final int maxConcurrentStatements;
    private final int queueCapacity;
    private final int shutdownTimeoutSeconds;

    private RedshiftDataConfig(Builder builder) {
        super(builder.enabled);
        this.resultTtlHours = builder.resultTtlHours;
        this.maxConcurrentStatements = builder.maxConcurrentStatements;
        this.queueCapacity = builder.queueCapacity;
        this.shutdownTimeoutSeconds = builder.shutdownTimeoutSeconds;
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
     * Returns the number of hours statement results are kept for DescribeStatement and GetStatementResult.
     *
     * @return the number of hours statement results are kept for DescribeStatement and GetStatementResult
     */
    public int getResultTtlHours() {
        return resultTtlHours;
    }

    /**
     * Returns the number of shared workers that execute SQL statements asynchronously.
     *
     * @return the maximum number of concurrently executing statements
     */
    public int getMaxConcurrentStatements() {
        return maxConcurrentStatements;
    }

    /**
     * Returns the maximum number of queued statements, which is also the capacity of the statement
     * status event delivery queue.
     *
     * @return the queue capacity
     */
    public int getQueueCapacity() {
        return queueCapacity;
    }

    /**
     * Returns the shared deadline in seconds for draining running SQL and event delivery on shutdown.
     *
     * @return the shutdown timeout in seconds
     */
    public int getShutdownTimeoutSeconds() {
        return shutdownTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", String.valueOf(resultTtlHours));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_MAX_CONCURRENT_STATEMENTS", String.valueOf(maxConcurrentStatements));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_QUEUE_CAPACITY", String.valueOf(queueCapacity));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_SHUTDOWN_TIMEOUT_SECONDS", String.valueOf(shutdownTimeoutSeconds));
        }
    }

    /**
     * Builder for {@link RedshiftDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedshiftDataConfig> {

        private int resultTtlHours = DEFAULT_RESULT_TTL_HOURS;
        private int maxConcurrentStatements = DEFAULT_MAX_CONCURRENT_STATEMENTS;
        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
        private int shutdownTimeoutSeconds = DEFAULT_SHUTDOWN_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via RedshiftDataConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedshiftDataConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedshiftDataConfig instance) {
            super(instance);
            this.resultTtlHours = instance.getResultTtlHours();
            this.maxConcurrentStatements = instance.getMaxConcurrentStatements();
            this.queueCapacity = instance.getQueueCapacity();
            this.shutdownTimeoutSeconds = instance.getShutdownTimeoutSeconds();
        }

        /**
         * Sets the number of hours statement results are kept for DescribeStatement and GetStatementResult.
         *
         * @param resultTtlHours the number of hours statement results are kept for DescribeStatement and GetStatementResult (default {@value DEFAULT_RESULT_TTL_HOURS})
         * @return this builder
         */
        public Builder resultTtlHours(int resultTtlHours) {
            this.resultTtlHours = resultTtlHours;
            return this;
        }

        /**
         * Sets the number of shared workers that execute SQL statements asynchronously. Statements
         * beyond this number wait in the queue until a worker is free.
         *
         * @param maxConcurrentStatements the maximum number of concurrently executing statements (default {@value DEFAULT_MAX_CONCURRENT_STATEMENTS})
         * @return this builder
         */
        public Builder maxConcurrentStatements(int maxConcurrentStatements) {
            this.maxConcurrentStatements = maxConcurrentStatements;
            return this;
        }

        /**
         * Sets the maximum number of queued statements, which is also the capacity of the statement
         * status event delivery queue.
         *
         * @param queueCapacity the queue capacity (default {@value DEFAULT_QUEUE_CAPACITY})
         * @return this builder
         */
        public Builder queueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        /**
         * Sets the shared deadline in seconds for draining running SQL and event delivery when Floci
         * shuts down.
         *
         * @param shutdownTimeoutSeconds the shutdown timeout in seconds (default {@value DEFAULT_SHUTDOWN_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder shutdownTimeoutSeconds(int shutdownTimeoutSeconds) {
            this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link RedshiftDataConfig} from this builder.
         *
         * @return the Redshift Data API configuration
         */
        @Override
        public RedshiftDataConfig build() {
            return new RedshiftDataConfig(this);
        }
    }
}
