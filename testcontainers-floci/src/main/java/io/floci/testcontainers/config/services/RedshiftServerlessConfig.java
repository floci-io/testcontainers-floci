package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Redshift Serverless-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
 *     .build();
 * }</pre>
 */
public class RedshiftServerlessConfig extends AbstractServiceConfig<RedshiftServerlessConfig.Builder> {

    private RedshiftServerlessConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link RedshiftServerlessConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedshiftServerlessConfig> {

        private Builder() {
            // Allow instantiation only via RedshiftServerlessConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedshiftServerlessConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedshiftServerlessConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link RedshiftServerlessConfig} from this builder.
         *
         * @return the Redshift Serverless configuration
         */
        @Override
        public RedshiftServerlessConfig build() {
            return new RedshiftServerlessConfig(this);
        }
    }
}
