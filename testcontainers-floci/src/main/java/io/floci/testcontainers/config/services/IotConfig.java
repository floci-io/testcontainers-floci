package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for IoT Core-specific container settings, including the embedded MQTT broker.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IotConfig config = IotConfig.builder()
 *     .mqttAutoStart(true)
 *     .mqttPort(1883)
 *     .build();
 * }</pre>
 */
public class IotConfig extends AbstractServiceConfig {

    private static final boolean DEFAULT_MQTT_ENABLED = true;
    private static final boolean DEFAULT_MQTT_AUTO_START = false;
    private static final String DEFAULT_MQTT_HOST = "0.0.0.0";
    private static final int DEFAULT_MQTT_PORT = 1883;

    private final boolean mqttEnabled;
    private final boolean mqttAutoStart;
    private final String mqttHost;
    private final int mqttPort;

    private IotConfig(Builder builder) {
        super(builder.enabled);
        this.mqttEnabled = builder.mqttEnabled;
        this.mqttAutoStart = builder.mqttAutoStart;
        this.mqttHost = builder.mqttHost;
        this.mqttPort = builder.mqttPort;
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
     * Returns whether the embedded MQTT broker is enabled.
     *
     * @return {@code true} if the MQTT broker is enabled
     */
    public boolean isMqttEnabled() {
        return mqttEnabled;
    }

    /**
     * Returns whether the embedded MQTT broker starts automatically on container startup rather
     * than lazily on the first MQTT connection.
     *
     * @return {@code true} if the MQTT broker auto-starts
     */
    public boolean isMqttAutoStart() {
        return mqttAutoStart;
    }

    /**
     * Returns the bind address for the embedded MQTT broker.
     *
     * @return the MQTT broker host
     */
    public String getMqttHost() {
        return mqttHost;
    }

    /**
     * Returns the port used by the embedded MQTT broker.
     *
     * @return the MQTT broker port
     */
    public int getMqttPort() {
        return mqttPort;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_IOT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_IOT_MQTT_ENABLED", String.valueOf(mqttEnabled));
            container.withEnv("FLOCI_SERVICES_IOT_MQTT_AUTO_START", String.valueOf(mqttAutoStart));
            container.withEnv("FLOCI_SERVICES_IOT_MQTT_HOST", mqttHost);
            container.withEnv("FLOCI_SERVICES_IOT_MQTT_PORT", String.valueOf(mqttPort));
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled() && mqttEnabled) {
            container.addExposedPorts(mqttPort);
        }
    }

    /**
     * Builder for {@link IotConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private boolean mqttEnabled = DEFAULT_MQTT_ENABLED;
        private boolean mqttAutoStart = DEFAULT_MQTT_AUTO_START;
        private String mqttHost = DEFAULT_MQTT_HOST;
        private int mqttPort = DEFAULT_MQTT_PORT;

        private Builder() {
            // Allow instantiation only via IotConfig.builder()
        }

        /**
         * Enables or disables the IoT Core service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Enables or disables the embedded MQTT broker.
         *
         * @param mqttEnabled {@code true} to enable (default {@value DEFAULT_MQTT_ENABLED})
         * @return this builder
         */
        public Builder mqttEnabled(boolean mqttEnabled) {
            this.mqttEnabled = mqttEnabled;
            return this;
        }

        /**
         * Sets whether the embedded MQTT broker starts automatically on container startup rather
         * than lazily on the first MQTT connection.
         *
         * @param mqttAutoStart {@code true} to auto-start (default {@value DEFAULT_MQTT_AUTO_START})
         * @return this builder
         */
        public Builder mqttAutoStart(boolean mqttAutoStart) {
            this.mqttAutoStart = mqttAutoStart;
            return this;
        }

        /**
         * Sets the bind address for the embedded MQTT broker.
         *
         * @param mqttHost the host (default {@value DEFAULT_MQTT_HOST})
         * @return this builder
         */
        public Builder mqttHost(String mqttHost) {
            this.mqttHost = mqttHost;
            return this;
        }

        /**
         * Sets the port used by the embedded MQTT broker.
         *
         * @param mqttPort the port (default {@value DEFAULT_MQTT_PORT})
         * @return this builder
         */
        public Builder mqttPort(int mqttPort) {
            this.mqttPort = mqttPort;
            return this;
        }

        /**
         * Creates an immutable {@link IotConfig} from this builder.
         *
         * @return the IoT Core configuration
         */
        public IotConfig build() {
            return new IotConfig(this);
        }
    }
}
