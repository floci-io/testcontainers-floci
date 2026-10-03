package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SageMakerConfigTest {

    @Test
    void shouldApplyDefaultSageMakerConfig() {
        SageMakerConfig config = SageMakerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.isGpuEnabled()).isFalse();
        assertThat(config.getGpuMode()).isEqualTo("cdi");
        assertThat(config.getGpuDevices()).isEmpty();
    }

    @Test
    void shouldApplyCustomSageMakerConfig() {
        SageMakerConfig config = SageMakerConfig.builder()
                .enabled(false)
                .dockerNetwork("my-network")
                .gpuEnabled(true)
                .gpuMode("count")
                .gpuDevices(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"))
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDockerNetwork()).contains("my-network");
        assertThat(config.isGpuEnabled()).isTrue();
        assertThat(config.getGpuMode()).isEqualTo("count");
        assertThat(config.getGpuDevices()).contains(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"));
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SageMakerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK")
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_MODE", "cdi")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SageMakerConfig.builder()
                .dockerNetwork("my-network")
                .gpuEnabled(true)
                .gpuMode("count")
                .gpuDevices(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK", "my-network")
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_MODE", "count")
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES", "nvidia.com/gpu=GPU-0,nvidia.com/gpu=GPU-1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SageMakerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SAGEMAKER_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_MODE")
                .doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SageMakerConfig config = SageMakerConfig.builder()
                .enabled(false)
                .dockerNetwork("my-network")
                .gpuEnabled(true)
                .gpuMode("count")
                .gpuDevices(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"))
                .build();
        SageMakerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDockerNetwork()).contains("my-network");
        assertThat(copy.isGpuEnabled()).isTrue();
        assertThat(copy.getGpuMode()).isEqualTo("count");
        assertThat(copy.getGpuDevices()).contains(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"));
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(SageMakerConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(SageMakerConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
