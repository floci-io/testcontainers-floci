package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DataSyncConfigTest {

    @Test
    void shouldApplyDefaultDataSyncConfig() {
        DataSyncConfig config = DataSyncConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomDataSyncConfig() {
        DataSyncConfig config = DataSyncConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DataSyncConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DATASYNC_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DataSyncConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DATASYNC_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DataSyncConfig config = DataSyncConfig.builder()
                .enabled(false)
                .build();
        DataSyncConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
