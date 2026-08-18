package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Pipes-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PipesConfig config = PipesConfig.builder()
 *     .build();
 * }</pre>
 */
public class PipesConfig extends AbstractServiceConfig<PipesConfig.Builder> {


    private PipesConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_PIPES_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link PipesConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PipesConfig> {


        private Builder() {
            // Allow instantiation only via PipesConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PipesConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PipesConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link PipesConfig} from this builder.
         *
         * @return the Pipes configuration
         */
        @Override
        public PipesConfig build() {
            return new PipesConfig(this);
        }
    }
}
