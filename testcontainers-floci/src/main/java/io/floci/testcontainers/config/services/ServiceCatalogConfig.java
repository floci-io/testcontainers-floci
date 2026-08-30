package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Service Catalog-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ServiceCatalogConfig config = ServiceCatalogConfig.builder()
 *     .build();
 * }</pre>
 */
public class ServiceCatalogConfig extends AbstractServiceConfig<ServiceCatalogConfig.Builder> {

    private ServiceCatalogConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_SERVICECATALOG_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ServiceCatalogConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceCatalogConfig> {

        private Builder() {
            // Allow instantiation only via ServiceCatalogConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ServiceCatalogConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ServiceCatalogConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ServiceCatalogConfig} from this builder.
         *
         * @return the Service Catalog configuration
         */
        @Override
        public ServiceCatalogConfig build() {
            return new ServiceCatalogConfig(this);
        }
    }
}
