package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Control Catalog-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ControlCatalogConfig config = ControlCatalogConfig.builder()
 *     .build();
 * }</pre>
 */
public class ControlCatalogConfig extends AbstractServiceConfig<ControlCatalogConfig.Builder> {

    private ControlCatalogConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CONTROLCATALOG_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ControlCatalogConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ControlCatalogConfig> {

        private Builder() {
            // Allow instantiation only via ControlCatalogConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ControlCatalogConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ControlCatalogConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ControlCatalogConfig} from this builder.
         *
         * @return the Control Catalog configuration
         */
        @Override
        public ControlCatalogConfig build() {
            return new ControlCatalogConfig(this);
        }
    }
}
