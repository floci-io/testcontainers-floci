package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for MSK (Managed Streaming for Apache Kafka)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MskConfig config = MskConfig.builder()
 *     .enabled(true)
 *     .mock(false)
 *     .defaultImage("redpandadata/redpanda:latest")
 *     .kafkaHostPortRange(9300, 10)
 *     .bootstrapHostname("localhost")
 *     .build();
 * }</pre>
 */
public class MskConfig extends AbstractServiceConfig<MskConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "redpandadata/redpanda:latest";
    private static final int DEFAULT_KAFKA_HOST_PORT_BASE = 9300;
    private static final int DEFAULT_KAFKA_HOST_PORTS_COUNT = 10;

    private final boolean mock;
    private final String defaultImage;
    private final int kafkaHostPortBase;
    private final int kafkaHostPortsCount;
    private final String bootstrapHostname;

    private MskConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.kafkaHostPortBase = builder.kafkaHostPortBase;
        this.kafkaHostPortsCount = builder.kafkaHostPortsCount;
        this.bootstrapHostname = builder.bootstrapHostname;
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
     * Returns whether MSK clusters are simulated in-memory without real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the default Docker image used for MSK (Redpanda) instances.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the base port for the MSK Kafka broker host port range.
     *
     * @return the base port
     */
    public int getKafkaHostPortBase() {
        return kafkaHostPortBase;
    }

    /**
     * Returns the number of ports allocated for MSK Kafka brokers, starting from {@link #getKafkaHostPortBase()}.
     *
     * @return the number of Kafka host ports
     */
    public int getKafkaHostPortsCount() {
        return kafkaHostPortsCount;
    }

    /**
     * Returns the maximum port for the MSK Kafka broker host port range.
     *
     * @return the maximum port
     */
    public int getKafkaHostPortMax() {
        return kafkaHostPortBase + kafkaHostPortsCount - 1;
    }

    /**
     * Returns the hostname the brokers' host listener advertises when Floci runs in a container.
     * Empty if Floci's default ({@code localhost}, Docker-network address in GetBootstrapBrokers) is used.
     *
     * @return the bootstrap hostname, if set
     */
    public Optional<String> getBootstrapHostname() {
        return Optional.ofNullable(bootstrapHostname);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_MSK_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_MSK_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_MSK_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_BASE", String.valueOf(kafkaHostPortBase));
            container.withEnv("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_MAX", String.valueOf(getKafkaHostPortMax()));

            if (bootstrapHostname != null) {
                container.withEnv("FLOCI_SERVICES_MSK_BOOTSTRAP_HOSTNAME", bootstrapHostname);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            // Expose ports of MSK Kafka brokers to make them accessible by the user
            for (int port = kafkaHostPortBase; port <= getKafkaHostPortMax(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link MskConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MskConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_IMAGE;
        private int kafkaHostPortBase = DEFAULT_KAFKA_HOST_PORT_BASE;
        private int kafkaHostPortsCount = DEFAULT_KAFKA_HOST_PORTS_COUNT;
        private String bootstrapHostname;

        private Builder() {
            // Allow instantiation only via MskConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MskConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MskConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultImage = instance.getDefaultImage();
            this.kafkaHostPortBase = instance.getKafkaHostPortBase();
            this.kafkaHostPortsCount = instance.getKafkaHostPortsCount();
            this.bootstrapHostname = instance.getBootstrapHostname().orElse(null);
        }

        /**
         * Sets whether MSK clusters are simulated in-memory without real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the default Docker image for MSK (Redpanda) instances.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range for MSK Kafka brokers.
         *
         * @param basePort the base port (default {@value DEFAULT_KAFKA_HOST_PORT_BASE})
         * @param amount   the amount of ports (default {@value DEFAULT_KAFKA_HOST_PORTS_COUNT})
         * @return this builder
         */
        public Builder kafkaHostPortRange(int basePort, int amount) {
            this.kafkaHostPortBase = basePort;
            this.kafkaHostPortsCount = amount;
            return this;
        }

        /**
         * Sets the hostname the brokers' host listener advertises. Only applies when Floci runs in a
         * container (always the case here): each broker then has a second Kafka listener, published on
         * the Docker host on a port from the Kafka host port range and advertised as
         * {@code <bootstrapHostname>:<port>} ({@code localhost} when unset). When set,
         * GetBootstrapBrokers returns both listeners, {@code <container-name>:9092,<bootstrapHostname>:<port>},
         * so clients on the Docker host and in sibling containers can each discover the broker; when
         * unset, it returns only the broker's Docker-network address.
         *
         * @param bootstrapHostname the hostname to advertise, or {@code null} to use Floci's default (default unset)
         * @return this builder
         */
        public Builder bootstrapHostname(String bootstrapHostname) {
            this.bootstrapHostname = bootstrapHostname;
            return this;
        }

        /**
         * Creates an immutable {@link MskConfig} from this builder.
         *
         * @return the MSK configuration
         */
        @Override
        public MskConfig build() {
            return new MskConfig(this);
        }
    }
}
