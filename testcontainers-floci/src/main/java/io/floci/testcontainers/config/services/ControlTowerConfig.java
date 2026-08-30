package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Control Tower-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ControlTowerConfig config = ControlTowerConfig.builder()
 *     .build();
 * }</pre>
 */
public class ControlTowerConfig extends AbstractServiceConfig<ControlTowerConfig.Builder> {

    private ControlTowerConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CONTROLTOWER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ControlTowerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ControlTowerConfig> {

        private Builder() {
            // Allow instantiation only via ControlTowerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ControlTowerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ControlTowerConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ControlTowerConfig} from this builder.
         *
         * @return the Control Tower configuration
         */
        @Override
        public ControlTowerConfig build() {
            return new ControlTowerConfig(this);
        }
    }
}
