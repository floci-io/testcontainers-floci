package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class S3VectorsConfigTest {

    @Test
    void shouldApplyDefaultS3VectorsConfig() {
        S3VectorsConfig config = S3VectorsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomS3VectorsConfig() {
        S3VectorsConfig config = S3VectorsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        S3VectorsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_S3VECTORS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        S3VectorsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_S3VECTORS_ENABLED", "false");
    }
}
