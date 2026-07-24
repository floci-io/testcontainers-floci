package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

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
public class MemoryDbConfig extends AbstractServiceConfig {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_PROXY_BASE_PORT = 6400;
    private static final int DEFAULT_PROXY_PORTS_COUNT = 10;
    private static final String DEFAULT_IMAGE = "valkey/valkey:8";

    private final boolean mock;
    private final int proxyBasePort;
    private final int proxyPortsCount;
    private final String defaultImage;

    private MemoryDbConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.proxyBasePort = builder.proxyBasePort;
        this.proxyPortsCount = builder.proxyPortsCount;
        this.defaultImage = builder.defaultImage;
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_MEMORYDB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_MEMORYDB_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT", String.valueOf(proxyBasePort));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_PROXY_MAX_PORT", String.valueOf(getProxyMaxPort()));
            container.withEnv("FLOCI_SERVICES_MEMORYDB_DEFAULT_IMAGE", defaultImage);
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

    /**
     * Builder for {@link MemoryDbConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private boolean mock = DEFAULT_MOCK;
        private int proxyBasePort = DEFAULT_PROXY_BASE_PORT;
        private int proxyPortsCount = DEFAULT_PROXY_PORTS_COUNT;
        private String defaultImage = DEFAULT_IMAGE;

        private Builder() {
            // Allow instantiation only via MemoryDbConfig.builder()
        }

        /**
         * Enables or disables the MemoryDB service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
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
         * Creates an immutable {@link MemoryDbConfig} from this builder.
         *
         * @return the MemoryDB configuration
         */
        public MemoryDbConfig build() {
            return new MemoryDbConfig(this);
        }
    }
}
