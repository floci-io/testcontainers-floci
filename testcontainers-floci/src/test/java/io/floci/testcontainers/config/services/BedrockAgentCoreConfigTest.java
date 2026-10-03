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
    }

    @Test
    void shouldApplyCustomBedrockAgentCoreConfig() {
        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder()
                .enabled(false)
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .build();

        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getInvokeResponse()).isEqualTo("{\"output\":\"hello\"}");
        assertThat(config.isValidateRuntimeExists()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE", "{\"output\":\"yes\"}");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder()
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE", "{\"output\":\"hello\"}");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_INVOKE_RESPONSE");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder()
                .enabled(false)
                .invokeResponse("{\"output\":\"hello\"}")
                .validateRuntimeExists(true)
                .build();
        BedrockAgentCoreConfig copy = config.toBuilder().build();

        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getInvokeResponse()).isEqualTo("{\"output\":\"hello\"}");
        assertThat(copy.isValidateRuntimeExists()).isTrue();
    }

    @Test
    void shouldApplyHarnessEchoPrefix() {
        BedrockAgentCoreConfig defaults = BedrockAgentCoreConfig.builder().build();
        assertThat(defaults.getHarnessEchoPrefix()).isEqualTo("You said: ");

        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder().harnessEchoPrefix("Echo: ").build();
        assertThat(config.getHarnessEchoPrefix()).isEqualTo("Echo: ");
        assertThat(config.toBuilder().build().getHarnessEchoPrefix()).isEqualTo("Echo: ");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX", "Echo: ");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX", "You said: ");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_ECHO_PREFIX");
    }

    @Test
    void shouldApplyHarnessEmptyReply() {
        BedrockAgentCoreConfig defaults = BedrockAgentCoreConfig.builder().build();
        assertThat(defaults.getHarnessEmptyReply()).isEqualTo("No user message was supplied.");

        BedrockAgentCoreConfig config = BedrockAgentCoreConfig.builder().harnessEmptyReply("Nothing to echo.").build();
        assertThat(config.getHarnessEmptyReply()).isEqualTo("Nothing to echo.");
        assertThat(config.toBuilder().build().getHarnessEmptyReply()).isEqualTo("Nothing to echo.");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY", "Nothing to echo.");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY", "No user message was supplied.");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_AGENT_CORE_HARNESS_EMPTY_REPLY");
    }

}
