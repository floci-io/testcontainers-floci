package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Management Groups of Floci Azure.
 *
 * <p>Management groups ({@code Microsoft.Management}): the tenant hierarchy of groups and
 * subscriptions, with descendants and subscription membership.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ManagementGroupsConfig config = ManagementGroupsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ManagementGroupsConfig extends AbstractServiceConfig<ManagementGroupsConfig.Builder> {

    private ManagementGroupsConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_MANAGEMENT_GROUPS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ManagementGroupsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ManagementGroupsConfig> {

        private Builder() {
            // Allow instantiation only via ManagementGroupsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ManagementGroupsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ManagementGroupsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ManagementGroupsConfig} from this builder.
         *
         * @return the Management Groups configuration
         */
        @Override
        public ManagementGroupsConfig build() {
            return new ManagementGroupsConfig(this);
        }
    }
}
