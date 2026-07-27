package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AppSyncConfigTest {

    @Test
    void shouldApplyDefaultAppSyncConfig() {
        AppSyncConfig config = AppSyncConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getSchemaWorkerThreads()).isEqualTo(4);
        assertThat(config.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyCustomAppSyncConfig() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSchemaWorkerThreads()).isEqualTo(8);
        assertThat(config.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "4")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder()
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "8")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(2)
                .schemaWorkerShutdownTimeoutSeconds(10)
                .build();
        AppSyncConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSchemaWorkerThreads()).isEqualTo(2);
        assertThat(copy.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(10);
    }

}
