package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DlmConfigTest {

    @Test
    void shouldApplyDefaultDlmConfig() {
        DlmConfig config = DlmConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomDlmConfig() {
        DlmConfig config = DlmConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DlmConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DLM_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DlmConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DLM_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DlmConfig config = DlmConfig.builder()
                .enabled(false)
                .build();
        DlmConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
