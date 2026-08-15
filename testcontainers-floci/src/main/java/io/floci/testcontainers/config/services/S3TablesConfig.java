package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for S3 Tables-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * S3TablesConfig config = S3TablesConfig.builder()
 *     .build();
 * }</pre>
 */
public class S3TablesConfig extends AbstractServiceConfig<S3TablesConfig.Builder> {

    private S3TablesConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_S3TABLES_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link S3TablesConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, S3TablesConfig> {

        private Builder() {
            // Allow instantiation only via S3TablesConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link S3TablesConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(S3TablesConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link S3TablesConfig} from this builder.
         *
         * @return the S3 Tables configuration
         */
        public S3TablesConfig build() {
            return new S3TablesConfig(this);
        }
    }
}
