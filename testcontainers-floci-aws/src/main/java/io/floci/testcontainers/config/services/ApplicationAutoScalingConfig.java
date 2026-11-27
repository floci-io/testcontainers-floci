package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Application Auto Scaling-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ApplicationAutoScalingConfig config = ApplicationAutoScalingConfig.builder()
 *     .build();
 * }</pre>
 */
public class ApplicationAutoScalingConfig extends AbstractServiceConfig<ApplicationAutoScalingConfig.Builder> {

    private static final long DEFAULT_SCHEDULED_ACTION_INTERVAL_SECONDS = 1L;

    private final long scheduledActionIntervalSeconds;

    private ApplicationAutoScalingConfig(Builder builder) {
        super(builder);
        this.scheduledActionIntervalSeconds = builder.scheduledActionIntervalSeconds;
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
     * Returns how often, in seconds, Floci checks for and runs due scheduled actions.
     *
     * @return how often, in seconds, Floci checks for and runs due scheduled actions
     */
    public long getScheduledActionIntervalSeconds() {
        return scheduledActionIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_APPLICATIONAUTOSCALING_SCHEDULED_ACTION_INTERVAL_SECONDS", String.valueOf(scheduledActionIntervalSeconds));
        }
    }

    /**
     * Builder for {@link ApplicationAutoScalingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApplicationAutoScalingConfig> {

        private long scheduledActionIntervalSeconds = DEFAULT_SCHEDULED_ACTION_INTERVAL_SECONDS;

        private Builder() {
            // Allow instantiation only via ApplicationAutoScalingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ApplicationAutoScalingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ApplicationAutoScalingConfig instance) {
            super(instance);
            this.scheduledActionIntervalSeconds = instance.getScheduledActionIntervalSeconds();
        }

        /**
         * Sets how often, in seconds, Floci checks for and runs due scheduled actions.
         *
         * @param scheduledActionIntervalSeconds how often, in seconds, Floci checks for and runs due scheduled actions (default {@value DEFAULT_SCHEDULED_ACTION_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder scheduledActionIntervalSeconds(long scheduledActionIntervalSeconds) {
            this.scheduledActionIntervalSeconds = scheduledActionIntervalSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link ApplicationAutoScalingConfig} from this builder.
         *
         * @return the Application Auto Scaling configuration
         */
        @Override
        public ApplicationAutoScalingConfig build() {
            return new ApplicationAutoScalingConfig(this);
        }
    }
}
