package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AccessAnalyzerConfigTest {

    @Test
    void shouldApplyDefaultAccessAnalyzerConfig() {
        AccessAnalyzerConfig config = AccessAnalyzerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomAccessAnalyzerConfig() {
        AccessAnalyzerConfig config = AccessAnalyzerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AccessAnalyzerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ACCESSANALYZER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AccessAnalyzerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ACCESSANALYZER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AccessAnalyzerConfig config = AccessAnalyzerConfig.builder()
                .enabled(false)
                .build();
        AccessAnalyzerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
