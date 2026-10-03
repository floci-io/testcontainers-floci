package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Pipes-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PipesConfig config = PipesConfig.builder()
 *     .build();
 * }</pre>
 */
public class PipesConfig extends AbstractServiceConfig<PipesConfig.Builder> {

    private static final String DEFAULT_KAFKA_REST_BRIDGE_DEFAULT_IMAGE = "ghcr.io/aiven-open/karapace:latest";
    private static final int DEFAULT_KAFKA_REST_BRIDGE_HOST_PORT_BASE = 9500;
    private static final int DEFAULT_KAFKA_REST_BRIDGE_HOST_PORTS_COUNT = 100;

    private final String kafkaRestBridgeDefaultImage;
    private final int kafkaRestBridgeHostPortBase;
    private final int kafkaRestBridgeHostPortsCount;

    private PipesConfig(Builder builder) {
        super(builder.enabled);
        this.kafkaRestBridgeDefaultImage = builder.kafkaRestBridgeDefaultImage;
        this.kafkaRestBridgeHostPortBase = builder.kafkaRestBridgeHostPortBase;
        this.kafkaRestBridgeHostPortsCount = builder.kafkaRestBridgeHostPortsCount;
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
     * Returns the image of the Karapace sidecar Pipes launches, on demand, to poll a Kafka source over REST.
     *
     * <p>One container is started per distinct target {@code bootstrap.servers} (a self-managed cluster, or
     * an MSK cluster's Redpanda backing), and reused across pipes that share the same source.
     *
     * @return the image of the Karapace sidecar Pipes launches, on demand, to poll a Kafka source over REST
     */
    public String getKafkaRestBridgeDefaultImage() {
        return kafkaRestBridgeDefaultImage;
    }

    /**
     * Returns the base port of the host port range the Karapace Kafka REST bridge sidecars are
     * published on.
     *
     * @return the base port
     */
    public int getKafkaRestBridgeHostPortBase() {
        return kafkaRestBridgeHostPortBase;
    }

    /**
     * Returns the number of Kafka REST bridge host ports, starting from
     * {@link #getKafkaRestBridgeHostPortBase()}.
     *
     * @return the number of Kafka REST bridge host ports
     */
    public int getKafkaRestBridgeHostPortsCount() {
        return kafkaRestBridgeHostPortsCount;
    }

    /**
     * Returns the maximum port of the Kafka REST bridge host port range.
     *
     * @return the maximum port
     */
    public int getKafkaRestBridgeHostPortMax() {
        return kafkaRestBridgeHostPortBase + kafkaRestBridgeHostPortsCount - 1;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_PIPES_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE", kafkaRestBridgeDefaultImage);
            container.withEnv("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE", String.valueOf(kafkaRestBridgeHostPortBase));
            container.withEnv("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX", String.valueOf(getKafkaRestBridgeHostPortMax()));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        // Kafka sources are polled through Karapace sidecar containers
        return isEnabled();
    }

    /**
     * Builder for {@link PipesConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PipesConfig> {

        private String kafkaRestBridgeDefaultImage = DEFAULT_KAFKA_REST_BRIDGE_DEFAULT_IMAGE;
        private int kafkaRestBridgeHostPortBase = DEFAULT_KAFKA_REST_BRIDGE_HOST_PORT_BASE;
        private int kafkaRestBridgeHostPortsCount = DEFAULT_KAFKA_REST_BRIDGE_HOST_PORTS_COUNT;

        private Builder() {
            // Allow instantiation only via PipesConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PipesConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PipesConfig instance) {
            super(instance);
            this.kafkaRestBridgeDefaultImage = instance.getKafkaRestBridgeDefaultImage();
            this.kafkaRestBridgeHostPortBase = instance.getKafkaRestBridgeHostPortBase();
            this.kafkaRestBridgeHostPortsCount = instance.getKafkaRestBridgeHostPortsCount();
        }

        /**
         * Sets the image of the Karapace sidecar Pipes launches, on demand, to poll a Kafka source over REST.
         *
         * <p>One container is started per distinct target {@code bootstrap.servers} (a self-managed cluster,
         * or an MSK cluster's Redpanda backing), and reused across pipes that share the same source.
         *
         * @param kafkaRestBridgeDefaultImage the image of the Karapace sidecar Pipes launches, on demand, to poll a Kafka source over REST (default {@value DEFAULT_KAFKA_REST_BRIDGE_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder kafkaRestBridgeDefaultImage(String kafkaRestBridgeDefaultImage) {
            this.kafkaRestBridgeDefaultImage = kafkaRestBridgeDefaultImage;
            return this;
        }

        /**
         * Sets the host port range the Karapace Kafka REST bridge sidecars are published on.
         *
         * @param basePort the base port (default {@value DEFAULT_KAFKA_REST_BRIDGE_HOST_PORT_BASE})
         * @param amount   the amount of ports (default {@value DEFAULT_KAFKA_REST_BRIDGE_HOST_PORTS_COUNT})
         * @return this builder
         */
        public Builder kafkaRestBridgeHostPortRange(int basePort, int amount) {
            this.kafkaRestBridgeHostPortBase = basePort;
            this.kafkaRestBridgeHostPortsCount = amount;
            return this;
        }

        /**
         * Creates an immutable {@link PipesConfig} from this builder.
         *
         * @return the Pipes configuration
         */
        @Override
        public PipesConfig build() {
            return new PipesConfig(this);
        }
    }
}
