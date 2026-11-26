package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Resource Graph of Floci Azure.
 *
 * <p>{@code POST /providers/Microsoft.ResourceGraph/resources} with a subset of KQL over the
 * resources, resource containers and policy resources Floci Azure holds.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ResourceGraphConfig config = ResourceGraphConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ResourceGraphConfig extends AbstractServiceConfig<ResourceGraphConfig.Builder> {

    private ResourceGraphConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_RESOURCE_GRAPH_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ResourceGraphConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ResourceGraphConfig> {

        private Builder() {
            // Allow instantiation only via ResourceGraphConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ResourceGraphConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ResourceGraphConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ResourceGraphConfig} from this builder.
         *
         * @return the Resource Graph configuration
         */
        @Override
        public ResourceGraphConfig build() {
            return new ResourceGraphConfig(this);
        }
    }
}
