package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Managed Service for Apache Kafka of Floci GCP.
 *
 * <p>Each cluster is backed by a Redpanda container, and Kafka Connect clusters by Kafka Connect worker containers.
 * Floci GCP publishes their ports on the Docker host and reports the reachable bootstrap address of a cluster; in
 * {@code mock} mode only the control plane is emulated.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KafkaConfig config = KafkaConfig.builder()
 *     .mock(true)
 *     .defaultImage("redpandadata/redpanda:v25.1.1")
 *     .build();
 * }</pre>
 */
public class KafkaConfig extends AbstractServiceConfig<KafkaConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "redpandadata/redpanda:latest";
    private static final String DEFAULT_CONNECT_IMAGE = "apache/kafka:4.3.1";

    private final boolean mock;
    private final String defaultImage;
    private final String connectImage;
    private final String dockerNetwork;

    private KafkaConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.connectImage = builder.connectImage;
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
     * Returns whether no broker or Kafka Connect containers are started; clusters are only emulated as control
     * plane resources.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker image of the Redpanda broker container backing each cluster.
     *
     * @return the broker Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the Docker image of the Kafka Connect worker containers (an Apache Kafka distribution with the
     * layout under {@code /opt/kafka}).
     *
     * @return the Kafka Connect Docker image
     */
    public String getConnectImage() {
        return connectImage;
    }

    /**
     * Returns the Docker network the broker and Kafka Connect containers join, or empty if not set.
     *
     * @return the Docker network, or empty
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_KAFKA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_KAFKA_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_KAFKA_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_GCP_SERVICES_KAFKA_CONNECT_IMAGE", connectImage);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_GCP_SERVICES_KAFKA_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link KafkaConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KafkaConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private String connectImage = DEFAULT_CONNECT_IMAGE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via KafkaConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KafkaConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KafkaConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.defaultImage = instance.defaultImage;
            this.connectImage = instance.connectImage;
            this.dockerNetwork = instance.dockerNetwork;
        }

        /**
         * Sets whether no broker or Kafka Connect containers are started; clusters are only emulated as control
         * plane resources. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Docker image of the Redpanda broker container backing each cluster.
         *
         * @param defaultImage the broker Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the Docker image of the Kafka Connect worker containers. It must be an Apache Kafka distribution
         * with the layout under {@code /opt/kafka}.
         *
         * @param connectImage the Kafka Connect Docker image (default {@value DEFAULT_CONNECT_IMAGE})
         * @return this builder
         */
        public Builder connectImage(String connectImage) {
            this.connectImage = connectImage;
            return this;
        }

        /**
         * Sets the Docker network the broker and Kafka Connect containers join. When unset, Floci GCP falls back
         * to the shared services network (see {@code FlociGcpContainer#withDedicatedNetwork()}) and then to the
         * network of the Floci GCP container itself.
         *
         * @param dockerNetwork the Docker network, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link KafkaConfig} from this builder.
         *
         * @return the Managed Service for Apache Kafka configuration
         */
        @Override
        public KafkaConfig build() {
            return new KafkaConfig(this);
        }
    }
}
