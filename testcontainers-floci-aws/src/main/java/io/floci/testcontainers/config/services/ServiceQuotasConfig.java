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

    private static final int DEFAULT_STANDARD_ON_DEMAND_VCPUS = 5;

    private final int standardOnDemandVcpus;

    private ServiceQuotasConfig(Builder builder) {
        super(builder);
        this.standardOnDemandVcpus = builder.standardOnDemandVcpus;
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

    /**
     * Returns the applied value of the EC2 "Running On-Demand Standard instances" vCPU quota.
     *
     * <p>EC2 checks RunInstances and StartInstances against it when {@link Ec2Config.Builder#enforceVcpuQuotas(boolean)} is enabled.
     *
     * @return the applied value of the EC2 "Running On-Demand Standard instances" vCPU quota
     */
    public int getStandardOnDemandVcpus() {
        return standardOnDemandVcpus;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SERVICEQUOTAS_STANDARD_ON_DEMAND_VCPUS", String.valueOf(standardOnDemandVcpus));
        }
    }

    /**
     * Builder for {@link ServiceQuotasConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceQuotasConfig> {

        private int standardOnDemandVcpus = DEFAULT_STANDARD_ON_DEMAND_VCPUS;

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
            this.standardOnDemandVcpus = instance.getStandardOnDemandVcpus();
        }

        /**
         * Sets the applied value of the EC2 "Running On-Demand Standard instances" vCPU quota.
         *
         * <p>EC2 checks RunInstances and StartInstances against it when {@link Ec2Config.Builder#enforceVcpuQuotas(boolean)} is enabled.
         *
         * @param standardOnDemandVcpus the applied value of the EC2 "Running On-Demand Standard instances" vCPU quota (default {@value DEFAULT_STANDARD_ON_DEMAND_VCPUS})
         * @return this builder
         */
        public Builder standardOnDemandVcpus(int standardOnDemandVcpus) {
            this.standardOnDemandVcpus = standardOnDemandVcpus;
            return this;
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
