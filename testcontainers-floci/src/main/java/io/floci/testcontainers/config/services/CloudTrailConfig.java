package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudTrail-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudTrailConfig config = CloudTrailConfig.builder()
 *     .enabled(true)
 *     .build();
 * }</pre>
 */
public class CloudTrailConfig extends AbstractServiceConfig<CloudTrailConfig.Builder> {

    private CloudTrailConfig(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudTrailConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudTrailConfig> {


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
        }

        /**
         * Creates an immutable {@link CloudTrailConfig} from this builder.
         *
         * @return the CloudTrail configuration
         */
        public CloudTrailConfig build() {
            return new CloudTrailConfig(this);
        }
    }
}
