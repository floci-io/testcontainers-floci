package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Bedrock-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BedrockConfig config = BedrockConfig.builder()
 *     .build();
 * }</pre>
 */
public class BedrockConfig extends AbstractServiceConfig<BedrockConfig.Builder> {

    private BedrockConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_BEDROCK_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link BedrockConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BedrockConfig> {

        private Builder() {
            // Allow instantiation only via BedrockConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BedrockConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BedrockConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link BedrockConfig} from this builder.
         *
         * @return the Bedrock configuration
         */
        @Override
        public BedrockConfig build() {
            return new BedrockConfig(this);
        }
    }
}
