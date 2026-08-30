package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Resource Explorer v2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ResourceExplorer2Config config = ResourceExplorer2Config.builder()
 *     .build();
 * }</pre>
 */
public class ResourceExplorer2Config extends AbstractServiceConfig<ResourceExplorer2Config.Builder> {

    private ResourceExplorer2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_RESOURCEEXPLORER2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ResourceExplorer2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ResourceExplorer2Config> {

        private Builder() {
            // Allow instantiation only via ResourceExplorer2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ResourceExplorer2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ResourceExplorer2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ResourceExplorer2Config} from this builder.
         *
         * @return the Resource Explorer v2 configuration
         */
        @Override
        public ResourceExplorer2Config build() {
            return new ResourceExplorer2Config(this);
        }
    }
}
