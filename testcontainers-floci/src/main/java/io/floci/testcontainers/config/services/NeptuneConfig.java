package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Neptune-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * NeptuneConfig config = NeptuneConfig.builder()
 *     .enabled(true)
 *     .proxyPortRange(8182, 10)
 *     .defaultImage("tinkerpop/gremlin-server:3.7.3")
 *     .build();
 * }</pre>
 */
public class NeptuneConfig extends AbstractServiceConfig<NeptuneConfig.Builder> {

    private static final int DEFAULT_PROXY_BASE_PORT = 8182;
    private static final int DEFAULT_PROXY_PORTS_COUNT = 10;
    private static final String DEFAULT_DB_TYPE = "gremlin";
    private static final String DEFAULT_IMAGE = "tinkerpop/gremlin-server:3.7.3";
    private static final String DEFAULT_NEO4J_IMAGE = "neo4j:5-community";

    private final int proxyBasePort;
    private final int proxyPortsCount;
    private final String dbType;
    private final String defaultImage;
    private final String defaultNeo4jImage;
    private final String dockerNetwork;

    private NeptuneConfig(Builder builder) {
        super(builder.enabled);
        this.proxyBasePort = builder.proxyBasePort;
        this.proxyPortsCount = builder.proxyPortsCount;
        this.dbType = builder.dbType;
        this.defaultImage = builder.defaultImage;
        this.defaultNeo4jImage = builder.defaultNeo4jImage;
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
     * Returns the base port for the Neptune proxy port range.
     *
     * @return the base port
     */
    public int getProxyBasePort() {
        return proxyBasePort;
    }

    /**
     * Returns the number of ports allocated for the Neptune proxy, starting from {@link #getProxyBasePort()}.
     *
     * @return the number of proxy ports
     */
    public int getProxyPortsCount() {
        return proxyPortsCount;
    }

    /**
     * Returns the inclusive upper bound of the Neptune proxy port range.
     *
     * @return the maximum port
     */
    public int getProxyMaxPort() {
        return proxyBasePort + proxyPortsCount - 1;
    }

    /**
     * Returns the backend graph engine and query language used for Neptune: {@code gremlin}
     * (Apache TinkerPop, Gremlin over WebSocket) or {@code neo4j} (Neo4j, openCypher over Bolt).
     * Mirrors LocalStack's {@code NEPTUNE_DB_TYPE}.
     *
     * @return the database type
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * Returns the default Docker image used when {@link #getDbType()} is {@code gremlin}.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the default Docker image used when {@link #getDbType()} is {@code neo4j}
     * (openCypher / Bolt).
     *
     * @return the image name
     */
    public String getDefaultNeo4jImage() {
        return defaultNeo4jImage;
    }

    /**
     * Returns the Docker network used for Neptune containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_NEPTUNE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_NEPTUNE_PROXY_BASE_PORT", String.valueOf(proxyBasePort));
            container.withEnv("FLOCI_SERVICES_NEPTUNE_PROXY_MAX_PORT", String.valueOf(getProxyMaxPort()));
            container.withEnv("FLOCI_SERVICES_NEPTUNE_DB_TYPE", dbType);
            container.withEnv("FLOCI_SERVICES_NEPTUNE_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_SERVICES_NEPTUNE_DEFAULT_NEO4J_IMAGE", defaultNeo4jImage);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_NEPTUNE_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            for (int port = proxyBasePort; port <= getProxyMaxPort(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled();
    }

    /**
     * Builder for {@link NeptuneConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, NeptuneConfig> {

        private int proxyBasePort = DEFAULT_PROXY_BASE_PORT;
        private int proxyPortsCount = DEFAULT_PROXY_PORTS_COUNT;
        private String dbType = DEFAULT_DB_TYPE;
        private String defaultImage = DEFAULT_IMAGE;
        private String defaultNeo4jImage = DEFAULT_NEO4J_IMAGE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via NeptuneConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link NeptuneConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(NeptuneConfig instance) {
            super(instance);
            this.proxyBasePort = instance.getProxyBasePort();
            this.proxyPortsCount = instance.getProxyPortsCount();
            this.dbType = instance.getDbType();
            this.defaultImage = instance.getDefaultImage();
            this.defaultNeo4jImage = instance.getDefaultNeo4jImage();
            this.dockerNetwork = instance.getDockerNetwork();
        }

        /**
         * Sets the port range for the Neptune proxy.
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
         * Sets the backend graph engine and query language for Neptune: {@code gremlin}
         * (Apache TinkerPop, Gremlin over WebSocket) or {@code neo4j} (Neo4j, openCypher over
         * Bolt). Mirrors LocalStack's {@code NEPTUNE_DB_TYPE}.
         *
         * @param dbType the database type (default {@value DEFAULT_DB_TYPE})
         * @return this builder
         */
        public Builder dbType(String dbType) {
            this.dbType = dbType;
            return this;
        }

        /**
         * Sets the default Docker image for Neptune (Gremlin Server) instances, used when
         * {@code db-type=gremlin}.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the default Docker image for Neptune (Neo4j / openCypher) instances, used when
         * {@code db-type=neo4j}.
         *
         * @param defaultNeo4jImage the image name (default {@value DEFAULT_NEO4J_IMAGE})
         * @return this builder
         */
        public Builder defaultNeo4jImage(String defaultNeo4jImage) {
            this.defaultNeo4jImage = defaultNeo4jImage;
            return this;
        }

        /**
         * Sets the Docker network that Neptune containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link NeptuneConfig} from this builder.
         *
         * @return the Neptune configuration
         */
        @Override
        public NeptuneConfig build() {
            return new NeptuneConfig(this);
        }
    }
}
