package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Amazon MQ-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AmazonMqConfig config = AmazonMqConfig.builder()
 *     .enabled(true)
 *     .mock(false)
 *     .defaultImage("rabbitmq:3-management")
 *     .amqpHostPortRange(5672, 28)
 *     .consoleHostPortRange(15672, 28)
 *     .build();
 * }</pre>
 */
public class AmazonMqConfig extends AbstractServiceConfig<AmazonMqConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "rabbitmq:3-management";
    private static final int DEFAULT_AMQP_HOST_PORT_BASE = 5672;
    private static final int DEFAULT_AMQP_HOST_PORTS_COUNT = 28;
    private static final int DEFAULT_CONSOLE_HOST_PORT_BASE = 15672;
    private static final int DEFAULT_CONSOLE_HOST_PORTS_COUNT = 28;

    private final boolean mock;
    private final String defaultImage;
    private final int amqpHostPortBase;
    private final int amqpHostPortsCount;
    private final int consoleHostPortBase;
    private final int consoleHostPortsCount;

    private AmazonMqConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.amqpHostPortBase = builder.amqpHostPortBase;
        this.amqpHostPortsCount = builder.amqpHostPortsCount;
        this.consoleHostPortBase = builder.consoleHostPortBase;
        this.consoleHostPortsCount = builder.consoleHostPortsCount;
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
     * Returns whether Amazon MQ brokers are simulated in-memory without real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the default Docker image used for Amazon MQ (RabbitMQ) instances.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the base port of the Docker host port range the brokers' AMQP listener (container
     * port 5672) is published on, one port per broker.
     *
     * <p>The broker containers publish these ports on the Docker host directly (no Floci-internal
     * proxy fronts them), so this is how a client outside the Docker network reaches a broker.
     *
     * @return the base AMQP host port
     */
    public int getAmqpHostPortBase() {
        return amqpHostPortBase;
    }

    /**
     * Returns the number of AMQP host ports, starting from {@link #getAmqpHostPortBase()}.
     *
     * @return the number of AMQP host ports
     */
    public int getAmqpHostPortsCount() {
        return amqpHostPortsCount;
    }

    /**
     * Returns the maximum port of the AMQP host port range.
     *
     * @return the maximum AMQP host port
     */
    public int getAmqpHostPortMax() {
        return amqpHostPortBase + amqpHostPortsCount - 1;
    }

    /**
     * Returns the base port of the Docker host port range the brokers' RabbitMQ management console
     * (container port 15672) is published on, one port per broker.
     *
     * @return the base console host port
     */
    public int getConsoleHostPortBase() {
        return consoleHostPortBase;
    }

    /**
     * Returns the number of console host ports, starting from {@link #getConsoleHostPortBase()}.
     *
     * @return the number of console host ports
     */
    public int getConsoleHostPortsCount() {
        return consoleHostPortsCount;
    }

    /**
     * Returns the maximum port of the console host port range.
     *
     * @return the maximum console host port
     */
    public int getConsoleHostPortMax() {
        return consoleHostPortBase + consoleHostPortsCount - 1;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_AMAZONMQ_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", String.valueOf(amqpHostPortBase));
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX", String.valueOf(getAmqpHostPortMax()));
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE", String.valueOf(consoleHostPortBase));
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX", String.valueOf(getConsoleHostPortMax()));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link AmazonMqConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AmazonMqConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_IMAGE;
        private int amqpHostPortBase = DEFAULT_AMQP_HOST_PORT_BASE;
        private int amqpHostPortsCount = DEFAULT_AMQP_HOST_PORTS_COUNT;
        private int consoleHostPortBase = DEFAULT_CONSOLE_HOST_PORT_BASE;
        private int consoleHostPortsCount = DEFAULT_CONSOLE_HOST_PORTS_COUNT;

        private Builder() {
            // Allow instantiation only via AmazonMqConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AmazonMqConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AmazonMqConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultImage = instance.getDefaultImage();
            this.amqpHostPortBase = instance.getAmqpHostPortBase();
            this.amqpHostPortsCount = instance.getAmqpHostPortsCount();
            this.consoleHostPortBase = instance.getConsoleHostPortBase();
            this.consoleHostPortsCount = instance.getConsoleHostPortsCount();
        }

        /**
         * Sets whether Amazon MQ brokers are simulated in-memory without real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the default Docker image for Amazon MQ (RabbitMQ) instances.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the Docker host port range the brokers' AMQP listener (container port 5672) is
         * published on, one port per broker.
         *
         * @param basePort the base port (default {@value DEFAULT_AMQP_HOST_PORT_BASE})
         * @param amount   the amount of ports (default {@value DEFAULT_AMQP_HOST_PORTS_COUNT})
         * @return this builder
         */
        public Builder amqpHostPortRange(int basePort, int amount) {
            this.amqpHostPortBase = basePort;
            this.amqpHostPortsCount = amount;
            return this;
        }

        /**
         * Sets the Docker host port range the brokers' RabbitMQ management console (container port
         * 15672) is published on, one port per broker.
         *
         * @param basePort the base port (default {@value DEFAULT_CONSOLE_HOST_PORT_BASE})
         * @param amount   the amount of ports (default {@value DEFAULT_CONSOLE_HOST_PORTS_COUNT})
         * @return this builder
         */
        public Builder consoleHostPortRange(int basePort, int amount) {
            this.consoleHostPortBase = basePort;
            this.consoleHostPortsCount = amount;
            return this;
        }

        /**
         * Creates an immutable {@link AmazonMqConfig} from this builder.
         *
         * @return the Amazon MQ configuration
         */
        @Override
        public AmazonMqConfig build() {
            return new AmazonMqConfig(this);
        }
    }
}
