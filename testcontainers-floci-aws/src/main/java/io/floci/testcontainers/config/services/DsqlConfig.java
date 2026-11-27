package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Aurora DSQL-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DsqlConfig config = DsqlConfig.builder()
 *     .image("postgres:16")
 *     .build();
 * }</pre>
 */
public class DsqlConfig extends AbstractServiceConfig<DsqlConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_PORT = 5432;
    private static final String DEFAULT_ENDPOINT_SUFFIX = "localhost.floci.io";
    private static final String DEFAULT_IMAGE = "postgres:16-alpine";

    private final boolean mock;
    private final int port;
    private final String endpointSuffix;
    private final String image;
    private final String dockerNetwork;

    private DsqlConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.port = builder.port;
        this.endpointSuffix = builder.endpointSuffix;
        this.image = builder.image;
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
     * Returns whether clusters are mocked: they have no database and are {@code ACTIVE} at once, without Docker.
     *
     * @return whether clusters are mocked: they have no database and are {@code ACTIVE} at once, without Docker
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the port inside the container every cluster endpoint is served on.
     *
     * <p>As on AWS, where a DSQL endpoint is always reached on 5432. Clients connect to {@code <id>.dsql.<region>.<endpoint-suffix>} with TLS, and the server name they send picks the cluster. The port is exposed on the container while DSQL is enabled and not mocked; use {@code getMappedPort(port)} to reach it.
     *
     * @return the port inside the container every cluster endpoint is served on
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the domain cluster endpoints are named under, {@code <id>.dsql.<region>.<suffix>}.
     *
     * <p>The default resolves to the loopback address on the host and to Floci inside its Docker network, so an endpoint works unchanged in both.
     *
     * @return the domain cluster endpoints are named under, {@code <id>.dsql.<region>.<suffix>}
     */
    public String getEndpointSuffix() {
        return endpointSuffix;
    }

    /**
     * Returns the PostgreSQL image each cluster's database runs in.
     *
     * <p>DSQL is PostgreSQL 16 compatible.
     *
     * @return the PostgreSQL image each cluster's database runs in
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the Docker network the database containers join.
     *
     * <p>Floci uses its own network while unset.
     *
     * @return the Docker network the database containers join, or empty to keep Floci's default
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_DSQL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_DSQL_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_DSQL_PORT", String.valueOf(port));
            container.withEnv("FLOCI_SERVICES_DSQL_ENDPOINT_SUFFIX", endpointSuffix);
            container.withEnv("FLOCI_SERVICES_DSQL_IMAGE", image);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_DSQL_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    /**
     * Exposes the cluster endpoint port while enabled and not mocked, so that clients outside the container can
     * connect to the clusters' databases via its mapped port.
     */
    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled() && !mock) {
            container.addExposedPorts(port);
        }
    }

    /**
     * Returns {@code true} while enabled and not mocked, since each cluster's database runs in a PostgreSQL
     * container.
     */
    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link DsqlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DsqlConfig> {

        private boolean mock = DEFAULT_MOCK;
        private int port = DEFAULT_PORT;
        private String endpointSuffix = DEFAULT_ENDPOINT_SUFFIX;
        private String image = DEFAULT_IMAGE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via DsqlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DsqlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DsqlConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.port = instance.getPort();
            this.endpointSuffix = instance.getEndpointSuffix();
            this.image = instance.getImage();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
        }

        /**
         * Sets whether clusters are mocked: they have no database and are {@code ACTIVE} at once, without Docker.
         *
         * @param mock whether clusters are mocked: they have no database and are {@code ACTIVE} at once, without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the port inside the container every cluster endpoint is served on.
         *
         * <p>As on AWS, where a DSQL endpoint is always reached on 5432. Clients connect to {@code <id>.dsql.<region>.<endpoint-suffix>} with TLS, and the server name they send picks the cluster. The port is exposed on the container while DSQL is enabled and not mocked; use {@code getMappedPort(port)} to reach it.
         *
         * @param port the port inside the container every cluster endpoint is served on (default {@value DEFAULT_PORT})
         * @return this builder
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets the domain cluster endpoints are named under, {@code <id>.dsql.<region>.<suffix>}.
         *
         * <p>The default resolves to the loopback address on the host and to Floci inside its Docker network, so an endpoint works unchanged in both.
         *
         * @param endpointSuffix the domain cluster endpoints are named under, {@code <id>.dsql.<region>.<suffix>} (default {@value DEFAULT_ENDPOINT_SUFFIX})
         * @return this builder
         */
        public Builder endpointSuffix(String endpointSuffix) {
            this.endpointSuffix = endpointSuffix;
            return this;
        }

        /**
         * Sets the PostgreSQL image each cluster's database runs in.
         *
         * <p>DSQL is PostgreSQL 16 compatible.
         *
         * @param image the PostgreSQL image each cluster's database runs in (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets the Docker network the database containers join.
         *
         * <p>Floci uses its own network while unset.
         *
         * @param dockerNetwork the Docker network the database containers join, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link DsqlConfig} from this builder.
         *
         * @return the Aurora DSQL configuration
         */
        @Override
        public DsqlConfig build() {
            return new DsqlConfig(this);
        }
    }
}
