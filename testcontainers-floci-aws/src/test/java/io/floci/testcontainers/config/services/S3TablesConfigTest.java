package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class S3TablesConfigTest {

    @Test
    void shouldApplyDefaultS3TablesConfig() {
        S3TablesConfig config = S3TablesConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomS3TablesConfig() {
        S3TablesConfig config = S3TablesConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        S3TablesConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_S3TABLES_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        S3TablesConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_S3TABLES_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        S3TablesConfig config = S3TablesConfig.builder()
                .enabled(false)
                .build();
        S3TablesConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
