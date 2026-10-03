package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SecretsManagerConfigTest {

    @Test
    void shouldApplyDefaultSecretsManagerConfig() {
        SecretsManagerConfig config = SecretsManagerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultRecoveryWindowDays()).isEqualTo(30);
        assertThat(config.isScheduledRotationEnabled()).isTrue();
        assertThat(config.getRotationTickSeconds()).isEqualTo(60L);
    }

    @Test
    void shouldApplyCustomSecretsManagerConfig() {
        SecretsManagerConfig config = SecretsManagerConfig.builder()
                .enabled(false)
                .defaultRecoveryWindowDays(7)
                .scheduledRotationEnabled(false)
                .rotationTickSeconds(5L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultRecoveryWindowDays()).isEqualTo(7);
        assertThat(config.isScheduledRotationEnabled()).isFalse();
        assertThat(config.getRotationTickSeconds()).isEqualTo(5L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", "30")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder()
                .defaultRecoveryWindowDays(7)
                .scheduledRotationEnabled(false)
                .rotationTickSeconds(5L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", "7")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SecretsManagerConfig config = SecretsManagerConfig.builder()
                .enabled(false)
                .defaultRecoveryWindowDays(14)
                .scheduledRotationEnabled(false)
                .rotationTickSeconds(5L)
                .build();
        SecretsManagerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultRecoveryWindowDays()).isEqualTo(14);
        assertThat(copy.isScheduledRotationEnabled()).isFalse();
        assertThat(copy.getRotationTickSeconds()).isEqualTo(5L);
    }

}
