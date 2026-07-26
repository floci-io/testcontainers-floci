package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Bedrock Runtime-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
 *     .build();
 * }</pre>
 */
public class BedrockRuntimeConfig extends AbstractServiceConfig<BedrockRuntimeConfig.Builder> {


    private BedrockRuntimeConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link BedrockRuntimeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BedrockRuntimeConfig> {


        private Builder() {
            // Allow instantiation only via BedrockRuntimeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BedrockRuntimeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BedrockRuntimeConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link BedrockRuntimeConfig} from this builder.
         *
         * @return the Bedrock Runtime configuration
         */
        public BedrockRuntimeConfig build() {
            return new BedrockRuntimeConfig(this);
        }
    }
}
