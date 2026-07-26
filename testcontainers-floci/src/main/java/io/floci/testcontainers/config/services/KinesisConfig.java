package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Kinesis-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KinesisConfig config = KinesisConfig.builder()
 *     .build();
 * }</pre>
 */
public class KinesisConfig extends AbstractServiceConfig<KinesisConfig.Builder> {

    private KinesisConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_KINESIS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link KinesisConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KinesisConfig> {


        private Builder() {
            // Allow instantiation only via KinesisConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KinesisConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KinesisConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link KinesisConfig} from this builder.
         *
         * @return the Kinesis configuration
         */
        public KinesisConfig build() {
            return new KinesisConfig(this);
        }
    }
}
