package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudMap-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudMapConfig config = CloudMapConfig.builder()
 *     .operationCompletionDelaySeconds(5)
 *     .build();
 * }</pre>
 */
public class CloudMapConfig extends AbstractServiceConfig<CloudMapConfig.Builder> {

    private static final int DEFAULT_OPERATION_COMPLETION_DELAY_SECONDS = 0;

    private final int operationCompletionDelaySeconds;

    private CloudMapConfig(Builder builder) {
        super(builder.enabled);
        this.operationCompletionDelaySeconds = builder.operationCompletionDelaySeconds;
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
     * Returns the number of seconds before an async operation (e.g. CreateNamespace, RegisterInstance)
     * transitions from PENDING to SUCCESS. 0 means the operation completes immediately.
     *
     * @return the operation completion delay in seconds
     */
    public int getOperationCompletionDelaySeconds() {
        return operationCompletionDelaySeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDMAP_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDMAP_OPERATION_COMPLETION_DELAY_SECONDS", String.valueOf(operationCompletionDelaySeconds));
        }
    }

    /**
     * Builder for {@link CloudMapConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudMapConfig> {

        private int operationCompletionDelaySeconds = DEFAULT_OPERATION_COMPLETION_DELAY_SECONDS;

        private Builder() {
            // Allow instantiation only via CloudMapConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudMapConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudMapConfig instance) {
            super(instance);
            this.operationCompletionDelaySeconds = instance.getOperationCompletionDelaySeconds();
        }

        /**
         * Sets the delay in seconds before an async operation (e.g. CreateNamespace, RegisterInstance)
         * transitions from PENDING to SUCCESS. Use 0 to complete immediately.
         *
         * @param operationCompletionDelaySeconds the delay in seconds (default {@value DEFAULT_OPERATION_COMPLETION_DELAY_SECONDS})
         * @return this builder
         */
        public Builder operationCompletionDelaySeconds(int operationCompletionDelaySeconds) {
            this.operationCompletionDelaySeconds = operationCompletionDelaySeconds;
            return this;
        }

        /**
         * Creates an immutable {@link CloudMapConfig} from this builder.
         *
         * @return the CloudMap configuration
         */
        @Override
        public CloudMapConfig build() {
            return new CloudMapConfig(this);
        }
    }
}
