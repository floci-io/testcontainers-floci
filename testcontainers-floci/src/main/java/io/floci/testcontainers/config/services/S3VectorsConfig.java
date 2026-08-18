package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for S3 Vectors-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * S3VectorsConfig config = S3VectorsConfig.builder()
 *     .build();
 * }</pre>
 */
public class S3VectorsConfig extends AbstractServiceConfig<S3VectorsConfig.Builder> {

    private S3VectorsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_S3VECTORS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link S3VectorsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, S3VectorsConfig> {


        private Builder() {
            // Allow instantiation only via S3VectorsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link S3VectorsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(S3VectorsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link S3VectorsConfig} from this builder.
         *
         * @return the S3 Vectors configuration
         */
        @Override
        public S3VectorsConfig build() {
            return new S3VectorsConfig(this);
        }
    }
}
