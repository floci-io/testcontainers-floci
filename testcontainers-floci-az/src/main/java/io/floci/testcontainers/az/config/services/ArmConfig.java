package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Resource Manager (ARM) of Floci Azure.
 *
 * <p>The management plane entry point for all {@code /providers/...}, {@code /subscriptions} and
 * resource-group calls. Disabling it turns off every ARM-based service at once.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ArmConfig config = ArmConfig.builder()
 *     .defaultSubscriptionId("22222222-2222-2222-2222-222222222222")
 *     .build();
 * }</pre>
 */
public class ArmConfig extends AbstractServiceConfig<ArmConfig.Builder> {

    private static final String DEFAULT_DEFAULT_SUBSCRIPTION_ID = "00000000-0000-0000-0000-000000000001";

    private final String defaultSubscriptionId;

    private ArmConfig(Builder builder) {
        super(builder);
        this.defaultSubscriptionId = builder.defaultSubscriptionId;
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
     * Returns the subscription that always exists in Floci Azure's directory, listed first by
     * {@code GET /subscriptions} (and therefore the one {@code az login} selects). By default, any
     * other subscription id is registered on first use.
     *
     * @return the default subscription id
     */
    public String getDefaultSubscriptionId() {
        return defaultSubscriptionId;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_ARM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_ARM_DEFAULT_SUBSCRIPTION_ID", defaultSubscriptionId);
        }
    }

    /**
     * Builder for {@link ArmConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ArmConfig> {

        private String defaultSubscriptionId = DEFAULT_DEFAULT_SUBSCRIPTION_ID;

        private Builder() {
            // Allow instantiation only via ArmConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ArmConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ArmConfig instance) {
            super(instance);
            this.defaultSubscriptionId = instance.defaultSubscriptionId;
        }

        /**
         * Sets the subscription that always exists in Floci Azure's directory, listed first by
         * {@code GET /subscriptions} (and therefore the one {@code az login} selects). By default, any
         * other subscription id is registered on first use, so clients configured with their own
         * subscription keep working.
         *
         * @param defaultSubscriptionId the default subscription id (default {@value DEFAULT_DEFAULT_SUBSCRIPTION_ID})
         * @return this builder
         */
        public Builder defaultSubscriptionId(String defaultSubscriptionId) {
            this.defaultSubscriptionId = defaultSubscriptionId;
            return this;
        }

        /**
         * Creates an immutable {@link ArmConfig} from this builder.
         *
         * @return the ARM configuration
         */
        @Override
        public ArmConfig build() {
            return new ArmConfig(this);
        }
    }
}
