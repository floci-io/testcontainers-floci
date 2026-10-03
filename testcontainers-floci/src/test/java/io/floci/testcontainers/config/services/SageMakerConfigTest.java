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
    }

    @Test
    void shouldApplyCustomSageMakerConfig() {
        SageMakerConfig config = SageMakerConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SageMakerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SageMakerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SageMakerConfig config = SageMakerConfig.builder()
                .enabled(false)
                .build();
        SageMakerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDockerNetwork() {
        SageMakerConfig defaults = SageMakerConfig.builder().build();
        assertThat(defaults.getDockerNetwork()).isEmpty();

        SageMakerConfig config = SageMakerConfig.builder().dockerNetwork("my-network").build();
        assertThat(config.getDockerNetwork()).contains("my-network");
        assertThat(config.toBuilder().build().getDockerNetwork()).contains("my-network");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK", "my-network");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyGpuEnabled() {
        SageMakerConfig defaults = SageMakerConfig.builder().build();
        assertThat(defaults.isGpuEnabled()).isEqualTo(false);

        SageMakerConfig config = SageMakerConfig.builder().gpuEnabled(true).build();
        assertThat(config.isGpuEnabled()).isEqualTo(true);
        assertThat(config.toBuilder().build().isGpuEnabled()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED");
    }

    @Test
    void shouldApplyGpuMode() {
        SageMakerConfig defaults = SageMakerConfig.builder().build();
        assertThat(defaults.getGpuMode()).isEqualTo("cdi");

        SageMakerConfig config = SageMakerConfig.builder().gpuMode("count").build();
        assertThat(config.getGpuMode()).isEqualTo("count");
        assertThat(config.toBuilder().build().getGpuMode()).isEqualTo("count");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_MODE", "count");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_MODE", "cdi");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_MODE");
    }

    @Test
    void shouldApplyGpuDevices() {
        SageMakerConfig defaults = SageMakerConfig.builder().build();
        assertThat(defaults.getGpuDevices()).isEmpty();

        SageMakerConfig config = SageMakerConfig.builder().gpuDevices(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1")).build();
        assertThat(config.getGpuDevices()).contains(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"));
        assertThat(config.toBuilder().build().getGpuDevices()).contains(List.of("nvidia.com/gpu=GPU-0", "nvidia.com/gpu=GPU-1"));

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES", "nvidia.com/gpu=GPU-0,nvidia.com/gpu=GPU-1");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(SageMakerConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(SageMakerConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
