package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Backup-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BackupConfig config = BackupConfig.builder()
 *     .jobCompletionDelaySeconds(5)
 *     .build();
 * }</pre>
 */
public class BackupConfig extends AbstractServiceConfig<BackupConfig.Builder> {

    private static final int DEFAULT_JOB_COMPLETION_DELAY_SECONDS = 3;

    private final int jobCompletionDelaySeconds;

    private BackupConfig(Builder builder) {
        super(builder.enabled);
        this.jobCompletionDelaySeconds = builder.jobCompletionDelaySeconds;
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
     * Returns the number of seconds to wait before marking a backup job as completed.
     *
     * @return the job completion delay in seconds
     */
    public int getJobCompletionDelaySeconds() {
        return jobCompletionDelaySeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BACKUP_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_BACKUP_JOB_COMPLETION_DELAY_SECONDS", String.valueOf(jobCompletionDelaySeconds));
        }
    }

    /**
     * Builder for {@link BackupConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BackupConfig> {

        private int jobCompletionDelaySeconds = DEFAULT_JOB_COMPLETION_DELAY_SECONDS;

        private Builder() {
            // Allow instantiation only via BackupConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BackupConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BackupConfig instance) {
            super(instance);
            this.jobCompletionDelaySeconds = instance.getJobCompletionDelaySeconds();
        }

        /**
         * Sets the number of seconds to wait before marking a backup job as completed.
         *
         * @param jobCompletionDelaySeconds the number of seconds to wait (default {@value DEFAULT_JOB_COMPLETION_DELAY_SECONDS})
         * @return this builder
         */
        public Builder jobCompletionDelaySeconds(int jobCompletionDelaySeconds) {
            this.jobCompletionDelaySeconds = jobCompletionDelaySeconds;
            return this;
        }

        /**
         * Creates an immutable {@link BackupConfig} from this builder.
         *
         * @return the Backup configuration
         */
        @Override
        public BackupConfig build() {
            return new BackupConfig(this);
        }
    }
}
