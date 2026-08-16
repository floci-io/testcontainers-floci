package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Bedrock AgentCore-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder()
 *     .invokeResponse("{\"output\":\"hello\"}")
 *     .build();
 * }</pre>
 */
public class BedrockAgentCoreConfig extends AbstractServiceConfig<BedrockAgentCoreConfig.Builder> {

    private static final String DEFAULT_INVOKE_RESPONSE = "{\"output\":\"yes\"}";
    private static final boolean DEFAULT_VALIDATE_RUNTIME_EXISTS = false;

    private final String invokeResponse;
    private final boolean validateRuntimeExists;

    private BedrockAgentCoreConfig(Builder builder) {
        super(builder.enabled);
        this.invokeResponse = builder.invokeResponse;
        this.validateRuntimeExists = builder.validateRuntimeExists;
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
     * Returns the hardcoded JSON response returned for runtime invocations.
     *
     * @return the invoke response (default {@value DEFAULT_INVOKE_RESPONSE})
     */
    public String getInvokeResponse() {
        return invokeResponse;
    }

    /**
     * Returns whether invoking a runtime requires that runtime to have been created first.
     *
     * @return {@code true} if runtime existence is validated before invocation
     */
    public boolean isValidateRuntimeExists() {
        return validateRuntimeExists;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BEDROCK_AGENT_CORE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE", invokeResponse);
            container.withEnv("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS",
                    String.valueOf(validateRuntimeExists));
        }
    }

    /**
     * Builder for {@link BedrockAgentCoreConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BedrockAgentCoreConfig> {

        private String invokeResponse = DEFAULT_INVOKE_RESPONSE;
        private boolean validateRuntimeExists = DEFAULT_VALIDATE_RUNTIME_EXISTS;

        private Builder() {
            // Allow instantiation only via BedrockAgentCoreConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BedrockAgentCoreConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BedrockAgentCoreConfig instance) {
            super(instance);
            this.invokeResponse = instance.getInvokeResponse();
            this.validateRuntimeExists = instance.isValidateRuntimeExists();
        }

        /**
         * Sets the hardcoded JSON response returned for runtime invocations.
         *
         * @param invokeResponse the invoke response (default {@value DEFAULT_INVOKE_RESPONSE})
         * @return this builder
         */
        public Builder invokeResponse(String invokeResponse) {
            this.invokeResponse = invokeResponse;
            return this;
        }

        /**
         * Sets whether invoking a runtime requires that runtime to have been created first.
         *
         * @param validateRuntimeExists {@code true} to validate runtime existence before invocation
         *                               (default {@value DEFAULT_VALIDATE_RUNTIME_EXISTS})
         * @return this builder
         */
        public Builder validateRuntimeExists(boolean validateRuntimeExists) {
            this.validateRuntimeExists = validateRuntimeExists;
            return this;
        }

        /**
         * Creates an immutable {@link BedrockAgentCoreConfig} from this builder.
         *
         * @return the Bedrock AgentCore configuration
         */
        @Override
        public BedrockAgentCoreConfig build() {
            return new BedrockAgentCoreConfig(this);
        }
    }
}
