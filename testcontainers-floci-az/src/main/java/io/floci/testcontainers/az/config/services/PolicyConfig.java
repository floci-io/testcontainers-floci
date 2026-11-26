package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Policy of Floci Azure.
 *
 * <p>Azure Policy under {@code Microsoft.Authorization}: policy definitions, set definitions
 * (initiatives), assignments and exemptions, and the evaluation of assigned rules on resource writes
 * ({@code deny}, {@code append}, {@code audit}). The compliance states the evaluation records are served
 * under {@code Microsoft.PolicyInsights}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PolicyConfig config = PolicyConfig.builder()
 *     .enforcement(false)
 *     .insights(false)
 *     .build();
 * }</pre>
 */
public class PolicyConfig extends AbstractServiceConfig<PolicyConfig.Builder> {

    private static final boolean DEFAULT_ENFORCEMENT = true;
    private static final boolean DEFAULT_INSIGHTS = true;

    private final boolean enforcement;
    private final boolean insights;

    private PolicyConfig(Builder builder) {
        super(builder);
        this.enforcement = builder.enforcement;
        this.insights = builder.insights;
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
     * Returns whether assigned policy rules are evaluated on every PUT and PATCH of a resource:
     * {@code deny} refuses the request with {@code RequestDisallowedByPolicy}, {@code append} amends it,
     * {@code audit} is recorded. When off, policies are still stored, but nothing is evaluated.
     *
     * @return {@code true} if policy rules are enforced
     */
    public boolean isEnforcement() {
        return enforcement;
    }

    /**
     * Returns whether {@code Microsoft.PolicyInsights} is served: the compliance states the evaluation
     * records, their query and summary operations, and on-demand compliance scans
     * ({@code triggerEvaluation}). Records nothing unless {@link #isEnforcement()} is on.
     *
     * @return {@code true} if Policy Insights is served
     */
    public boolean isInsights() {
        return insights;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_POLICY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_POLICY_ENFORCEMENT", String.valueOf(enforcement));
            container.withEnv("FLOCI_AZ_SERVICES_POLICY_INSIGHTS", String.valueOf(insights));
        }
    }

    /**
     * Builder for {@link PolicyConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PolicyConfig> {

        private boolean enforcement = DEFAULT_ENFORCEMENT;
        private boolean insights = DEFAULT_INSIGHTS;

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
            this.enforcement = instance.enforcement;
            this.insights = instance.insights;
        }

        /**
         * Sets whether assigned policy rules are evaluated on every PUT and PATCH of a resource:
         * {@code deny} refuses the request with {@code RequestDisallowedByPolicy}, {@code append} amends
         * it, {@code audit} is recorded. When off, policies are still stored, but nothing is evaluated.
         *
         * @param enforcement {@code true} to enforce policy rules (default {@value DEFAULT_ENFORCEMENT})
         * @return this builder
         */
        public Builder enforcement(boolean enforcement) {
            this.enforcement = enforcement;
            return this;
        }

        /**
         * Sets whether {@code Microsoft.PolicyInsights} is served: the compliance states the evaluation
         * records, their query and summary operations, and on-demand compliance scans
         * ({@code triggerEvaluation}). Records nothing unless {@link #enforcement(boolean)} is on.
         *
         * @param insights {@code true} to serve Policy Insights (default {@value DEFAULT_INSIGHTS})
         * @return this builder
         */
        public Builder insights(boolean insights) {
            this.insights = insights;
            return this;
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
