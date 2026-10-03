package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Global Accelerator-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GlobalAcceleratorConfig config = GlobalAcceleratorConfig.builder()
 *     .build();
 * }</pre>
 */
public class GlobalAcceleratorConfig extends AbstractServiceConfig<GlobalAcceleratorConfig.Builder> {

    private GlobalAcceleratorConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_GLOBALACCELERATOR_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link GlobalAcceleratorConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GlobalAcceleratorConfig> {

        private Builder() {
            // Allow instantiation only via GlobalAcceleratorConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GlobalAcceleratorConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GlobalAcceleratorConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link GlobalAcceleratorConfig} from this builder.
         *
         * @return the Global Accelerator configuration
         */
        @Override
        public GlobalAcceleratorConfig build() {
            return new GlobalAcceleratorConfig(this);
        }
    }
}
