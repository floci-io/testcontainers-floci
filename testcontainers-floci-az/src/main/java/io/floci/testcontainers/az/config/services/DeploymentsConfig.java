package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Resource Manager template deployments of Floci Azure.
 *
 * <p>ARM template deployments ({@code Microsoft.Resources/deployments}): a template's resources
 * are created through Floci Azure's own ARM handlers.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DeploymentsConfig config = DeploymentsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class DeploymentsConfig extends AbstractServiceConfig<DeploymentsConfig.Builder> {

    private DeploymentsConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_DEPLOYMENTS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DeploymentsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DeploymentsConfig> {

        private Builder() {
            // Allow instantiation only via DeploymentsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DeploymentsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DeploymentsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DeploymentsConfig} from this builder.
         *
         * @return the Deployments configuration
         */
        @Override
        public DeploymentsConfig build() {
            return new DeploymentsConfig(this);
        }
    }
}
