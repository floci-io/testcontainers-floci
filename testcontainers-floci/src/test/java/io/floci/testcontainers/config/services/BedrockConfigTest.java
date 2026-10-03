package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockConfigTest {

    @Test
    void shouldApplyDefaultBedrockConfig() {
        BedrockConfig config = BedrockConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomBedrockConfig() {
        BedrockConfig config = BedrockConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockConfig config = BedrockConfig.builder()
                .enabled(false)
                .build();
        BedrockConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
