package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CodePipelineConfigTest {

    @Test
    void shouldApplyDefaultCodePipelineConfig() {
        CodePipelineConfig config = CodePipelineConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCodePipelineConfig() {
        CodePipelineConfig config = CodePipelineConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodePipelineConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEPIPELINE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodePipelineConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEPIPELINE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodePipelineConfig config = CodePipelineConfig.builder()
                .enabled(false)
                .build();
        CodePipelineConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
