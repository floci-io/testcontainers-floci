package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudWatch Metrics-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudWatchMetricsConfig config = CloudWatchMetricsConfig.builder()
 *     .build();
 * }</pre>
 */
public class CloudWatchMetricsConfig extends AbstractServiceConfig<CloudWatchMetricsConfig.Builder> {


    private CloudWatchMetricsConfig(Builder builder) {
        super(builder.enabled);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDWATCHMETRICS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudWatchMetricsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudWatchMetricsConfig> {


        private Builder() {
            // Allow instantiation only via CloudWatchMetricsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudWatchMetricsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudWatchMetricsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudWatchMetricsConfig} from this builder.
         *
         * @return the CloudWatch Metrics configuration
         */
        @Override
        public CloudWatchMetricsConfig build() {
            return new CloudWatchMetricsConfig(this);
        }
    }
}
