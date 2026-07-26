package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudFormation-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudFormationConfig config = CloudFormationConfig.builder()
 *     .deletedStackRetentionSeconds(60)
 *     .build();
 * }</pre>
 */
public class CloudFormationConfig extends AbstractServiceConfig<CloudFormationConfig.Builder> {

    private static final long DEFAULT_DELETED_STACK_RETENTION_SECONDS = 30L;

    private final long deletedStackRetentionSeconds;

    private CloudFormationConfig(Builder builder) {
        super(builder.enabled);
        this.deletedStackRetentionSeconds = builder.deletedStackRetentionSeconds;
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
     * Returns how long deleted stacks are retained in seconds.
     *
     * @return the deleted stack retention period in seconds
     */
    public long getDeletedStackRetentionSeconds() {
        return deletedStackRetentionSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_DELETED_STACK_RETENTION_SECONDS", String.valueOf(deletedStackRetentionSeconds));
        }
    }

    /**
     * Builder for {@link CloudFormationConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudFormationConfig> {

        private long deletedStackRetentionSeconds = DEFAULT_DELETED_STACK_RETENTION_SECONDS;

        private Builder() {
            // Allow instantiation only via CloudFormationConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudFormationConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudFormationConfig instance) {
            super(instance);
            this.deletedStackRetentionSeconds = instance.getDeletedStackRetentionSeconds();
        }

        /**
         * Sets how long deleted stacks are retained in seconds.
         *
         * @param deletedStackRetentionSeconds the retention period in seconds (default {@value DEFAULT_DELETED_STACK_RETENTION_SECONDS})
         * @return this builder
         */
        public Builder deletedStackRetentionSeconds(long deletedStackRetentionSeconds) {
            this.deletedStackRetentionSeconds = deletedStackRetentionSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link CloudFormationConfig} from this builder.
         *
         * @return the CloudFormation configuration
         */
        public CloudFormationConfig build() {
            return new CloudFormationConfig(this);
        }
    }
}
