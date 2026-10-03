package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Timestream for InfluxDB-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder()
 *     .mock(false)
 *     .defaultImage("influxdb:2.7")
 *     .hostPortRange(8086, 10)
 *     .build();
 * }</pre>
 */
public class TimestreamInfluxDbConfig extends AbstractServiceConfig<TimestreamInfluxDbConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "influxdb:2.7";
    private static final int DEFAULT_HOST_PORT_BASE = 8086;
    private static final int DEFAULT_HOST_PORTS_COUNT = 10;
    private static final int DEFAULT_READINESS_TIMEOUT_SECONDS = 120;

    private final boolean mock;
    private final String defaultImage;
    private final int hostPortBase;
    private final int hostPortsCount;
    private final int readinessTimeoutSeconds;
    private final String dockerNetwork;

    private TimestreamInfluxDbConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.hostPortBase = builder.hostPortBase;
        this.hostPortsCount = builder.hostPortsCount;
        this.readinessTimeoutSeconds = builder.readinessTimeoutSeconds;
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
     * Returns whether DB instances and clusters reach {@code AVAILABLE} without a backing InfluxDB container.
     *
     * @return whether DB instances and clusters reach {@code AVAILABLE} without a backing InfluxDB container
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the InfluxDB 2.x Docker image backing DB instances.
     *
     * @return the InfluxDB 2.x Docker image backing DB instances
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the lowest Docker host port the InfluxDB HTTP listener (container port 8086) of a DB
     * instance is published on.
     *
     * @return the base host port
     */
    public int getHostPortBase() {
        return hostPortBase;
    }

    /**
     * Returns the number of host ports, starting from {@link #getHostPortBase()}.
     *
     * @return the number of host ports
     */
    public int getHostPortsCount() {
        return hostPortsCount;
    }

    /**
     * Returns the highest Docker host port the InfluxDB HTTP listener of a DB instance is
     * published on.
     *
     * @return the maximum host port
     */
    public int getHostPortMax() {
        return hostPortBase + hostPortsCount - 1;
    }

    /**
     * Returns the number of seconds to wait for a started InfluxDB container to answer its health check.
     *
     * @return the number of seconds to wait for a started InfluxDB container to answer its health check
     */
    public int getReadinessTimeoutSeconds() {
        return readinessTimeoutSeconds;
    }

    /**
     * Returns the Docker network InfluxDB containers are attached to.
     *
     * <p>Unset uses the default network.
     *
     * @return the Docker network InfluxDB containers are attached to, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE", String.valueOf(hostPortBase));
            container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX", String.valueOf(getHostPortMax()));
            container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS", String.valueOf(readinessTimeoutSeconds));

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link TimestreamInfluxDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TimestreamInfluxDbConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_IMAGE;
        private int hostPortBase = DEFAULT_HOST_PORT_BASE;
        private int hostPortsCount = DEFAULT_HOST_PORTS_COUNT;
        private int readinessTimeoutSeconds = DEFAULT_READINESS_TIMEOUT_SECONDS;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via TimestreamInfluxDbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TimestreamInfluxDbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TimestreamInfluxDbConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultImage = instance.getDefaultImage();
            this.hostPortBase = instance.getHostPortBase();
            this.hostPortsCount = instance.getHostPortsCount();
            this.readinessTimeoutSeconds = instance.getReadinessTimeoutSeconds();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
        }

        /**
         * Sets whether DB instances and clusters reach {@code AVAILABLE} without a backing InfluxDB
         * container.
         *
         * @param mock whether DB instances and clusters reach {@code AVAILABLE} without a backing InfluxDB container (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the InfluxDB 2.x Docker image backing DB instances.
         *
         * @param defaultImage the InfluxDB 2.x Docker image backing DB instances (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the Docker host port range the InfluxDB HTTP listener (container port 8086) of the DB
         * instances is published on, one port per instance.
         *
         * @param basePort the base port (default {@value DEFAULT_HOST_PORT_BASE})
         * @param amount   the amount of ports (default {@value DEFAULT_HOST_PORTS_COUNT})
         * @return this builder
         */
        public Builder hostPortRange(int basePort, int amount) {
            this.hostPortBase = basePort;
            this.hostPortsCount = amount;
            return this;
        }

        /**
         * Sets the number of seconds to wait for a started InfluxDB container to answer its health check.
         *
         * @param readinessTimeoutSeconds the number of seconds to wait for a started InfluxDB container to answer its health check (default {@value DEFAULT_READINESS_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder readinessTimeoutSeconds(int readinessTimeoutSeconds) {
            this.readinessTimeoutSeconds = readinessTimeoutSeconds;
            return this;
        }

        /**
         * Sets the Docker network InfluxDB containers are attached to.
         *
         * <p>Unset uses the default network.
         *
         * @param dockerNetwork the Docker network InfluxDB containers are attached to, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link TimestreamInfluxDbConfig} from this builder.
         *
         * @return the Timestream for InfluxDB configuration
         */
        @Override
        public TimestreamInfluxDbConfig build() {
            return new TimestreamInfluxDbConfig(this);
        }
    }
}
