package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class StorageConfigTest {

    @Test
    void shouldApplyDefaultStorageConfig() {
        StorageConfig config = StorageConfig.builder().build();
        assertThat(config.getHostPersistentPath()).isEmpty();
        assertThat(config.isPruneVolumesOnDelete()).isTrue();
    }

    @Test
    void shouldApplyCustomStorageConfig() {
        StorageConfig config = StorageConfig.builder()
                .randomHostPersistentPath()
                .pruneVolumesOnDelete(false)
                .build();
        assertThat(config.getHostPersistentPath()).isPresent();
        assertThat(config.isPruneVolumesOnDelete()).isFalse();
    }

    @Test
    void shouldGenerateRandomHostPersistentPath() {
        StorageConfig config = StorageConfig.builder()
                .randomHostPersistentPath()
                .build();
        assertThat(config.getHostPersistentPath()).isPresent();
        assertThat(config.getHostPersistentPath().get()).isDirectory();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StorageConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .doesNotContainKey("FLOCI_STORAGE_HOST_PERSISTENT_PATH")
                .containsEntry("FLOCI_STORAGE_PRUNE_VOLUMES_ON_DELETE", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StorageConfig.builder()
                .randomHostPersistentPath()
                .pruneVolumesOnDelete(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsKey("FLOCI_STORAGE_HOST_PERSISTENT_PATH")
                .containsEntry("FLOCI_STORAGE_PRUNE_VOLUMES_ON_DELETE", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        StorageConfig config = StorageConfig.builder()
                .randomHostPersistentPath()
                .pruneVolumesOnDelete(false)
                .build();

        StorageConfig copy = config.toBuilder().build();

        assertThat(copy.getHostPersistentPath()).isEqualTo(config.getHostPersistentPath());
        assertThat(copy.isPruneVolumesOnDelete()).isFalse();
    }
}
