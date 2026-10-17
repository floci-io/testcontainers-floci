package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockAgentCoreConfigTest {

    @Test
    void shouldApplyDefaultBedrockAgentCoreConfig() {
        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getInvokeResponse()).isEqualTo("{\"output\":\"yes\"}");
        assertThat(config.isValidateRuntimeExists()).isFalse();
        assertThat(config.getHarnessEchoPrefix()).isEqualTo("You said: ");
        assertThat(config.getHarnessEmptyReply()).isEqualTo("No user message was supplied.");
    }

    @Test
    void shouldApplyCustomBedrockAgentCoreConfig() {
        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder()
                .enabled(false)
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .harnessEchoPrefix("Echo: ")
                .harnessEmptyReply("Nothing to echo.")
                .build();

        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getInvokeResponse()).isEqualTo("{\"output\":\"hello\"}");
        assertThat(config.isValidateRuntimeExists()).isTrue();
        assertThat(config.getHarnessEchoPrefix()).isEqualTo("Echo: ");
        assertThat(config.getHarnessEmptyReply()).isEqualTo("Nothing to echo.");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE", "{\"output\":\"yes\"}");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS", "false");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX", "You said: ");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY", "No user message was supplied.");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder()
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .harnessEchoPrefix("Echo: ")
                .harnessEmptyReply("Nothing to echo.")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE", "{\"output\":\"hello\"}");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX", "Echo: ");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY", "Nothing to echo.");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder()
                .enabled(false)
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .harnessEchoPrefix("Echo: ")
                .harnessEmptyReply("Nothing to echo.")
                .build();
        BedrockAgentCoreConfig copy = config.toBuilder().build();

        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getInvokeResponse()).isEqualTo("{\"output\":\"hello\"}");
        assertThat(copy.isValidateRuntimeExists()).isTrue();
        assertThat(copy.getHarnessEchoPrefix()).isEqualTo("Echo: ");
        assertThat(copy.getHarnessEmptyReply()).isEqualTo("Nothing to echo.");
    }

}
