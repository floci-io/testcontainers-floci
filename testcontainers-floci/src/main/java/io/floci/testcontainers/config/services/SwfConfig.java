package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for SWF (Simple Workflow Service)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SwfConfig config = SwfConfig.builder()
 *     .timeoutSweepEnabled(true)
 *     .timeoutSweepIntervalSeconds(1)
 *     .build();
 * }</pre>
 */
public class SwfConfig extends AbstractServiceConfig<SwfConfig.Builder> {

    private static final boolean DEFAULT_TIMEOUT_SWEEP_ENABLED = true;
    private static final long DEFAULT_TIMEOUT_SWEEP_INTERVAL_SECONDS = 1;

    private final boolean timeoutSweepEnabled;
    private final long timeoutSweepIntervalSeconds;

    private SwfConfig(Builder builder) {
        super(builder.enabled);
        this.timeoutSweepEnabled = builder.timeoutSweepEnabled;
        this.timeoutSweepIntervalSeconds = builder.timeoutSweepIntervalSeconds;
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether the background sweep that expires activity, decision, workflow and timer
     * timeouts runs. When disabled, timeouts are recorded but never fired, which is useful for
     * tests that drive the clock themselves.
     *
     * @return {@code true} if the timeout sweep is enabled
     */
    public boolean isTimeoutSweepEnabled() {
        return timeoutSweepEnabled;
    }

    /**
     * Returns how often the timeout sweep runs, in seconds. SWF timeouts are specified in whole
     * seconds, so a 1s sweep bounds the observable lateness of an expiry at one second.
     *
     * @return the sweep interval in seconds
     */
    public long getTimeoutSweepIntervalSeconds() {
        return timeoutSweepIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SWF_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_ENABLED", String.valueOf(timeoutSweepEnabled));
            container.withEnv("FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_INTERVAL_SECONDS", String.valueOf(timeoutSweepIntervalSeconds));
        }
    }

    /**
     * Builder for {@link SwfConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SwfConfig> {

        private boolean timeoutSweepEnabled = DEFAULT_TIMEOUT_SWEEP_ENABLED;
        private long timeoutSweepIntervalSeconds = DEFAULT_TIMEOUT_SWEEP_INTERVAL_SECONDS;

        private Builder() {
            // Allow instantiation only via SwfConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SwfConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SwfConfig instance) {
            super(instance);
            this.timeoutSweepEnabled = instance.isTimeoutSweepEnabled();
            this.timeoutSweepIntervalSeconds = instance.getTimeoutSweepIntervalSeconds();
        }

        /**
         * Sets whether the background sweep that expires activity, decision, workflow and timer
         * timeouts runs.
         *
         * @param timeoutSweepEnabled {@code true} to run the timeout sweep (default {@value DEFAULT_TIMEOUT_SWEEP_ENABLED})
         * @return this builder
         */
        public Builder timeoutSweepEnabled(boolean timeoutSweepEnabled) {
            this.timeoutSweepEnabled = timeoutSweepEnabled;
            return this;
        }

        /**
         * Sets how often the timeout sweep runs, in seconds.
         *
         * @param timeoutSweepIntervalSeconds the sweep interval in seconds (default {@value DEFAULT_TIMEOUT_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder timeoutSweepIntervalSeconds(long timeoutSweepIntervalSeconds) {
            this.timeoutSweepIntervalSeconds = timeoutSweepIntervalSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link SwfConfig} from this builder.
         *
         * @return the SWF configuration
         */
        public SwfConfig build() {
            return new SwfConfig(this);
        }
    }
}
