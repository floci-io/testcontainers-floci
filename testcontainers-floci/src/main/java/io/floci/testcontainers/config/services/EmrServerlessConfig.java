package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EMR Serverless-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EmrServerlessConfig config = EmrServerlessConfig.builder()
 *     .build();
 * }</pre>
 */
public class EmrServerlessConfig extends AbstractServiceConfig<EmrServerlessConfig.Builder> {

    private EmrServerlessConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link EmrServerlessConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EmrServerlessConfig> {

        private Builder() {
            // Allow instantiation only via EmrServerlessConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EmrServerlessConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EmrServerlessConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link EmrServerlessConfig} from this builder.
         *
         * @return the EMR Serverless configuration
         */
        @Override
        public EmrServerlessConfig build() {
            return new EmrServerlessConfig(this);
        }
    }
}
