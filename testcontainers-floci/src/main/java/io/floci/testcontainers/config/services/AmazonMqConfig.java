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
 *     .build();
 * }</pre>
 */
public class AmazonMqConfig extends AbstractServiceConfig<AmazonMqConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "rabbitmq:3-management";

    private final boolean mock;
    private final String defaultImage;

    private AmazonMqConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_AMAZONMQ_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", defaultImage);
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
