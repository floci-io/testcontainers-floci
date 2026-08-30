package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Service Quotas-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ServiceQuotasConfig config = ServiceQuotasConfig.builder()
 *     .build();
 * }</pre>
 */
public class ServiceQuotasConfig extends AbstractServiceConfig<ServiceQuotasConfig.Builder> {

    private ServiceQuotasConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ServiceQuotasConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceQuotasConfig> {

        private Builder() {
            // Allow instantiation only via ServiceQuotasConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ServiceQuotasConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ServiceQuotasConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ServiceQuotasConfig} from this builder.
         *
         * @return the Service Quotas configuration
         */
        @Override
        public ServiceQuotasConfig build() {
            return new ServiceQuotasConfig(this);
        }
    }
}
