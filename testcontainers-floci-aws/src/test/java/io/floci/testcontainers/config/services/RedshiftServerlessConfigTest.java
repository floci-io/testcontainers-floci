package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftServerlessConfigTest {

    @Test
    void shouldApplyDefaultRedshiftServerlessConfig() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomRedshiftServerlessConfig() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftServerlessConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftServerlessConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_SERVERLESS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftServerlessConfig config = RedshiftServerlessConfig.builder()
                .enabled(false)
                .build();
        RedshiftServerlessConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
