package io.floci.testcontainers.config.services;

import java.util.List;
import java.util.Optional;
import org.testcontainers.containers.Container;

/**
 * Configuration for Lambda-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * LambdaConfig config = LambdaConfig.builder()
 *     .enabled(true)
 *     .runtimeApiPortRange(9300, 10)
 *     .defaultMemoryMb(256)
 *     .build();
 * }</pre>
 */
public class LambdaConfig extends AbstractServiceConfig<LambdaConfig.Builder> {

    private static final boolean DEFAULT_EPHEMERAL = false;
    private static final boolean DEFAULT_EXPOSE_RUNTIME_PORTS = false;
    private static final int DEFAULT_MEMORY_MB = 128;
    private static final int DEFAULT_TIMEOUT_SECONDS = 3;
    private static final int DEFAULT_RUNTIME_API_BASE_PORT = 9200;
    private static final int DEFAULT_RUNTIME_API_PORTS_COUNT = 10;
    private static final int DEFAULT_POLL_INTERVAL_MS = 1000;
    private static final int DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS = 300;
    private static final int DEFAULT_REGION_CONCURRENCY_LIMIT = 1000;
    private static final int DEFAULT_UNRESERVED_CONCURRENCY_MIN = 100;

    private final boolean ephemeral;
    private final boolean exposeRuntimePorts;
    private final int defaultMemoryMb;
    private final int defaultTimeoutSeconds;
    private final String dockerNetwork;
    private final int runtimeApiBasePort;
    private final int runtimeApiPortsCount;
    private final int pollIntervalMs;
    private final int containerIdleTimeoutSeconds;
    private final int regionConcurrencyLimit;
    private final int unreservedConcurrencyMin;
    private final HotReload hotReload;
    private final String awsConfigPath;

    private LambdaConfig(Builder builder) {
        super(builder.enabled);
        this.ephemeral = builder.ephemeral;
        this.exposeRuntimePorts = builder.exposeRuntimePorts;
        this.defaultMemoryMb = builder.defaultMemoryMb;
        this.defaultTimeoutSeconds = builder.defaultTimeoutSeconds;
        this.dockerNetwork = builder.dockerNetwork;
        this.runtimeApiBasePort = builder.runtimeApiBasePort;
        this.runtimeApiPortsCount = builder.runtimeApiPortsCount;
        this.pollIntervalMs = builder.pollIntervalMs;
        this.containerIdleTimeoutSeconds = builder.containerIdleTimeoutSeconds;
        this.regionConcurrencyLimit = builder.regionConcurrencyLimit;
        this.unreservedConcurrencyMin = builder.unreservedConcurrencyMin;
        this.hotReload = builder.hotReload;
        this.awsConfigPath = builder.awsConfigPath;
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether Lambda containers are removed after each invocation.
     *
     * @return {@code true} if containers are ephemeral
     */
    public boolean isEphemeral() {
        return ephemeral;
    }

    /**
     * Returns whether the Lambda Runtime API ports should be exposed on the host machine.
     *
     * @return {@code true} if Runtime API ports are exposed
     */
    public boolean isExposeRuntimePorts() {
        return exposeRuntimePorts;
    }

    /**
     * Returns the default memory size for Lambda functions in megabytes.
     *
     * @return memory in MB
     */
    public int getDefaultMemoryMb() {
        return defaultMemoryMb;
    }

    /**
     * Returns the default timeout for Lambda function invocations in seconds.
     *
     * @return timeout in seconds
     */
    public int getDefaultTimeoutSeconds() {
        return defaultTimeoutSeconds;
    }

    /**
     * Returns the Docker network used for Lambda containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    /**
     * Returns the base port for the Lambda Runtime API port range.
     *
     * @return the base port
     */
    public int getRuntimeApiBasePort() {
        return runtimeApiBasePort;
    }

    /**
     * Returns the number of ports allocated for the Lambda Runtime API.
     *
     * @return the port count
     */
    public int getRuntimeApiPortsCount() {
        return runtimeApiPortsCount;
    }

    /**
     * Returns the highest port for the Lambda Runtime API port range.
     *
     * @return the highest port in the range
     */
    public int getRuntimeApiMaxPort() {
        return runtimeApiBasePort + runtimeApiPortsCount - 1;
    }

    /**
     * Returns the poll interval in milliseconds for Lambda container status checks.
     *
     * @return interval in milliseconds
     */
    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

    /**
     * Returns the idle timeout in seconds after which unused Lambda containers are
     * cleaned up.
     *
     * @return timeout in seconds
     */
    public int getContainerIdleTimeoutSeconds() {
        return containerIdleTimeoutSeconds;
    }

    /**
     * Returns the per-region concurrent executions ceiling.
     *
     * @return the region concurrency limit
     */
    public int getRegionConcurrencyLimit() {
        return regionConcurrencyLimit;
    }

    /**
     * Returns the minimum unreserved concurrency that must remain after
     * {@code PutFunctionConcurrency}.
     *
     * @return the unreserved concurrency minimum
     */
    public int getUnreservedConcurrencyMin() {
        return unreservedConcurrencyMin;
    }

    /**
     * Returns the hot-reload configuration for development workflows.
     *
     * @return the hot-reload configuration
     */
    public HotReload getHotReload() {
        return hotReload;
    }

    /**
     * Returns the host path to bind-mount (read-only) into Lambda containers at
     * {@code /opt/aws-config}, or {@code null} if not set.
     *
     * <p>When present, no AWS credential env vars are injected; instead
     * {@code AWS_SHARED_CREDENTIALS_FILE} and {@code AWS_CONFIG_FILE} are set to point
     * at the mounted files. Blank values are treated as absent.
     *
     * @return the AWS config path, or {@code null} if not configured
     */
    public String getAwsConfigPath() {
        return awsConfigPath;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_LAMBDA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_LAMBDA_EPHEMERAL", String.valueOf(ephemeral));
            container.withEnv("FLOCI_SERVICES_LAMBDA_DEFAULT_MEMORY_MB", String.valueOf(defaultMemoryMb));
            container.withEnv("FLOCI_SERVICES_LAMBDA_DEFAULT_TIMEOUT_SECONDS", String.valueOf(defaultTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_LAMBDA_RUNTIME_API_BASE_PORT", String.valueOf(runtimeApiBasePort));
            container.withEnv("FLOCI_SERVICES_LAMBDA_RUNTIME_API_MAX_PORT", String.valueOf(getRuntimeApiMaxPort()));
            container.withEnv("FLOCI_SERVICES_LAMBDA_POLL_INTERVAL_MS", String.valueOf(pollIntervalMs));
            container.withEnv("FLOCI_SERVICES_LAMBDA_CONTAINER_IDLE_TIMEOUT_SECONDS", String.valueOf(containerIdleTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_LAMBDA_REGION_CONCURRENCY_LIMIT", String.valueOf(regionConcurrencyLimit));
            container.withEnv("FLOCI_SERVICES_LAMBDA_UNRESERVED_CONCURRENCY_MIN", String.valueOf(unreservedConcurrencyMin));

            container.withEnv("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ENABLED", String.valueOf(hotReload.enabled()));
            if (hotReload.allowedPaths().isPresent() && !hotReload.allowedPaths().get().isEmpty()) {
                container.withEnv("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ALLOWED_PATHS", String.join(",", hotReload.allowedPaths().get()));
            }

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_LAMBDA_DOCKER_NETWORK", dockerNetwork);
            }

            if (awsConfigPath != null && !awsConfigPath.isBlank()) {
                container.withEnv("FLOCI_SERVICES_LAMBDA_AWS_CONFIG_PATH", awsConfigPath);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled() && exposeRuntimePorts) {
            for (int port = runtimeApiBasePort; port <= getRuntimeApiMaxPort(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    /**
     * Configuration for hot-reload development mode.
     */
    public interface HotReload {
        /**
         * When true, the magic bucket name {@code hot-reload} triggers a bind-mount of the
         * S3Key path (a Docker-host absolute path) into the Lambda container instead of
         * extracting a ZIP. Changes on disk are visible on the next invocation without
         * re-deploying. Disabled by default — when false, {@code hot-reload} is an
         * ordinary (non-existent) bucket and returns NoSuchBucket as usual.
         *
         * @return {@code true} if hot-reload is enabled
         */
        boolean enabled();

        /**
         * Optional allow-list of absolute path prefixes. When non-empty, the S3Key supplied
         * to a hot-reload CreateFunction/UpdateFunctionCode must start with one of these
         * prefixes. Empty = all absolute paths are accepted.
         *
         * @return the list of allowed path prefixes, or empty if unrestricted
         */
        Optional<List<String>> allowedPaths();
    }

    /**
     * Builder for {@link LambdaConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, LambdaConfig> {

        private boolean ephemeral = DEFAULT_EPHEMERAL;
        private boolean exposeRuntimePorts = DEFAULT_EXPOSE_RUNTIME_PORTS;
        private int defaultMemoryMb = DEFAULT_MEMORY_MB;
        private int defaultTimeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
        private String dockerNetwork;
        private int runtimeApiBasePort = DEFAULT_RUNTIME_API_BASE_PORT;
        private int runtimeApiPortsCount = DEFAULT_RUNTIME_API_PORTS_COUNT;
        private int pollIntervalMs = DEFAULT_POLL_INTERVAL_MS;
        private int containerIdleTimeoutSeconds = DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS;
        private int regionConcurrencyLimit = DEFAULT_REGION_CONCURRENCY_LIMIT;
        private int unreservedConcurrencyMin = DEFAULT_UNRESERVED_CONCURRENCY_MIN;
        private HotReload hotReload = new DefaultHotReload(false, null);
        private String awsConfigPath;

        private Builder() {
            // Allow instantiation only via LambdaConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link LambdaConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(LambdaConfig instance) {
            super(instance);
            this.ephemeral = instance.isEphemeral();
            this.exposeRuntimePorts = instance.isExposeRuntimePorts();
            this.defaultMemoryMb = instance.getDefaultMemoryMb();
            this.defaultTimeoutSeconds = instance.getDefaultTimeoutSeconds();
            this.dockerNetwork = instance.getDockerNetwork();
            this.runtimeApiBasePort = instance.getRuntimeApiBasePort();
            this.runtimeApiPortsCount = instance.getRuntimeApiPortsCount();
            this.pollIntervalMs = instance.getPollIntervalMs();
            this.containerIdleTimeoutSeconds = instance.getContainerIdleTimeoutSeconds();
            this.regionConcurrencyLimit = instance.getRegionConcurrencyLimit();
            this.unreservedConcurrencyMin = instance.getUnreservedConcurrencyMin();
            this.hotReload = instance.getHotReload();
            this.awsConfigPath = instance.getAwsConfigPath();
        }

        /**
         * Sets whether Lambda containers are removed after each invocation.
         *
         * @param ephemeral {@code true} to remove containers after each invocation (default {@value DEFAULT_EPHEMERAL})
         * @return this builder
         */
        public Builder ephemeral(boolean ephemeral) {
            this.ephemeral = ephemeral;
            return this;
        }

        /**
         * Sets whether the Lambda Runtime API ports should be exposed on the host machine.
         *
         * @param exposeRuntimePorts {@code true} to expose Runtime API ports (default {@value DEFAULT_EXPOSE_RUNTIME_PORTS})
         * @return this builder
         */
        public Builder exposeRuntimePorts(boolean exposeRuntimePorts) {
            this.exposeRuntimePorts = exposeRuntimePorts;
            return this;
        }

        /**
         * Sets the default memory size for Lambda functions in megabytes.
         *
         * @param defaultMemoryMb memory in MB (default {@value DEFAULT_MEMORY_MB})
         * @return this builder
         */
        public Builder defaultMemoryMb(int defaultMemoryMb) {
            this.defaultMemoryMb = defaultMemoryMb;
            return this;
        }

        /**
         * Sets the default timeout for Lambda function invocations in seconds.
         *
         * @param defaultTimeoutSeconds timeout in seconds (default {@value DEFAULT_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder defaultTimeoutSeconds(int defaultTimeoutSeconds) {
            this.defaultTimeoutSeconds = defaultTimeoutSeconds;
            return this;
        }

        /**
         * Sets the Docker network that Lambda containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use default network
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the port range for the Lambda Runtime API.
         *
         * @param basePort the base port (default {@value DEFAULT_RUNTIME_API_BASE_PORT})
         * @param amount   the amount of ports (default {@value DEFAULT_RUNTIME_API_PORTS_COUNT})
         * @return this builder
         */
        public Builder runtimeApiPortRange(int basePort, int amount) {
            this.runtimeApiBasePort = basePort;
            this.runtimeApiPortsCount = amount;
            return this;
        }

        /**
         * Sets the poll interval in milliseconds for Lambda container status checks.
         *
         * @param pollIntervalMs interval in milliseconds (default {@value DEFAULT_POLL_INTERVAL_MS})
         * @return this builder
         */
        public Builder pollIntervalMs(int pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
            return this;
        }

        /**
         * Sets the idle timeout in seconds after which unused Lambda containers are cleaned up.
         *
         * @param containerIdleTimeoutSeconds timeout in seconds (default {@value DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder containerIdleTimeoutSeconds(int containerIdleTimeoutSeconds) {
            this.containerIdleTimeoutSeconds = containerIdleTimeoutSeconds;
            return this;
        }

        /**
         * Sets the per-region concurrent executions ceiling. AWS Lambda's account-level
         * concurrency is per-region (default 1000); Floci mirrors that semantics.
         *
         * @param regionConcurrencyLimit the concurrency limit (default {@value DEFAULT_REGION_CONCURRENCY_LIMIT})
         * @return this builder
         */
        public Builder regionConcurrencyLimit(int regionConcurrencyLimit) {
            this.regionConcurrencyLimit = regionConcurrencyLimit;
            return this;
        }

        /**
         * Sets the minimum unreserved concurrency that must remain after
         * {@code PutFunctionConcurrency}. Puts that would leave less than this are rejected.
         *
         * @param unreservedConcurrencyMin the minimum (default {@value DEFAULT_UNRESERVED_CONCURRENCY_MIN})
         * @return this builder
         */
        public Builder unreservedConcurrencyMin(int unreservedConcurrencyMin) {
            this.unreservedConcurrencyMin = unreservedConcurrencyMin;
            return this;
        }

        /**
         * Sets the hot-reload configuration for development workflows.
         *
         * @param enabled whether hot-reload is enabled
         * @return this builder
         */
        public Builder hotReload(boolean enabled) {
            this.hotReload = new DefaultHotReload(enabled, null);
            return this;
        }

        /**
         * Sets the hot-reload configuration with allowed paths.
         *
         * @param enabled whether hot-reload is enabled
         * @param allowedPaths optional list of allowed path prefixes
         * @return this builder
         */
        public Builder hotReload(boolean enabled, List<String> allowedPaths) {
            this.hotReload = new DefaultHotReload(enabled, allowedPaths);
            return this;
        }

        /**
         * Sets the host path to bind-mount (read-only) into Lambda containers at
         * {@code /opt/aws-config}. When set, no AWS credential env vars are injected;
         * instead {@code AWS_SHARED_CREDENTIALS_FILE} and {@code AWS_CONFIG_FILE} are
         * pointed at the mounted files. Blank values are treated as absent.
         *
         * @param awsConfigPath the host path, or {@code null} / blank to unset
         * @return this builder
         */
        public Builder awsConfigPath(String awsConfigPath) {
            this.awsConfigPath = awsConfigPath;
            return this;
        }

        /**
         * Creates an immutable {@link LambdaConfig} from this builder.
         *
         * @return the Lambda configuration
         */
        public LambdaConfig build() {
            return new LambdaConfig(this);
        }
    }

    /**
     * Default implementation of {@link HotReload}.
     */
    private record DefaultHotReload(boolean enabled, List<String> allowedPathsList) implements HotReload {
        @Override
        public Optional<List<String>> allowedPaths() {
            return Optional.ofNullable(allowedPathsList);
        }
    }
}
