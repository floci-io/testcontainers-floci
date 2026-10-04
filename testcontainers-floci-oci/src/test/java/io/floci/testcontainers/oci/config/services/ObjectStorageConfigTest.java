package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectStorageConfigTest {

    @Test
    void shouldApplyDefaultObjectStorageConfig() {
        ObjectStorageConfig config = ObjectStorageConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomObjectStorageConfig() {
        ObjectStorageConfig config = ObjectStorageConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ObjectStorageConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OBJECTSTORAGE_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ObjectStorageConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OBJECTSTORAGE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ObjectStorageConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_OBJECTSTORAGE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ObjectStorageConfig config = ObjectStorageConfig.builder()
                .enabled(false)
                .build();
        ObjectStorageConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
