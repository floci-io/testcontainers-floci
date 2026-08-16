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

    private ApplicationAutoScalingConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ApplicationAutoScalingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApplicationAutoScalingConfig> {

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
