package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Kinesis Analytics (Managed Service for Apache Flink)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KinesisAnalyticsConfig config = KinesisAnalyticsConfig.builder()
 *     .mock(true)
 *     .build();
 * }</pre>
 */
public class KinesisAnalyticsConfig extends AbstractServiceConfig<KinesisAnalyticsConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;

    private final boolean mock;
    private final String defaultImage;

    private KinesisAnalyticsConfig(Builder builder) {
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
     * Returns whether applications come up {@code RUNNING} immediately with no backing Flink
     * container when started. Useful for tests and hosts without a Docker daemon.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the fixed Docker image used for every application regardless of the requested
     * runtime environment, or {@link Optional#empty()} if the image should be chosen from the
     * application's {@code RuntimeEnvironment} instead.
     *
     * @return the default image, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDefaultImage() {
        return Optional.ofNullable(defaultImage);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_KINESIS_ANALYTICS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_KINESIS_ANALYTICS_MOCK", String.valueOf(mock));
            if (defaultImage != null) {
                container.withEnv("FLOCI_SERVICES_KINESIS_ANALYTICS_DEFAULT_IMAGE", defaultImage);
            }
        }
    }

    /**
     * Builder for {@link KinesisAnalyticsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KinesisAnalyticsConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = null;

        private Builder() {
            // Allow instantiation only via KinesisAnalyticsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KinesisAnalyticsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KinesisAnalyticsConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultImage = instance.getDefaultImage().orElse(null);
        }

        /**
         * Sets whether applications come up {@code RUNNING} immediately with no backing Flink
         * container when started.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the fixed Docker image used for every application regardless of the requested
         * runtime environment.
         *
         * @param defaultImage the image name, or {@code null} to choose the image from each
         *                      application's runtime environment
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Creates an immutable {@link KinesisAnalyticsConfig} from this builder.
         *
         * @return the Kinesis Analytics configuration
         */
        @Override
        public KinesisAnalyticsConfig build() {
            return new KinesisAnalyticsConfig(this);
        }
    }
}
