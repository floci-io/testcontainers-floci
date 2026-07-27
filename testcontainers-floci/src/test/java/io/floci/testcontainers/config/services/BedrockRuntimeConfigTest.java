package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockRuntimeConfigTest {

    @Test
    void shouldApplyDefaultBedrockRuntimeConfig() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomBedrockRuntimeConfig() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockRuntimeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockRuntimeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
                .enabled(false)
                .build();
        BedrockRuntimeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
