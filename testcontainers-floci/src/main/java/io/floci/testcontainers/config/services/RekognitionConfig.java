package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Rekognition-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RekognitionConfig config = RekognitionConfig.builder()
 *     .build();
 * }</pre>
 */
public class RekognitionConfig extends AbstractServiceConfig<RekognitionConfig.Builder> {

    private RekognitionConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_REKOGNITION_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link RekognitionConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RekognitionConfig> {

        private Builder() {
            // Allow instantiation only via RekognitionConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RekognitionConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RekognitionConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link RekognitionConfig} from this builder.
         *
         * @return the Rekognition configuration
         */
        @Override
        public RekognitionConfig build() {
            return new RekognitionConfig(this);
        }
    }
}
