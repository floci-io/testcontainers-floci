package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudHSM v2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudHsmV2Config config = CloudHsmV2Config.builder()
 *     .build();
 * }</pre>
 */
public class CloudHsmV2Config extends AbstractServiceConfig<CloudHsmV2Config.Builder> {

    private CloudHsmV2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CLOUDHSMV2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudHsmV2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudHsmV2Config> {

        private Builder() {
            // Allow instantiation only via CloudHsmV2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudHsmV2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudHsmV2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudHsmV2Config} from this builder.
         *
         * @return the CloudHSM v2 configuration
         */
        @Override
        public CloudHsmV2Config build() {
            return new CloudHsmV2Config(this);
        }
    }
}
