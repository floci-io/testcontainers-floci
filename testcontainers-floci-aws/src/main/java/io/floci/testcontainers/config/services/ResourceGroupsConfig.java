package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Resource Groups-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ResourceGroupsConfig config = ResourceGroupsConfig.builder()
 *     .build();
 * }</pre>
 */
public class ResourceGroupsConfig extends AbstractServiceConfig<ResourceGroupsConfig.Builder> {

    private ResourceGroupsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_RESOURCE_GROUPS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ResourceGroupsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ResourceGroupsConfig> {

        private Builder() {
            // Allow instantiation only via ResourceGroupsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ResourceGroupsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ResourceGroupsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ResourceGroupsConfig} from this builder.
         *
         * @return the Resource Groups configuration
         */
        @Override
        public ResourceGroupsConfig build() {
            return new ResourceGroupsConfig(this);
        }
    }
}
