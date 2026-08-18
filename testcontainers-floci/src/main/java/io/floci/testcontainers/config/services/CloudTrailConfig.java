package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudTrail-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudTrailConfig config = CloudTrailConfig.builder()
 *     .enabled(true)
 *     .flushIntervalSeconds(60)
 *     .build();
 * }</pre>
 */
public class CloudTrailConfig extends AbstractServiceConfig<CloudTrailConfig.Builder> {

    private static final int DEFAULT_FLUSH_INTERVAL_SECONDS = 60;

    private final int flushIntervalSeconds;

    private CloudTrailConfig(Builder builder) {
        super(builder.enabled);
        this.flushIntervalSeconds = builder.flushIntervalSeconds;
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
     * Returns how often, in seconds, the writer flushes pending records into the destination
     * bucket. Real AWS delivers data events with ~5-minute lag; the default here is 60s so
     * dev/CI feedback loops stay fast.
     *
     * @return the flush interval in seconds
     */
    public int getFlushIntervalSeconds() {
        return flushIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDTRAIL_FLUSH_INTERVAL_SECONDS", String.valueOf(flushIntervalSeconds));
        }
    }

    /**
     * Builder for {@link CloudTrailConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudTrailConfig> {

        private int flushIntervalSeconds = DEFAULT_FLUSH_INTERVAL_SECONDS;

        private Builder() {
            // Allow instantiation only via CloudTrailConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudTrailConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudTrailConfig instance) {
            super(instance);
            this.flushIntervalSeconds = instance.getFlushIntervalSeconds();
        }

        /**
         * Sets how often, in seconds, the writer flushes pending records into the destination
         * bucket.
         *
         * @param flushIntervalSeconds the flush interval in seconds (default {@value DEFAULT_FLUSH_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder flushIntervalSeconds(int flushIntervalSeconds) {
            this.flushIntervalSeconds = flushIntervalSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link CloudTrailConfig} from this builder.
         *
         * @return the CloudTrail configuration
         */
        @Override
        public CloudTrailConfig build() {
            return new CloudTrailConfig(this);
        }
    }
}
