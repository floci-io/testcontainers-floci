package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Bedrock AgentCore Control-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BedrockAgentCoreControlConfig config = BedrockAgentCoreControlConfig.builder()
 *     .build();
 * }</pre>
 */
public class BedrockAgentCoreControlConfig extends AbstractServiceConfig<BedrockAgentCoreControlConfig.Builder> {

    private BedrockAgentCoreControlConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_BEDROCK_AGENT_CORE_CONTROL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link BedrockAgentCoreControlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BedrockAgentCoreControlConfig> {

        private Builder() {
            // Allow instantiation only via BedrockAgentCoreControlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BedrockAgentCoreControlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BedrockAgentCoreControlConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link BedrockAgentCoreControlConfig} from this builder.
         *
         * @return the Bedrock AgentCore Control configuration
         */
        public BedrockAgentCoreControlConfig build() {
            return new BedrockAgentCoreControlConfig(this);
        }
    }
}
