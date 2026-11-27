package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Database with PostgreSQL ({@code psql}) of Floci OCI.
 *
 * <p>Each DB system is backed by its own PostgreSQL container that Floci OCI starts as a sibling container and
 * publishes on the Docker host, one host port per DB system. A DB system stays {@code CREATING} until PostgreSQL
 * accepts connections. In {@code mock} mode no container is started: DB systems are management-plane only and
 * become {@code ACTIVE} at once.
 *
 * <p>While Floci OCI runs in a container, {@code GetConnectionDetails} returns the PostgreSQL container's name and
 * port {@code 5432}, for clients on the same Docker network. Clients on the Docker host (e.g. the test itself) need
 * the published host port instead, which Floci OCI returns with {@code FLOCI_OCI_DOCKER_ENDPOINT_MODE=published}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PsqlConfig config = PsqlConfig.builder()
 *     .mock(true)
 *     .image("postgres:{major}")
 *     .portRange(25432, 5)
 *     .readyTimeoutSeconds(60)
 *     .build();
 * }</pre>
 */
public class PsqlConfig extends AbstractServiceConfig<PsqlConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "postgres:{major}-alpine";
    private static final int DEFAULT_BASE_PORT = 15432;
    private static final int DEFAULT_PORTS_COUNT = 10;
    private static final int DEFAULT_READY_TIMEOUT_SECONDS = 120;

    private final boolean mock;
    private final String image;
    private final int basePort;
    private final int portsCount;
    private final int readyTimeoutSeconds;

    private PsqlConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.image = builder.image;
        this.basePort = builder.basePort;
        this.portsCount = builder.portsCount;
        this.readyTimeoutSeconds = builder.readyTimeoutSeconds;
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
     * Returns whether no Docker container is started: DB systems are management-plane only and become {@code ACTIVE}
     * at once.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the PostgreSQL image of the DB system containers; {@code {major}} is replaced with the major version of
     * the DB system's {@code dbVersion}.
     *
     * @return the Docker image
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the first port of the host port range the DB systems' PostgreSQL endpoints are published on.
     *
     * @return the base port
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * Returns the number of ports of the host port range the DB systems' PostgreSQL endpoints are published on,
     * starting from {@link #getBasePort()}.
     *
     * @return the number of ports
     */
    public int getPortsCount() {
        return portsCount;
    }

    /**
     * Returns the last port of the host port range the DB systems' PostgreSQL endpoints are published on.
     *
     * @return the maximum port
     */
    public int getMaxPort() {
        return basePort + portsCount - 1;
    }

    /**
     * Returns how long a DB system may take to accept connections before it and its work request are marked
     * {@code FAILED}.
     *
     * @return the timeout in seconds
     */
    public int getReadyTimeoutSeconds() {
        return readyTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_OCI_SERVICES_PSQL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_OCI_SERVICES_PSQL_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_OCI_SERVICES_PSQL_IMAGE", image);
            container.withEnv("FLOCI_OCI_SERVICES_PSQL_BASE_PORT", String.valueOf(basePort));
            container.withEnv("FLOCI_OCI_SERVICES_PSQL_MAX_PORT", String.valueOf(getMaxPort()));
            container.withEnv("FLOCI_OCI_SERVICES_PSQL_READY_TIMEOUT_SECONDS", String.valueOf(readyTimeoutSeconds));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link PsqlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PsqlConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String image = DEFAULT_IMAGE;
        private int basePort = DEFAULT_BASE_PORT;
        private int portsCount = DEFAULT_PORTS_COUNT;
        private int readyTimeoutSeconds = DEFAULT_READY_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via PsqlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PsqlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PsqlConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.image = instance.image;
            this.basePort = instance.basePort;
            this.portsCount = instance.portsCount;
            this.readyTimeoutSeconds = instance.readyTimeoutSeconds;
        }

        /**
         * Sets whether no Docker container is started: DB systems are management-plane only and become {@code ACTIVE}
         * at once. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the PostgreSQL image of the DB system containers; {@code {major}} is replaced with the major version of
         * the DB system's {@code dbVersion}.
         *
         * @param image the Docker image (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets the host port range the DB systems' PostgreSQL endpoints are published on, one port per DB system.
         * Floci itself allows 15432-15531; this module defaults to 10 ports, i.e. 10 concurrent DB systems.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_PORTS_COUNT})
         * @return this builder
         */
        public Builder portRange(int basePort, int amount) {
            this.basePort = basePort;
            this.portsCount = amount;
            return this;
        }

        /**
         * Sets how long a DB system may take to accept connections before it and its work request are marked
         * {@code FAILED}. Not applicable in {@code mock} mode.
         *
         * @param readyTimeoutSeconds the timeout in seconds (default {@value DEFAULT_READY_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder readyTimeoutSeconds(int readyTimeoutSeconds) {
            this.readyTimeoutSeconds = readyTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link PsqlConfig} from this builder.
         *
         * @return the Database with PostgreSQL configuration
         */
        @Override
        public PsqlConfig build() {
            return new PsqlConfig(this);
        }
    }
}
