package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Secrets Manager-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SecretsManagerConfig config = SecretsManagerConfig.builder()
 *     .defaultRecoveryWindowDays(7)
 *     .build();
 * }</pre>
 */
public class SecretsManagerConfig extends AbstractServiceConfig<SecretsManagerConfig.Builder> {

    private static final int DEFAULT_RECOVERY_WINDOW_DAYS = 30;
    private static final boolean DEFAULT_SCHEDULED_ROTATION_ENABLED = true;
    private static final long DEFAULT_ROTATION_TICK_SECONDS = 60L;

    private final int defaultRecoveryWindowDays;
    private final boolean scheduledRotationEnabled;
    private final long rotationTickSeconds;

    private SecretsManagerConfig(Builder builder) {
        super(builder.enabled);
        this.defaultRecoveryWindowDays = builder.defaultRecoveryWindowDays;
        this.scheduledRotationEnabled = builder.scheduledRotationEnabled;
        this.rotationTickSeconds = builder.rotationTickSeconds;
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
     * Returns the default recovery window in days.
     *
     * @return the default recovery window in days
     */
    public int getDefaultRecoveryWindowDays() {
        return defaultRecoveryWindowDays;
    }

    /**
     * Returns whether secrets with a rotation schedule are rotated automatically when they become due.
     *
     * @return whether secrets with a rotation schedule are rotated automatically when they become due
     */
    public boolean isScheduledRotationEnabled() {
        return scheduledRotationEnabled;
    }

    /**
     * Returns the interval, in seconds, at which the rotation scheduler checks for secrets that are due for
     * rotation.
     *
     * @return the interval, in seconds, at which the rotation scheduler checks for secrets that are due for rotation
     */
    public long getRotationTickSeconds() {
        return rotationTickSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", String.valueOf(defaultRecoveryWindowDays));
            container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED", String.valueOf(scheduledRotationEnabled));
            container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS", String.valueOf(rotationTickSeconds));
        }
    }

    /**
     * Builder for {@link SecretsManagerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SecretsManagerConfig> {

        private int defaultRecoveryWindowDays = DEFAULT_RECOVERY_WINDOW_DAYS;
        private boolean scheduledRotationEnabled = DEFAULT_SCHEDULED_ROTATION_ENABLED;
        private long rotationTickSeconds = DEFAULT_ROTATION_TICK_SECONDS;

        private Builder() {
            // Allow instantiation only via SecretsManagerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SecretsManagerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SecretsManagerConfig instance) {
            super(instance);
            this.defaultRecoveryWindowDays = instance.getDefaultRecoveryWindowDays();
            this.scheduledRotationEnabled = instance.isScheduledRotationEnabled();
            this.rotationTickSeconds = instance.getRotationTickSeconds();
        }

        /**
         * Sets the default recovery window in days for deleted secrets.
         *
         * @param defaultRecoveryWindowDays the default recovery window in days for deleted secrets (default {@value DEFAULT_RECOVERY_WINDOW_DAYS})
         * @return this builder
         */
        public Builder defaultRecoveryWindowDays(int defaultRecoveryWindowDays) {
            this.defaultRecoveryWindowDays = defaultRecoveryWindowDays;
            return this;
        }

        /**
         * Sets whether secrets with a rotation schedule are rotated automatically when they become due.
         *
         * @param scheduledRotationEnabled whether secrets with a rotation schedule are rotated automatically when they become due (default {@value DEFAULT_SCHEDULED_ROTATION_ENABLED})
         * @return this builder
         */
        public Builder scheduledRotationEnabled(boolean scheduledRotationEnabled) {
            this.scheduledRotationEnabled = scheduledRotationEnabled;
            return this;
        }

        /**
         * Sets the interval, in seconds, at which the rotation scheduler checks for secrets that are due for
         * rotation.
         *
         * @param rotationTickSeconds the interval, in seconds, at which the rotation scheduler checks for secrets that are due for rotation (default {@value DEFAULT_ROTATION_TICK_SECONDS})
         * @return this builder
         */
        public Builder rotationTickSeconds(long rotationTickSeconds) {
            this.rotationTickSeconds = rotationTickSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link SecretsManagerConfig} from this builder.
         *
         * @return the Secrets Manager configuration
         */
        @Override
        public SecretsManagerConfig build() {
            return new SecretsManagerConfig(this);
        }
    }
}
