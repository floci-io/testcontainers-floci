package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for RDS-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RdsConfig config = RdsConfig.builder()
 *     .enabled(true)
 *     .mock(true)
 *     .proxyPortRange(7000, 100)
 *     .defaultPostgresImage("postgres:16-alpine")
 *     .build();
 * }</pre>
 */
public class RdsConfig extends AbstractServiceConfig<RdsConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_PROXY_BASE_PORT = 7000;
    private static final int DEFAULT_PROXY_PORTS_COUNT = 10;

    private final boolean mock;
    private final int proxyBasePort;
    private final int proxyPortsCount;
    private final String defaultPostgresImage;
    private final String defaultMysqlImage;
    private final String defaultMariadbImage;
    private final String dockerNetwork;
    private final String endpointHost;

    private RdsConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.proxyBasePort = builder.proxyBasePort;
        this.proxyPortsCount = builder.proxyPortsCount;
        this.defaultPostgresImage = builder.defaultPostgresImage;
        this.defaultMysqlImage = builder.defaultMysqlImage;
        this.defaultMariadbImage = builder.defaultMariadbImage;
        this.dockerNetwork = builder.dockerNetwork;
        this.endpointHost = builder.endpointHost;
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
     * Returns whether DB clusters and instances are created instantly without a real Docker
     * container or auth proxy (API/metadata only). Useful for CI and environments without
     * access to the Docker socket.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the base port for the RDS proxy port range.
     *
     * @return the base port
     */
    public int getProxyBasePort() {
        return proxyBasePort;
    }

    /**
     * Returns the number of ports allocated for the RDS proxy, starting from {@link #getProxyBasePort()}.
     *
     * @return the number of proxy ports
     */
    public int getProxyPortsCount() {
        return proxyPortsCount;
    }

    /**
     * Returns the maximum port for the RDS proxy port range.
     *
     * @return the maximum port
     */
    public int getProxyMaxPort() {
        return proxyBasePort + proxyPortsCount - 1;
    }

    /**
     * Returns the default Docker image used for PostgreSQL RDS instances, or {@code null} if not
     * set, in which case Floci uses its own configured default image.
     *
     * @return the PostgreSQL image name, or {@code null}
     */
    public String getDefaultPostgresImage() {
        return defaultPostgresImage;
    }

    /**
     * Returns the default Docker image used for MySQL RDS instances, or {@code null} if not set,
     * in which case Floci uses its own configured default image.
     *
     * @return the MySQL image name, or {@code null}
     */
    public String getDefaultMysqlImage() {
        return defaultMysqlImage;
    }

    /**
     * Returns the default Docker image used for MariaDB RDS instances, or {@code null} if not
     * set, in which case Floci uses its own configured default image.
     *
     * @return the MariaDB image name, or {@code null}
     */
    public String getDefaultMariadbImage() {
        return defaultMariadbImage;
    }

    /**
     * Returns the Docker network used for RDS database containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    /**
     * Returns the hostname advertised for RDS endpoints, or {@code null} if not set. When not
     * set, the container falls back to its Docker host so that clients can reach the mapped
     * proxy ports.
     *
     * @return the endpoint host, or {@code null}
     */
    public String getEndpointHost() {
        return endpointHost;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_RDS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_RDS_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_RDS_PROXY_BASE_PORT", String.valueOf(proxyBasePort));
            container.withEnv("FLOCI_SERVICES_RDS_PROXY_MAX_PORT", String.valueOf(getProxyMaxPort()));

            if (defaultPostgresImage != null) {
                container.withEnv("FLOCI_SERVICES_RDS_DEFAULT_POSTGRES_IMAGE", defaultPostgresImage);
            }
            if (defaultMysqlImage != null) {
                container.withEnv("FLOCI_SERVICES_RDS_DEFAULT_MYSQL_IMAGE", defaultMysqlImage);
            }
            if (defaultMariadbImage != null) {
                container.withEnv("FLOCI_SERVICES_RDS_DEFAULT_MARIADB_IMAGE", defaultMariadbImage);
            }
            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_RDS_DOCKER_NETWORK", dockerNetwork);
            }
            // RDS clients connect to the endpoint returned by the AWS API, so it must point at
            // a host that Testcontainers clients can actually reach. Use the caller-provided
            // value when set, otherwise fall back to the Docker host that publishes the mapped
            // proxy ports.
            container.withEnv("FLOCI_SERVICES_RDS_ENDPOINT_HOST",
                    endpointHost != null ? endpointHost : container.getHost());
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            // Expose ports of RDS to make them accessible by the user
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
     * Builder for {@link RdsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RdsConfig> {

        private boolean mock = DEFAULT_MOCK;
        private int proxyBasePort = DEFAULT_PROXY_BASE_PORT;
        private int proxyPortsCount = DEFAULT_PROXY_PORTS_COUNT;
        private String defaultPostgresImage;
        private String defaultMysqlImage;
        private String defaultMariadbImage;
        private String dockerNetwork;
        private String endpointHost;

        private Builder() {
            // Allow instantiation only via RdsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RdsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RdsConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.proxyBasePort = instance.getProxyBasePort();
            this.proxyPortsCount = instance.getProxyPortsCount();
            this.defaultPostgresImage = instance.getDefaultPostgresImage();
            this.defaultMysqlImage = instance.getDefaultMysqlImage();
            this.defaultMariadbImage = instance.getDefaultMariadbImage();
            this.dockerNetwork = instance.getDockerNetwork();
            this.endpointHost = instance.getEndpointHost();
        }

        /**
         * Sets whether DB clusters and instances are created instantly without a real Docker
         * container or auth proxy (API/metadata only). Useful for CI and environments without
         * access to the Docker socket.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the port range for the RDS proxy.
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
         * Sets the default Docker image for PostgreSQL RDS instances.
         *
         * @param defaultPostgresImage the image name, or {@code null} (the default) to use
         *                             Floci's own configured default image
         * @return this builder
         */
        public Builder defaultPostgresImage(String defaultPostgresImage) {
            this.defaultPostgresImage = defaultPostgresImage;
            return this;
        }

        /**
         * Sets the default Docker image for MySQL RDS instances.
         *
         * @param defaultMysqlImage the image name, or {@code null} (the default) to use
         *                          Floci's own configured default image
         * @return this builder
         */
        public Builder defaultMysqlImage(String defaultMysqlImage) {
            this.defaultMysqlImage = defaultMysqlImage;
            return this;
        }

        /**
         * Sets the default Docker image for MariaDB RDS instances.
         *
         * @param defaultMariadbImage the image name, or {@code null} (the default) to use
         *                            Floci's own configured default image
         * @return this builder
         */
        public Builder defaultMariadbImage(String defaultMariadbImage) {
            this.defaultMariadbImage = defaultMariadbImage;
            return this;
        }

        /**
         * Sets the Docker network that RDS database containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the hostname advertised for RDS endpoints. When {@code null}, the container
         * advertises its Docker host so that clients can reach the mapped proxy ports.
         *
         * @param endpointHost the endpoint host, or {@code null} to use the container's Docker host
         * @return this builder
         */
        public Builder endpointHost(String endpointHost) {
            this.endpointHost = endpointHost;
            return this;
        }

        /**
         * Creates an immutable {@link RdsConfig} from this builder.
         *
         * @return the RDS configuration
         */
        @Override
        public RdsConfig build() {
            return new RdsConfig(this);
        }
    }
}
