package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration for Cloud Run of Floci GCP.
 *
 * <p>Services, jobs and worker pools run in sibling containers whose ports Floci GCP publishes on the Docker host.
 * Services are invoked through the main Floci GCP port, with the service URL's host in the {@code Host} header. In
 * {@code mock} mode only the control plane is emulated and no containers are started.
 *
 * <p>The properties of Floci's {@code cloudrun.execution} group are flattened into this class
 * ({@code FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_*}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudRunConfig config = CloudRunConfig.builder()
 *     .startupTimeout(Duration.ofSeconds(60))
 *     .maxWorkerInstances(2)
 *     .build();
 * }</pre>
 */
public class CloudRunConfig extends AbstractServiceConfig<CloudRunConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_DEFAULT_PORT = 8080;
    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofSeconds(240);
    private static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(300);
    private static final Duration DEFAULT_OPERATION_TIMEOUT = Duration.ofSeconds(300);
    private static final Duration DEFAULT_CLEANUP_TIMEOUT = Duration.ofSeconds(15);
    private static final int DEFAULT_MAX_WORKER_INSTANCES = 1;

    private final boolean mock;
    private final int defaultPort;
    private final Duration startupTimeout;
    private final Duration requestTimeout;
    private final Duration operationTimeout;
    private final Duration cleanupTimeout;
    private final String urlHostSuffix;
    private final int maxWorkerInstances;

    private CloudRunConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.defaultPort = builder.defaultPort;
        this.startupTimeout = builder.startupTimeout;
        this.requestTimeout = builder.requestTimeout;
        this.operationTimeout = builder.operationTimeout;
        this.cleanupTimeout = builder.cleanupTimeout;
        this.urlHostSuffix = builder.urlHostSuffix;
        this.maxWorkerInstances = builder.maxWorkerInstances;
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
     * Returns whether no containers are started; services, jobs and worker pools are only emulated as control plane
     * resources.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the container port used for services that do not declare one.
     *
     * @return the default container port
     */
    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * Returns the maximum time to wait for a service, job or worker pool container to become ready.
     *
     * @return the startup timeout
     */
    public Duration getStartupTimeout() {
        return startupTimeout;
    }

    /**
     * Returns the timeout of the proxy that forwards invocations to service containers.
     *
     * @return the request timeout
     */
    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Returns the maximum time an asynchronous Cloud Run operation may take before its long-running operation
     * fails.
     *
     * @return the operation timeout
     */
    public Duration getOperationTimeout() {
        return operationTimeout;
    }

    /**
     * Returns the maximum time to wait for the best-effort Docker cleanup after an operation is already resolved.
     *
     * @return the cleanup timeout
     */
    public Duration getCleanupTimeout() {
        return cleanupTimeout;
    }

    /**
     * Returns the host suffix of the generated service URLs ({@code <service>-<hash>.<region>.run.<suffix>}), or
     * empty if not set.
     *
     * @return the URL host suffix, or empty
     */
    public Optional<String> getUrlHostSuffix() {
        return Optional.ofNullable(urlHostSuffix);
    }

    /**
     * Returns the maximum number of replica containers run per worker pool.
     *
     * @return the maximum number of worker instances
     */
    public int getMaxWorkerInstances() {
        return maxWorkerInstances;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_DEFAULT_PORT", String.valueOf(defaultPort));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_STARTUP_TIMEOUT", format(startupTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_REQUEST_TIMEOUT", format(requestTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_OPERATION_TIMEOUT", format(operationTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_CLEANUP_TIMEOUT", format(cleanupTimeout));

            if (urlHostSuffix != null) {
                container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_URL_HOST_SUFFIX", urlHostSuffix);
            }

            container.withEnv("FLOCI_GCP_SERVICES_CLOUDRUN_EXECUTION_MAX_WORKER_INSTANCES", String.valueOf(maxWorkerInstances));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Formats a duration the way Floci GCP declares its defaults: whole seconds as {@code <n>s}, anything finer
     * as {@code <n>ms}.
     */
    private static String format(Duration duration) {
        long millis = duration.toMillis();
        return millis % 1000 == 0 ? (millis / 1000) + "s" : millis + "ms";
    }

    /**
     * Builder for {@link CloudRunConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudRunConfig> {

        private boolean mock = DEFAULT_MOCK;
        private int defaultPort = DEFAULT_DEFAULT_PORT;
        private Duration startupTimeout = DEFAULT_STARTUP_TIMEOUT;
        private Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;
        private Duration operationTimeout = DEFAULT_OPERATION_TIMEOUT;
        private Duration cleanupTimeout = DEFAULT_CLEANUP_TIMEOUT;
        private String urlHostSuffix;
        private int maxWorkerInstances = DEFAULT_MAX_WORKER_INSTANCES;

        private Builder() {
            // Allow instantiation only via CloudRunConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudRunConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudRunConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.defaultPort = instance.defaultPort;
            this.startupTimeout = instance.startupTimeout;
            this.requestTimeout = instance.requestTimeout;
            this.operationTimeout = instance.operationTimeout;
            this.cleanupTimeout = instance.cleanupTimeout;
            this.urlHostSuffix = instance.urlHostSuffix;
            this.maxWorkerInstances = instance.maxWorkerInstances;
        }

        /**
         * Sets whether no containers are started; services, jobs and worker pools are only emulated as control
         * plane resources. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the container port used for services that do not declare one.
         *
         * @param defaultPort the default container port (default {@value DEFAULT_DEFAULT_PORT})
         * @return this builder
         */
        public Builder defaultPort(int defaultPort) {
            this.defaultPort = defaultPort;
            return this;
        }

        /**
         * Sets the maximum time to wait for a service, job or worker pool container to become ready.
         *
         * @param startupTimeout the startup timeout (default 240 seconds)
         * @return this builder
         */
        public Builder startupTimeout(Duration startupTimeout) {
            this.startupTimeout = startupTimeout;
            return this;
        }

        /**
         * Sets the timeout of the proxy that forwards invocations to service containers.
         *
         * @param requestTimeout the request timeout (default 300 seconds)
         * @return this builder
         */
        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * Sets the maximum time an asynchronous Cloud Run operation may take before its long-running operation
         * fails.
         *
         * @param operationTimeout the operation timeout (default 300 seconds)
         * @return this builder
         */
        public Builder operationTimeout(Duration operationTimeout) {
            this.operationTimeout = operationTimeout;
            return this;
        }

        /**
         * Sets the maximum time to wait for the best-effort Docker cleanup after an operation is already resolved.
         *
         * @param cleanupTimeout the cleanup timeout (default 15 seconds)
         * @return this builder
         */
        public Builder cleanupTimeout(Duration cleanupTimeout) {
            this.cleanupTimeout = cleanupTimeout;
            return this;
        }

        /**
         * Sets the host suffix of the generated service URLs ({@code <service>-<hash>.<region>.run.<suffix>}).
         * When unset, Floci GCP uses its hostname setting or {@code localhost.floci.io}.
         *
         * @param urlHostSuffix the URL host suffix, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder urlHostSuffix(String urlHostSuffix) {
            this.urlHostSuffix = urlHostSuffix;
            return this;
        }

        /**
         * Sets the maximum number of replica containers run per worker pool. Larger requested instance counts are
         * clamped with a warning.
         *
         * @param maxWorkerInstances the maximum number of worker instances (default {@value DEFAULT_MAX_WORKER_INSTANCES})
         * @return this builder
         */
        public Builder maxWorkerInstances(int maxWorkerInstances) {
            this.maxWorkerInstances = maxWorkerInstances;
            return this;
        }

        /**
         * Creates an immutable {@link CloudRunConfig} from this builder.
         *
         * @return the Cloud Run configuration
         */
        @Override
        public CloudRunConfig build() {
            return new CloudRunConfig(this);
        }
    }
}
