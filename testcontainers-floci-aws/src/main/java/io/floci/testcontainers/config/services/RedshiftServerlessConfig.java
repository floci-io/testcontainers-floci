package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Redshift Serverless-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
 *     .build();
 * }</pre>
 */
public class RedshiftServerlessConfig extends AbstractServiceConfig<RedshiftServerlessConfig.Builder> {

    private static final boolean DEFAULT_SNAPSHOT_EXPIRATION_ENABLED = true;
    private static final int DEFAULT_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS = 60;
    private static final boolean DEFAULT_SNAPSHOT_COPY_ENABLED = true;
    private static final int DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS = 60;
    private static final boolean DEFAULT_SCHEDULED_ACTIONS_ENABLED = true;
    private static final int DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS = 60;

    private final boolean snapshotExpirationEnabled;
    private final int snapshotExpirationSweepIntervalSeconds;
    private final boolean snapshotCopyEnabled;
    private final int snapshotCopySweepIntervalSeconds;
    private final boolean scheduledActionsEnabled;
    private final int scheduledActionSweepIntervalSeconds;

    private RedshiftServerlessConfig(Builder builder) {
        super(builder);
        this.snapshotExpirationEnabled = builder.snapshotExpirationEnabled;
        this.snapshotExpirationSweepIntervalSeconds = builder.snapshotExpirationSweepIntervalSeconds;
        this.snapshotCopyEnabled = builder.snapshotCopyEnabled;
        this.snapshotCopySweepIntervalSeconds = builder.snapshotCopySweepIntervalSeconds;
        this.scheduledActionsEnabled = builder.scheduledActionsEnabled;
        this.scheduledActionSweepIntervalSeconds = builder.scheduledActionSweepIntervalSeconds;
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
     * Returns whether Floci deletes snapshots once their retention period has passed.
     *
     * @return whether Floci deletes snapshots once their retention period has passed
     */
    public boolean isSnapshotExpirationEnabled() {
        return snapshotExpirationEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for expired snapshots.
     *
     * @return how often, in seconds, Floci checks for expired snapshots
     */
    public int getSnapshotExpirationSweepIntervalSeconds() {
        return snapshotExpirationSweepIntervalSeconds;
    }

    /**
     * Returns whether Floci copies snapshots into the destination region configured by a snapshot copy configuration.
     *
     * @return whether Floci copies snapshots into the destination region configured by a snapshot copy configuration
     */
    public boolean isSnapshotCopyEnabled() {
        return snapshotCopyEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for snapshots to copy into their destination region.
     *
     * @return how often, in seconds, Floci checks for snapshots to copy into their destination region
     */
    public int getSnapshotCopySweepIntervalSeconds() {
        return snapshotCopySweepIntervalSeconds;
    }

    /**
     * Returns whether Floci runs scheduled actions on their cron and at schedules.
     *
     * @return whether Floci runs scheduled actions on their cron and at schedules
     */
    public boolean isScheduledActionsEnabled() {
        return scheduledActionsEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for due scheduled actions.
     *
     * @return how often, in seconds, Floci checks for due scheduled actions
     */
    public int getScheduledActionSweepIntervalSeconds() {
        return scheduledActionSweepIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_ENABLED", String.valueOf(snapshotExpirationEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS", String.valueOf(snapshotExpirationSweepIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_ENABLED", String.valueOf(snapshotCopyEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", String.valueOf(snapshotCopySweepIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTIONS_ENABLED", String.valueOf(scheduledActionsEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", String.valueOf(scheduledActionSweepIntervalSeconds));
        }
    }

    /**
     * Builder for {@link RedshiftServerlessConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedshiftServerlessConfig> {

        private boolean snapshotExpirationEnabled = DEFAULT_SNAPSHOT_EXPIRATION_ENABLED;
        private int snapshotExpirationSweepIntervalSeconds = DEFAULT_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS;
        private boolean snapshotCopyEnabled = DEFAULT_SNAPSHOT_COPY_ENABLED;
        private int snapshotCopySweepIntervalSeconds = DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS;
        private boolean scheduledActionsEnabled = DEFAULT_SCHEDULED_ACTIONS_ENABLED;
        private int scheduledActionSweepIntervalSeconds = DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS;

        private Builder() {
            // Allow instantiation only via RedshiftServerlessConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedshiftServerlessConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedshiftServerlessConfig instance) {
            super(instance);
            this.snapshotExpirationEnabled = instance.isSnapshotExpirationEnabled();
            this.snapshotExpirationSweepIntervalSeconds = instance.getSnapshotExpirationSweepIntervalSeconds();
            this.snapshotCopyEnabled = instance.isSnapshotCopyEnabled();
            this.snapshotCopySweepIntervalSeconds = instance.getSnapshotCopySweepIntervalSeconds();
            this.scheduledActionsEnabled = instance.isScheduledActionsEnabled();
            this.scheduledActionSweepIntervalSeconds = instance.getScheduledActionSweepIntervalSeconds();
        }

        /**
         * Sets whether Floci deletes snapshots once their retention period has passed.
         *
         * @param snapshotExpirationEnabled whether Floci deletes snapshots once their retention period has passed (default {@value DEFAULT_SNAPSHOT_EXPIRATION_ENABLED})
         * @return this builder
         */
        public Builder snapshotExpirationEnabled(boolean snapshotExpirationEnabled) {
            this.snapshotExpirationEnabled = snapshotExpirationEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for expired snapshots.
         *
         * @param snapshotExpirationSweepIntervalSeconds how often, in seconds, Floci checks for expired snapshots (default {@value DEFAULT_SNAPSHOT_EXPIRATION_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder snapshotExpirationSweepIntervalSeconds(int snapshotExpirationSweepIntervalSeconds) {
            this.snapshotExpirationSweepIntervalSeconds = snapshotExpirationSweepIntervalSeconds;
            return this;
        }

        /**
         * Sets whether Floci copies snapshots into the destination region configured by a snapshot copy configuration.
         *
         * @param snapshotCopyEnabled whether Floci copies snapshots into the destination region configured by a snapshot copy configuration (default {@value DEFAULT_SNAPSHOT_COPY_ENABLED})
         * @return this builder
         */
        public Builder snapshotCopyEnabled(boolean snapshotCopyEnabled) {
            this.snapshotCopyEnabled = snapshotCopyEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for snapshots to copy into their destination region.
         *
         * @param snapshotCopySweepIntervalSeconds how often, in seconds, Floci checks for snapshots to copy into their destination region (default {@value DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder snapshotCopySweepIntervalSeconds(int snapshotCopySweepIntervalSeconds) {
            this.snapshotCopySweepIntervalSeconds = snapshotCopySweepIntervalSeconds;
            return this;
        }

        /**
         * Sets whether Floci runs scheduled actions on their cron and at schedules.
         *
         * @param scheduledActionsEnabled whether Floci runs scheduled actions on their cron and at schedules (default {@value DEFAULT_SCHEDULED_ACTIONS_ENABLED})
         * @return this builder
         */
        public Builder scheduledActionsEnabled(boolean scheduledActionsEnabled) {
            this.scheduledActionsEnabled = scheduledActionsEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for due scheduled actions.
         *
         * @param scheduledActionSweepIntervalSeconds how often, in seconds, Floci checks for due scheduled actions (default {@value DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder scheduledActionSweepIntervalSeconds(int scheduledActionSweepIntervalSeconds) {
            this.scheduledActionSweepIntervalSeconds = scheduledActionSweepIntervalSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link RedshiftServerlessConfig} from this builder.
         *
         * @return the Redshift Serverless configuration
         */
        @Override
        public RedshiftServerlessConfig build() {
            return new RedshiftServerlessConfig(this);
        }
    }
}
