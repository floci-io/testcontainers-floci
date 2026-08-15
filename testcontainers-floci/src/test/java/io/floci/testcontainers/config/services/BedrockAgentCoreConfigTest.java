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

}
