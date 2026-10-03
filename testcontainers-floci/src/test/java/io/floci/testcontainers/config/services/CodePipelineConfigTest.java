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

    @Test
    void shouldApplySourcePollIntervalMs() {
        CodePipelineConfig defaults = CodePipelineConfig.builder().build();
        assertThat(defaults.getSourcePollIntervalMs()).isEqualTo(500L);

        CodePipelineConfig config = CodePipelineConfig.builder().sourcePollIntervalMs(100L).build();
        assertThat(config.getSourcePollIntervalMs()).isEqualTo(100L);
        assertThat(config.toBuilder().build().getSourcePollIntervalMs()).isEqualTo(100L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS", "100");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS", "500");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS");
    }

}
