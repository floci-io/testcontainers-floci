package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for AppSync-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppSyncConfig config = AppSyncConfig.builder()
 *     .build();
 * }</pre>
 */
public class AppSyncConfig extends AbstractServiceConfig {

    private static final int DEFAULT_SCHEMA_WORKER_THREADS = 4;
    private static final int DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final int schemaWorkerThreads;
    private final int schemaWorkerShutdownTimeoutSeconds;

    private AppSyncConfig(Builder builder) {
        super(builder.enabled);
        this.schemaWorkerThreads = builder.schemaWorkerThreads;
        this.schemaWorkerShutdownTimeoutSeconds = builder.schemaWorkerShutdownTimeoutSeconds;
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
     * Returns the number of worker threads used for asynchronous schema creation.
     *
     * @return the number of worker threads
     */
    public int getSchemaWorkerThreads() {
        return schemaWorkerThreads;
    }

    /**
     * Returns the number of seconds to wait for in-flight schema workers on shutdown.
     *
     * @return timeout in seconds
     */
    public int getSchemaWorkerShutdownTimeoutSeconds() {
        return schemaWorkerShutdownTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APPSYNC_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", String.valueOf(schemaWorkerThreads));
            container.withEnv("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS",
                    String.valueOf(schemaWorkerShutdownTimeoutSeconds));
        }
    }

    /**
     * Builder for {@link AppSyncConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private int schemaWorkerThreads = DEFAULT_SCHEMA_WORKER_THREADS;
        private int schemaWorkerShutdownTimeoutSeconds = DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via AppSyncConfig.builder()
        }

        /**
         * Enables or disables the AppSync service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Sets the number of worker threads used for asynchronous schema creation.
         *
         * @param schemaWorkerThreads the number of worker threads (default {@value DEFAULT_SCHEMA_WORKER_THREADS})
         * @return this builder
         */
        public Builder schemaWorkerThreads(int schemaWorkerThreads) {
            this.schemaWorkerThreads = schemaWorkerThreads;
            return this;
        }

        /**
         * Sets the number of seconds to wait for in-flight schema workers on shutdown.
         *
         * @param schemaWorkerShutdownTimeoutSeconds timeout in seconds
         *         (default {@value DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder schemaWorkerShutdownTimeoutSeconds(int schemaWorkerShutdownTimeoutSeconds) {
            this.schemaWorkerShutdownTimeoutSeconds = schemaWorkerShutdownTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link AppSyncConfig} from this builder.
         *
         * @return the AppSync configuration
         */
        public AppSyncConfig build() {
            return new AppSyncConfig(this);
        }
    }
}
