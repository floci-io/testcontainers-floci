package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Firehose-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FirehoseConfig config = FirehoseConfig.builder()
 *     .build();
 * }</pre>
 */
public class FirehoseConfig extends AbstractServiceConfig<FirehoseConfig.Builder> {


    private FirehoseConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_FIREHOSE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link FirehoseConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FirehoseConfig> {


        private Builder() {
            // Allow instantiation only via FirehoseConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FirehoseConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FirehoseConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link FirehoseConfig} from this builder.
         *
         * @return the Firehose configuration
         */
        public FirehoseConfig build() {
            return new FirehoseConfig(this);
        }
    }
}
