package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudControlConfigTest {

    @Test
    void shouldApplyDefaultCloudControlConfig() {
        CloudControlConfig config = CloudControlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getCreateWorkerThreads()).isEqualTo(4);
        assertThat(config.getCreateQueueCapacity()).isEqualTo(64);
    }

    @Test
    void shouldApplyCustomCloudControlConfig() {
        CloudControlConfig config = CloudControlConfig.builder()
                .enabled(false)
                .createWorkerThreads(8)
                .createQueueCapacity(128)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getCreateWorkerThreads()).isEqualTo(8);
        assertThat(config.getCreateQueueCapacity()).isEqualTo(128);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudControlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_CREATE_WORKER_THREADS", "4")
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_CREATE_QUEUE_CAPACITY", "64");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudControlConfig.builder()
                .createWorkerThreads(8)
                .createQueueCapacity(128)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_CREATE_WORKER_THREADS", "8")
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_CREATE_QUEUE_CAPACITY", "128");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudControlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDCONTROL_CREATE_WORKER_THREADS")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDCONTROL_CREATE_QUEUE_CAPACITY");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudControlConfig config = CloudControlConfig.builder()
                .enabled(false)
                .createWorkerThreads(8)
                .createQueueCapacity(128)
                .build();
        CloudControlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getCreateWorkerThreads()).isEqualTo(8);
        assertThat(copy.getCreateQueueCapacity()).isEqualTo(128);
    }

}
