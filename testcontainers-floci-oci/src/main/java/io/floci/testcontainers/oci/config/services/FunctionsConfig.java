package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Functions of Floci OCI.
 *
 * <p>The management plane (applications and functions) is always emulated by Floci OCI itself. Real
 * invocations are delegated to a shared <a href="https://fnproject.io">Fn Project</a> server that Floci OCI
 * starts lazily as a sibling container on the first invocation and publishes on the Docker host. In
 * {@code mock} mode no container is started and invocations return a synthetic body.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FunctionsConfig config = FunctionsConfig.builder()
 *     .mock(true)
 *     .serverImage("fnproject/fnserver:0.3.749")
 *     .serverPortRange(18085, 5)
 *     .build();
 * }</pre>
 */
public class FunctionsConfig extends AbstractServiceConfig<FunctionsConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_SERVER_IMAGE = "fnproject/fnserver:latest";
    private static final int DEFAULT_SERVER_BASE_PORT = 8085;
    private static final int DEFAULT_SERVER_PORTS_COUNT = 10;
    private static final int DEFAULT_STARTUP_TIMEOUT_SECONDS = 120;

    private final boolean mock;
    private final String serverImage;
    private final int serverBasePort;
    private final int serverPortsCount;
    private final int startupTimeoutSeconds;

    private FunctionsConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.serverImage = builder.serverImage;
        this.serverBasePort = builder.serverBasePort;
        this.serverPortsCount = builder.serverPortsCount;
        this.startupTimeoutSeconds = builder.startupTimeoutSeconds;
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
     * Returns whether no Docker container is started: the management plane works fully and invocations return a
     * synthetic body.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Fn Project server image backing real invocations.
     *
     * @return the Docker image
     */
    public String getServerImage() {
        return serverImage;
    }

    /**
     * Returns the first port of the host port range the Fn server is published on.
     *
     * @return the base port
     */
    public int getServerBasePort() {
        return serverBasePort;
    }

    /**
     * Returns the number of ports of the host port range the Fn server is published on, starting from {@link
     * #getServerBasePort()}.
     *
     * @return the number of ports
     */
    public int getServerPortsCount() {
        return serverPortsCount;
    }

    /**
     * Returns the last port of the host port range the Fn server is published on.
     *
     * @return the maximum port
     */
    public int getServerMaxPort() {
        return serverBasePort + serverPortsCount - 1;
    }

    /**
     * Returns how long the first invocation waits for the Fn server to become ready.
     *
     * @return the startup timeout in seconds
     */
    public int getStartupTimeoutSeconds() {
        return startupTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_IMAGE", serverImage);
            container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_BASE_PORT", String.valueOf(serverBasePort));
            container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_SERVER_MAX_PORT", String.valueOf(getServerMaxPort()));
            container.withEnv("FLOCI_OCI_SERVICES_FUNCTIONS_STARTUP_TIMEOUT_SECONDS", String.valueOf(startupTimeoutSeconds));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link FunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FunctionsConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String serverImage = DEFAULT_SERVER_IMAGE;
        private int serverBasePort = DEFAULT_SERVER_BASE_PORT;
        private int serverPortsCount = DEFAULT_SERVER_PORTS_COUNT;
        private int startupTimeoutSeconds = DEFAULT_STARTUP_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via FunctionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FunctionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FunctionsConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.serverImage = instance.serverImage;
            this.serverBasePort = instance.serverBasePort;
            this.serverPortsCount = instance.serverPortsCount;
            this.startupTimeoutSeconds = instance.startupTimeoutSeconds;
        }

        /**
         * Sets whether no Docker container is started: the management plane works fully and invocations return a
         * synthetic body. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Fn Project server image backing real invocations.
         *
         * @param serverImage the Docker image (default {@value DEFAULT_SERVER_IMAGE})
         * @return this builder
         */
        public Builder serverImage(String serverImage) {
            this.serverImage = serverImage;
            return this;
        }

        /**
         * Sets the host port range the Fn server is published on. Floci itself allows 8085-8095; this module
         * defaults to 10 ports. Floci OCI runs a single shared Fn server.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_SERVER_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_SERVER_PORTS_COUNT})
         * @return this builder
         */
        public Builder serverPortRange(int basePort, int amount) {
            this.serverBasePort = basePort;
            this.serverPortsCount = amount;
            return this;
        }

        /**
         * Sets how long the first invocation waits for the Fn server to become ready.
         *
         * @param startupTimeoutSeconds the startup timeout in seconds (default {@value DEFAULT_STARTUP_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder startupTimeoutSeconds(int startupTimeoutSeconds) {
            this.startupTimeoutSeconds = startupTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link FunctionsConfig} from this builder.
         *
         * @return the Functions configuration
         */
        @Override
        public FunctionsConfig build() {
            return new FunctionsConfig(this);
        }
    }
}
