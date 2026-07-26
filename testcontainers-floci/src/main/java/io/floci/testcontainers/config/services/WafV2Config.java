package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for WAF V2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * WafV2Config config = WafV2Config.builder()
 *     .enabled(true)
 *     .build();
 * }</pre>
 */
public class WafV2Config extends AbstractServiceConfig<WafV2Config.Builder> {

    private WafV2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_WAFV2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link WafV2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, WafV2Config> {


        private Builder() {
            // Allow instantiation only via WafV2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link WafV2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(WafV2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link WafV2Config} from this builder.
         *
         * @return the WAF V2 configuration
         */
        public WafV2Config build() {
            return new WafV2Config(this);
        }
    }
}
