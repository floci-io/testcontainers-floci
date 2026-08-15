package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockAgentCoreControlConfigTest {

    @Test
    void shouldApplyDefaultBedrockAgentCoreControlConfig() {
        BedrockAgentCoreControlConfig config = BedrockAgentCoreControlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomBedrockAgentCoreControlConfig() {
        BedrockAgentCoreControlConfig config = BedrockAgentCoreControlConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreControlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_CONTROL_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockAgentCoreControlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_AGENT_CORE_CONTROL_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockAgentCoreControlConfig config = BedrockAgentCoreControlConfig.builder()
                .enabled(false)
                .build();
        BedrockAgentCoreControlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
