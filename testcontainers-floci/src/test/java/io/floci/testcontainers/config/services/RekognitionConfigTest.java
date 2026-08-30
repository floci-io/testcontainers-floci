package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RekognitionConfigTest {

    @Test
    void shouldApplyDefaultRekognitionConfig() {
        RekognitionConfig config = RekognitionConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomRekognitionConfig() {
        RekognitionConfig config = RekognitionConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RekognitionConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REKOGNITION_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RekognitionConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REKOGNITION_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RekognitionConfig config = RekognitionConfig.builder()
                .enabled(false)
                .build();
        RekognitionConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
