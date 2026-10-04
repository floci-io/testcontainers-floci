package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class QueueConfigTest {

    @Test
    void shouldApplyDefaultQueueConfig() {
        QueueConfig config = QueueConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomQueueConfig() {
        QueueConfig config = QueueConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        QueueConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_QUEUE_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        QueueConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_QUEUE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        QueueConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_QUEUE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        QueueConfig config = QueueConfig.builder()
                .enabled(false)
                .build();
        QueueConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
