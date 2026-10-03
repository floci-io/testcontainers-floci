package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for DMS (Database Migration Service)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DmsConfig config = DmsConfig.builder()
 *     .build();
 * }</pre>
 */
public class DmsConfig extends AbstractServiceConfig<DmsConfig.Builder> {

    private DmsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_DMS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DmsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DmsConfig> {

        private Builder() {
            // Allow instantiation only via DmsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DmsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DmsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DmsConfig} from this builder.
         *
         * @return the DMS (Database Migration Service) configuration
         */
        @Override
        public DmsConfig build() {
            return new DmsConfig(this);
        }
    }
}
