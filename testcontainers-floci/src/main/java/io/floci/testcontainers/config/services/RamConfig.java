package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for RAM (Resource Access Manager)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RamConfig config = RamConfig.builder()
 *     .build();
 * }</pre>
 */
public class RamConfig extends AbstractServiceConfig<RamConfig.Builder> {

    private RamConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_RAM_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link RamConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RamConfig> {

        private Builder() {
            // Allow instantiation only via RamConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RamConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RamConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link RamConfig} from this builder.
         *
         * @return the RAM (Resource Access Manager) configuration
         */
        @Override
        public RamConfig build() {
            return new RamConfig(this);
        }
    }
}
