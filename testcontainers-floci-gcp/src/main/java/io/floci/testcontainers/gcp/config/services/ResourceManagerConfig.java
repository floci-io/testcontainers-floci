package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Resource Manager of Floci GCP.
 *
 * <p>The Resource Manager API ({@code cloudresourcemanager.googleapis.com}, v1) returns project metadata and
 * manages the IAM policies of projects.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ResourceManagerConfig config = ResourceManagerConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ResourceManagerConfig extends AbstractServiceConfig<ResourceManagerConfig.Builder> {

    private ResourceManagerConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_RESOURCEMANAGER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ResourceManagerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ResourceManagerConfig> {

        private Builder() {
            // Allow instantiation only via ResourceManagerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ResourceManagerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ResourceManagerConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ResourceManagerConfig} from this builder.
         *
         * @return the Resource Manager configuration
         */
        @Override
        public ResourceManagerConfig build() {
            return new ResourceManagerConfig(this);
        }
    }
}
