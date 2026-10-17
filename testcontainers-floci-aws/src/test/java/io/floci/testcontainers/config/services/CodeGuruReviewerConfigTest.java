package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CodeGuruReviewerConfigTest {

    @Test
    void shouldApplyDefaultCodeGuruReviewerConfig() {
        CodeGuruReviewerConfig config = CodeGuruReviewerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCodeGuruReviewerConfig() {
        CodeGuruReviewerConfig config = CodeGuruReviewerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeGuruReviewerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEGURUREVIEWER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeGuruReviewerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEGURUREVIEWER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodeGuruReviewerConfig config = CodeGuruReviewerConfig.builder()
                .enabled(false)
                .build();
        CodeGuruReviewerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
