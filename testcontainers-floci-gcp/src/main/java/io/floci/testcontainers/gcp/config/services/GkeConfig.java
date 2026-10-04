package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Google Kubernetes Engine (GKE) of Floci GCP.
 *
 * <p>Each cluster is backed by a privileged k3s container whose API server Floci GCP publishes on the Docker host
 * (see {@link Builder#apiServerPortRange(int, int)}), so the port is reachable from the host without exposing it on
 * the Floci GCP container. In {@code mock} mode only the control plane is emulated and no k3s containers are
 * started.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GkeConfig config = GkeConfig.builder()
 *     .mock(true)
 *     .defaultImage("rancher/k3s:v1.31.4-k3s1")
 *     .apiServerPortRange(16550, 5)
 *     .build();
 * }</pre>
 */
public class GkeConfig extends AbstractServiceConfig<GkeConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "rancher/k3s:latest";
    private static final int DEFAULT_API_SERVER_BASE_PORT = 6550;
    private static final int DEFAULT_API_SERVER_PORTS_COUNT = 10;
    private static final boolean DEFAULT_KEEP_RUNNING_ON_SHUTDOWN = false;
    private static final String DEFAULT_ENDPOINT_MODE = "host";

    private final boolean mock;
    private final String defaultImage;
    private final int apiServerBasePort;
    private final int apiServerPortsCount;
    private final boolean keepRunningOnShutdown;
    private final String endpointMode;
    private final String dockerNetwork;

    private GkeConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.apiServerBasePort = builder.apiServerBasePort;
        this.apiServerPortsCount = builder.apiServerPortsCount;
        this.keepRunningOnShutdown = builder.keepRunningOnShutdown;
        this.endpointMode = builder.endpointMode;
        this.dockerNetwork = builder.dockerNetwork;
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
     * Returns whether no k3s container is started; clusters are only emulated as control plane
     * resources.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker image for the k3s container.
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the first port of the host port range of the k3s API servers.
     *
     * @return the base port
     */
    public int getApiServerBasePort() {
        return apiServerBasePort;
    }

    /**
     * Returns the number of ports of the host port range of the k3s API servers, starting from {@link
     * #getApiServerBasePort()}.
     *
     * @return the number of ports
     */
    public int getApiServerPortsCount() {
        return apiServerPortsCount;
    }

    /**
     * Returns the last port of the host port range of the k3s API servers.
     *
     * @return the maximum port
     */
    public int getApiServerMaxPort() {
        return apiServerBasePort + apiServerPortsCount - 1;
    }

    /**
     * Returns whether k3s containers are left running when Floci GCP shuts down.
     *
     * @return {@code true} if k3s containers survive a shutdown
     */
    public boolean isKeepRunningOnShutdown() {
        return keepRunningOnShutdown;
    }

    /**
     * Returns how the endpoint of a cluster is advertised: {@code host} (a {@code localhost:<port>} address
     * reachable from the Docker host) or {@code network} (the k3s container's address on its Docker network, for
     * clients running in Docker themselves).
     *
     * @return the endpoint mode
     */
    public String getEndpointMode() {
        return endpointMode;
    }

    /**
     * Returns the Docker network the k3s containers join, or empty if not set.
     *
     * @return the Docker network, or empty
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_GKE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_GKE_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_GKE_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_GCP_SERVICES_GKE_API_SERVER_BASE_PORT", String.valueOf(apiServerBasePort));
            container.withEnv("FLOCI_GCP_SERVICES_GKE_API_SERVER_MAX_PORT", String.valueOf(getApiServerMaxPort()));
            container.withEnv("FLOCI_GCP_SERVICES_GKE_KEEP_RUNNING_ON_SHUTDOWN", String.valueOf(keepRunningOnShutdown));
            container.withEnv("FLOCI_GCP_SERVICES_GKE_ENDPOINT_MODE", endpointMode);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_GCP_SERVICES_GKE_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link GkeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GkeConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private int apiServerBasePort = DEFAULT_API_SERVER_BASE_PORT;
        private int apiServerPortsCount = DEFAULT_API_SERVER_PORTS_COUNT;
        private boolean keepRunningOnShutdown = DEFAULT_KEEP_RUNNING_ON_SHUTDOWN;
        private String endpointMode = DEFAULT_ENDPOINT_MODE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via GkeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GkeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GkeConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.defaultImage = instance.defaultImage;
            this.apiServerBasePort = instance.apiServerBasePort;
            this.apiServerPortsCount = instance.apiServerPortsCount;
            this.keepRunningOnShutdown = instance.keepRunningOnShutdown;
            this.endpointMode = instance.endpointMode;
            this.dockerNetwork = instance.dockerNetwork;
        }

        /**
         * Sets whether no k3s container is started; clusters are only emulated as control plane
         * resources. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Docker image for the k3s container.
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range the k3s API servers are published on. Floci GCP defaults to
         * 6550-6599; this module defaults to 10 ports, i.e. 10 concurrent clusters.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_API_SERVER_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_API_SERVER_PORTS_COUNT})
         * @return this builder
         */
        public Builder apiServerPortRange(int basePort, int amount) {
            this.apiServerBasePort = basePort;
            this.apiServerPortsCount = amount;
            return this;
        }

        /**
         * Sets whether k3s containers are left running when Floci GCP shuts down.
         *
         * @param keepRunningOnShutdown {@code true} to keep k3s containers running (default {@value DEFAULT_KEEP_RUNNING_ON_SHUTDOWN})
         * @return this builder
         */
        public Builder keepRunningOnShutdown(boolean keepRunningOnShutdown) {
            this.keepRunningOnShutdown = keepRunningOnShutdown;
            return this;
        }

        /**
         * Sets how the endpoint of a cluster is advertised: {@code host} (a {@code localhost:<port>} address
         * reachable from the Docker host) or {@code network} (the k3s container's address on its Docker network,
         * for clients running in Docker themselves).
         *
         * @param endpointMode {@code host} or {@code network} (default {@value DEFAULT_ENDPOINT_MODE})
         * @return this builder
         */
        public Builder endpointMode(String endpointMode) {
            this.endpointMode = endpointMode;
            return this;
        }

        /**
         * Sets the Docker network the k3s containers join. When unset, Floci GCP falls back to the shared services
         * network (see {@code FlociGcpContainer#withDedicatedNetwork()}) and then to the network of the Floci GCP
         * container itself.
         *
         * @param dockerNetwork the Docker network, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link GkeConfig} from this builder.
         *
         * @return the GKE configuration
         */
        @Override
        public GkeConfig build() {
            return new GkeConfig(this);
        }
    }
}
