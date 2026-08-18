package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for GuardDuty-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GuardDutyConfig config = GuardDutyConfig.builder()
 *     .build();
 * }</pre>
 */
public class GuardDutyConfig extends AbstractServiceConfig<GuardDutyConfig.Builder> {

    private GuardDutyConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_GUARDDUTY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link GuardDutyConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GuardDutyConfig> {

        private Builder() {
            // Allow instantiation only via GuardDutyConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GuardDutyConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GuardDutyConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link GuardDutyConfig} from this builder.
         *
         * @return the GuardDuty configuration
         */
        @Override
        public GuardDutyConfig build() {
            return new GuardDutyConfig(this);
        }
    }
}
