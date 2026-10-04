package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class LoggingConfigTest {

    @Test
    void shouldApplyDefaultLoggingConfig() {
        LoggingConfig config = LoggingConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomLoggingConfig() {
        LoggingConfig config = LoggingConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LoggingConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_LOGGING_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LoggingConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_LOGGING_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        LoggingConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_LOGGING_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        LoggingConfig config = LoggingConfig.builder()
                .enabled(false)
                .build();
        LoggingConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
