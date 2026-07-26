package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for SNS-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SnsConfig config = SnsConfig.builder()
 *     .build();
 * }</pre>
 */
public class SnsConfig extends AbstractServiceConfig<SnsConfig.Builder> {

    private SnsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_SNS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link SnsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SnsConfig> {


        private Builder() {
            // Allow instantiation only via SnsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SnsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SnsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link SnsConfig} from this builder.
         *
         * @return the SNS configuration
         */
        public SnsConfig build() {
            return new SnsConfig(this);
        }
    }
}
