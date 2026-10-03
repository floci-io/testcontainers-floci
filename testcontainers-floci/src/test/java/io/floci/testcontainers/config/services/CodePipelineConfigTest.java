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
        assertThat(config.getSourcePollIntervalMs()).isEqualTo(500L);
    }

    @Test
    void shouldApplyCustomCodePipelineConfig() {
        CodePipelineConfig config = CodePipelineConfig.builder()
                .enabled(false)
                .sourcePollIntervalMs(100L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSourcePollIntervalMs()).isEqualTo(100L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodePipelineConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEPIPELINE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS", "500");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodePipelineConfig.builder()
                .sourcePollIntervalMs(100L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS", "100");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodePipelineConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEPIPELINE_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodePipelineConfig config = CodePipelineConfig.builder()
                .enabled(false)
                .sourcePollIntervalMs(100L)
                .build();
        CodePipelineConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSourcePollIntervalMs()).isEqualTo(100L);
    }

}
