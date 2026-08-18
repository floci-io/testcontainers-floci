package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Firehose-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FirehoseConfig config = FirehoseConfig.builder()
 *     .tickIntervalSeconds(10)
 *     .flushRecordCount(1)
 *     .build();
 * }</pre>
 */
public class FirehoseConfig extends AbstractServiceConfig<FirehoseConfig.Builder> {

    private static final long DEFAULT_TICK_INTERVAL_SECONDS = 10;
    private static final int DEFAULT_FLUSH_RECORD_COUNT = 0;

    private final long tickIntervalSeconds;
    private final int flushRecordCount;

    private FirehoseConfig(Builder builder) {
        super(builder.enabled);
        this.tickIntervalSeconds = builder.tickIntervalSeconds;
        this.flushRecordCount = builder.flushRecordCount;
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
     * Returns how often, in seconds, the buffer flusher checks for streams whose buffering
     * interval ({@code BufferingHints.IntervalInSeconds}) has elapsed.
     *
     * @return the tick interval in seconds
     */
    public long getTickIntervalSeconds() {
        return tickIntervalSeconds;
    }

    /**
     * Returns the emulator-only volume trigger: the number of buffered records that forces an
     * immediate flush, complementing the stream's {@code BufferingHints}. Disabled by default (0)
     * so out-of-the-box delivery matches real AWS; set to 1 for LocalStack-style record-at-a-time
     * delivery in local dev.
     *
     * @return the flush record count, or 0 if disabled
     */
    public int getFlushRecordCount() {
        return flushRecordCount;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_FIREHOSE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_FIREHOSE_TICK_INTERVAL_SECONDS", String.valueOf(tickIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_FIREHOSE_FLUSH_RECORD_COUNT", String.valueOf(flushRecordCount));
        }
    }

    /**
     * Builder for {@link FirehoseConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FirehoseConfig> {

        private long tickIntervalSeconds = DEFAULT_TICK_INTERVAL_SECONDS;
        private int flushRecordCount = DEFAULT_FLUSH_RECORD_COUNT;

        private Builder() {
            // Allow instantiation only via FirehoseConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FirehoseConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FirehoseConfig instance) {
            super(instance);
            this.tickIntervalSeconds = instance.getTickIntervalSeconds();
            this.flushRecordCount = instance.getFlushRecordCount();
        }

        /**
         * Sets how often, in seconds, the buffer flusher checks for streams whose buffering
         * interval has elapsed.
         *
         * @param tickIntervalSeconds the tick interval in seconds (default {@value DEFAULT_TICK_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder tickIntervalSeconds(long tickIntervalSeconds) {
            this.tickIntervalSeconds = tickIntervalSeconds;
            return this;
        }

        /**
         * Sets the emulator-only volume trigger: the number of buffered records that forces an
         * immediate flush. Set to 1 for LocalStack-style record-at-a-time delivery in local dev.
         *
         * @param flushRecordCount the flush record count (default {@value DEFAULT_FLUSH_RECORD_COUNT}, disabled)
         * @return this builder
         */
        public Builder flushRecordCount(int flushRecordCount) {
            this.flushRecordCount = flushRecordCount;
            return this;
        }

        /**
         * Creates an immutable {@link FirehoseConfig} from this builder.
         *
         * @return the Firehose configuration
         */
        @Override
        public FirehoseConfig build() {
            return new FirehoseConfig(this);
        }
    }
}
