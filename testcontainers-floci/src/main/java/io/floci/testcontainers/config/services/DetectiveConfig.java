package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Detective-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DetectiveConfig config = DetectiveConfig.builder()
 *     .build();
 * }</pre>
 */
public class DetectiveConfig extends AbstractServiceConfig<DetectiveConfig.Builder> {

    private DetectiveConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_DETECTIVE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DetectiveConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DetectiveConfig> {

        private Builder() {
            // Allow instantiation only via DetectiveConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DetectiveConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DetectiveConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DetectiveConfig} from this builder.
         *
         * @return the Detective configuration
         */
        @Override
        public DetectiveConfig build() {
            return new DetectiveConfig(this);
        }
    }
}
