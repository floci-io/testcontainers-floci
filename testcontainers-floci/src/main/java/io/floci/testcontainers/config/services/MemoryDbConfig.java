package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for MemoryDB-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MemoryDbConfig config = MemoryDbConfig.builder()
 *     .enabled(true)
 *     .mock(false)
 *     .proxyPortRange(6400, 20)
 *     .defaultImage("valkey/valkey:8")
 *     .build();
 * }</pre>
 */
public class MemoryDbConfig extends AbstractServiceConfig<MemoryDbConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_PROXY_BASE_PORT = 6400;
    private static final int DEFAULT_PROXY_PORTS_COUNT = 10;
    private static final String DEFAULT_IMAGE = "valkey/valkey:8";

    private final boolean mock;
    private final int proxyBasePort;
    private final int proxyPortsCount;
    private final String defaultImage;
    private final String dockerNetwork;

    private MemoryDbConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.proxyBasePort = builder.proxyBasePort;
        this.proxyPortsCount = builder.proxyPortsCount;
        this.defaultImage = builder.defaultImage;
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
     * Returns whether MemoryDB clusters are simulated in-memory without real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the base port for the MemoryDB proxy port range.
     *
     * @return the base port
     */
    public int getProxyBasePort() {
        return proxyBasePort;
    }

    /**
     * Returns the number of ports allocated for the MemoryDB proxy, starting from {@link #getProxyBasePort()}.
     *
     * @return the number of proxy ports
     */
    public int getProxyPortsCount() {
        return proxyPortsCount;
    }

    /**
     * Returns the maximum port for the MemoryDB proxy port range.
     *
     * @return the maximum port
     */
    public int getProxyMaxPort() {
        return proxyBasePort + proxyPortsCount - 1;
    }

    /**
     * Returns the default Docker image used for MemoryDB (Valkey) instances.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the Docker network to attach MemoryDB containers to, or {@link Optional#empty()}
     * if the default bridge network is used.
     *
     * @return the Docker network, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_MEMORYDB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_MEMORYDB_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT", String.valueOf(proxyBasePort));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_PROXY_MAX_PORT", String.valueOf(getProxyMaxPort()));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_DEFAULT_IMAGE", defaultImage);
            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_MEMORYDB_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            // Expose ports of MemoryDB to make them accessible by the user
            for (int port = proxyBasePort; port <= getProxyMaxPort(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link MemoryDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MemoryDbConfig> {

        private boolean mock = DEFAULT_MOCK;
        private int proxyBasePort = DEFAULT_PROXY_BASE_PORT;
        private int proxyPortsCount = DEFAULT_PROXY_PORTS_COUNT;
        private String defaultImage = DEFAULT_IMAGE;
        private String dockerNetwork = null;

        private Builder() {
            // Allow instantiation only via MemoryDbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MemoryDbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MemoryDbConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.proxyBasePort = instance.getProxyBasePort();
            this.proxyPortsCount = instance.getProxyPortsCount();
            this.defaultImage = instance.getDefaultImage();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
        }

        /**
         * Sets whether MemoryDB clusters are simulated in-memory without real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the port range for the MemoryDB proxy.
         *
         * @param basePort the base port (default {@value DEFAULT_PROXY_BASE_PORT})
         * @param amount   the amount of ports (default {@value DEFAULT_PROXY_PORTS_COUNT})
         * @return this builder
         */
        public Builder proxyPortRange(int basePort, int amount) {
            this.proxyBasePort = basePort;
            this.proxyPortsCount = amount;
            return this;
        }

        /**
         * Sets the default Docker image for MemoryDB (Valkey) instances.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the Docker network to attach MemoryDB containers to.
         *
         * @param dockerNetwork the Docker network name, or {@code null} to use the default bridge network
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link MemoryDbConfig} from this builder.
         *
         * @return the MemoryDB configuration
         */
        @Override
        public MemoryDbConfig build() {
            return new MemoryDbConfig(this);
        }
    }
}
