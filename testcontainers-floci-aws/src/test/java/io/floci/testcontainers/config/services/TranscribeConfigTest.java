package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TranscribeConfigTest {

    @Test
    void shouldApplyDefaultTranscribeConfig() {
        TranscribeConfig config = TranscribeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomTranscribeConfig() {
        TranscribeConfig config = TranscribeConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TranscribeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSCRIBE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TranscribeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSCRIBE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TranscribeConfig config = TranscribeConfig.builder()
                .enabled(false)
                .build();
        TranscribeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
