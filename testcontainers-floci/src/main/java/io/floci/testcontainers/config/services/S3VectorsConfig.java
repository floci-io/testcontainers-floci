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
public class S3VectorsConfig extends AbstractServiceConfig {

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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_S3VECTORS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link S3VectorsConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via S3VectorsConfig.builder()
        }

        /**
         * Enables or disables the S3 Vectors service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Creates an immutable {@link S3VectorsConfig} from this builder.
         *
         * @return the S3 Vectors configuration
         */
        public S3VectorsConfig build() {
            return new S3VectorsConfig(this);
        }
    }
}
