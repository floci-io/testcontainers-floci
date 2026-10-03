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
    }

    @Test
    void shouldApplyCustomSecretsManagerConfig() {
        SecretsManagerConfig config = SecretsManagerConfig.builder()
                .enabled(false)
                .defaultRecoveryWindowDays(7)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultRecoveryWindowDays()).isEqualTo(7);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", "30");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder()
                .defaultRecoveryWindowDays(7)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", "7");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SecretsManagerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SecretsManagerConfig config = SecretsManagerConfig.builder()
                .enabled(false)
                .defaultRecoveryWindowDays(14)
                .build();
        SecretsManagerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultRecoveryWindowDays()).isEqualTo(14);
    }

    @Test
    void shouldApplyScheduledRotationEnabled() {
        SecretsManagerConfig defaults = SecretsManagerConfig.builder().build();
        assertThat(defaults.isScheduledRotationEnabled()).isEqualTo(true);

        SecretsManagerConfig config = SecretsManagerConfig.builder().scheduledRotationEnabled(false).build();
        assertThat(config.isScheduledRotationEnabled()).isEqualTo(false);
        assertThat(config.toBuilder().build().isScheduledRotationEnabled()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SECRETSMANAGER_SCHEDULED_ROTATION_ENABLED");
    }

    @Test
    void shouldApplyRotationTickSeconds() {
        SecretsManagerConfig defaults = SecretsManagerConfig.builder().build();
        assertThat(defaults.getRotationTickSeconds()).isEqualTo(60L);

        SecretsManagerConfig config = SecretsManagerConfig.builder().rotationTickSeconds(5L).build();
        assertThat(config.getRotationTickSeconds()).isEqualTo(5L);
        assertThat(config.toBuilder().build().getRotationTickSeconds()).isEqualTo(5L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS", "5");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS", "60");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SECRETSMANAGER_ROTATION_TICK_SECONDS");
    }

}
