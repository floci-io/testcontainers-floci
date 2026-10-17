package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Policy of Floci Azure.
 *
 * <p>The Azure Policy control plane under {@code Microsoft.Authorization}: policy definitions, set
 * definitions (initiatives), assignments and exemptions. Control plane only: policy rules are
 * stored, but never evaluated against resource requests.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PolicyConfig config = PolicyConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class PolicyConfig extends AbstractServiceConfig<PolicyConfig.Builder> {

    private PolicyConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_POLICY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link PolicyConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PolicyConfig> {

        private Builder() {
            // Allow instantiation only via PolicyConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PolicyConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PolicyConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link PolicyConfig} from this builder.
         *
         * @return the Policy configuration
         */
        @Override
        public PolicyConfig build() {
            return new PolicyConfig(this);
        }
    }
}
