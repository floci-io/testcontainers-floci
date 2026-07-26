package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Glue-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GlueConfig config = GlueConfig.builder()
 *     .build();
 * }</pre>
 */
public class GlueConfig extends AbstractServiceConfig<GlueConfig.Builder> {


    private GlueConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_GLUE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link GlueConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GlueConfig> {


        private Builder() {
            // Allow instantiation only via GlueConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GlueConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GlueConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link GlueConfig} from this builder.
         *
         * @return the Glue configuration
         */
        public GlueConfig build() {
            return new GlueConfig(this);
        }
    }
}
