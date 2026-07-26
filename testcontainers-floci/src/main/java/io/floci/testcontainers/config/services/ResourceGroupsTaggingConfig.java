package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Resource Groups Tagging-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ResourceGroupsTaggingConfig config = ResourceGroupsTaggingConfig.builder()
 *     .build();
 * }</pre>
 */
public class ResourceGroupsTaggingConfig extends AbstractServiceConfig<ResourceGroupsTaggingConfig.Builder> {


    private ResourceGroupsTaggingConfig(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_TAGGING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ResourceGroupsTaggingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ResourceGroupsTaggingConfig> {


        private Builder() {
            // Allow instantiation only via ResourceGroupsTaggingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ResourceGroupsTaggingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ResourceGroupsTaggingConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ResourceGroupsTaggingConfig} from this builder.
         *
         * @return the Resource Groups Tagging configuration
         */
        public ResourceGroupsTaggingConfig build() {
            return new ResourceGroupsTaggingConfig(this);
        }
    }
}
