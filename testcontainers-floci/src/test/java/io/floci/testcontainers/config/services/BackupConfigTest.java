package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BackupConfigTest {

    @Test
    void shouldApplyDefaultBackupConfig() {
        BackupConfig config = BackupConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getJobCompletionDelaySeconds()).isEqualTo(3);
    }

    @Test
    void shouldApplyCustomBackupConfig() {
        BackupConfig config = BackupConfig.builder()
                .enabled(false)
                .jobCompletionDelaySeconds(10)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getJobCompletionDelaySeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BackupConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BACKUP_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_BACKUP_JOB_COMPLETION_DELAY_SECONDS", "3");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BackupConfig.builder()
                .jobCompletionDelaySeconds(10)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BACKUP_JOB_COMPLETION_DELAY_SECONDS", "10");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BackupConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BACKUP_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BackupConfig config = BackupConfig.builder()
                .enabled(false)
                .jobCompletionDelaySeconds(5)
                .build();
        BackupConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getJobCompletionDelaySeconds()).isEqualTo(5);
    }

}
