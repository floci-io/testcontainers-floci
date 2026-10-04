package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Service Usage of Floci GCP.
 *
 * <p>The Service Usage API ({@code serviceusage.googleapis.com}) enables and disables APIs of a project.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ServiceUsageConfig config = ServiceUsageConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ServiceUsageConfig extends AbstractServiceConfig<ServiceUsageConfig.Builder> {

    private ServiceUsageConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ServiceUsageConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceUsageConfig> {

        private Builder() {
            // Allow instantiation only via ServiceUsageConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ServiceUsageConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ServiceUsageConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ServiceUsageConfig} from this builder.
         *
         * @return the Service Usage configuration
         */
        @Override
        public ServiceUsageConfig build() {
            return new ServiceUsageConfig(this);
        }
    }
}
