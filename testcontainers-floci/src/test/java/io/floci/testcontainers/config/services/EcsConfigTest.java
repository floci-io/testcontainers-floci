package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EcsConfigTest {

    @Test
    void shouldApplyDefaultEcsConfig() {
        EcsConfig config = EcsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultMemoryMb()).isEqualTo(512);
        assertThat(config.getDefaultCpuUnits()).isEqualTo(256);
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomEcsConfig() {
        EcsConfig config = EcsConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultMemoryMb(1024)
                .defaultCpuUnits(512)
                .dockerNetwork("my-ecs-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultMemoryMb()).isEqualTo(1024);
        assertThat(config.getDefaultCpuUnits()).isEqualTo(512);
        assertThat(config.getDockerNetwork()).isEqualTo("my-ecs-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EcsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ECS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ECS_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_MEMORY_MB", "512")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_CPU_UNITS", "256")
                .doesNotContainKey("FLOCI_SERVICES_ECS_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EcsConfig.builder()
                .enabled(true)
                .mock(true)
                .defaultMemoryMb(1024)
                .defaultCpuUnits(512)
                .dockerNetwork("my-ecs-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ECS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ECS_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_MEMORY_MB", "1024")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_CPU_UNITS", "512")
                .containsEntry("FLOCI_SERVICES_ECS_DOCKER_NETWORK", "my-ecs-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EcsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EcsConfig config = EcsConfig.builder()
                .enabled(false)
                .mock(true)
                .dockerNetwork("test-network")
                .defaultMemoryMb(256)
                .defaultCpuUnits(512)
                .build();
        EcsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getDefaultMemoryMb()).isEqualTo(256);
        assertThat(copy.getDefaultCpuUnits()).isEqualTo(512);
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(EcsConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(EcsConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(EcsConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldApplyPublishAwsvpcPortsToHost() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.isPublishAwsvpcPortsToHost()).isEqualTo(false);

        EcsConfig config = EcsConfig.builder().publishAwsvpcPortsToHost(true).build();
        assertThat(config.isPublishAwsvpcPortsToHost()).isEqualTo(true);
        assertThat(config.toBuilder().build().isPublishAwsvpcPortsToHost()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST");
    }

    @Test
    void shouldApplyHostVolumeRoots() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.getHostVolumeRoots()).isEmpty();

        EcsConfig config = EcsConfig.builder().hostVolumeRoots(List.of("/data", "/srv/shared")).build();
        assertThat(config.getHostVolumeRoots()).contains(List.of("/data", "/srv/shared"));
        assertThat(config.toBuilder().build().getHostVolumeRoots()).contains(List.of("/data", "/srv/shared"));

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS", "/data,/srv/shared");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS");
    }

    @Test
    void shouldApplyAllowUnsafeHostVolumes() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.isAllowUnsafeHostVolumes()).isEqualTo(false);

        EcsConfig config = EcsConfig.builder().allowUnsafeHostVolumes(true).build();
        assertThat(config.isAllowUnsafeHostVolumes()).isEqualTo(true);
        assertThat(config.toBuilder().build().isAllowUnsafeHostVolumes()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES");
    }

    @Test
    void shouldApplyReconcileContainersOnStartup() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.isReconcileContainersOnStartup()).isEqualTo(true);

        EcsConfig config = EcsConfig.builder().reconcileContainersOnStartup(false).build();
        assertThat(config.isReconcileContainersOnStartup()).isEqualTo(false);
        assertThat(config.toBuilder().build().isReconcileContainersOnStartup()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP");
    }

    @Test
    void shouldApplyImagePullBehavior() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.getImagePullBehavior()).isEqualTo("default");

        EcsConfig config = EcsConfig.builder().imagePullBehavior("prefer-cached").build();
        assertThat(config.getImagePullBehavior()).isEqualTo("prefer-cached");
        assertThat(config.toBuilder().build().getImagePullBehavior()).isEqualTo("prefer-cached");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR", "prefer-cached");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR", "default");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR");
    }

    @Test
    void shouldApplyTaskRoleCredentialsEnabled() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.isTaskRoleCredentialsEnabled()).isEqualTo(false);

        EcsConfig config = EcsConfig.builder().taskRoleCredentialsEnabled(true).build();
        assertThat(config.isTaskRoleCredentialsEnabled()).isEqualTo(true);
        assertThat(config.toBuilder().build().isTaskRoleCredentialsEnabled()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED");
    }

    @Test
    void shouldApplyTaskRoleCredentialsTtlSeconds() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.getTaskRoleCredentialsTtlSeconds()).isEqualTo(21600L);

        EcsConfig config = EcsConfig.builder().taskRoleCredentialsTtlSeconds(900L).build();
        assertThat(config.getTaskRoleCredentialsTtlSeconds()).isEqualTo(900L);
        assertThat(config.toBuilder().build().getTaskRoleCredentialsTtlSeconds()).isEqualTo(900L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS", "900");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS", "21600");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS");
    }

    @Test
    void shouldApplyTaskRoleCredentialsPort() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.getTaskRoleCredentialsPort()).isEqualTo(51679);

        EcsConfig config = EcsConfig.builder().taskRoleCredentialsPort(51680).build();
        assertThat(config.getTaskRoleCredentialsPort()).isEqualTo(51680);
        assertThat(config.toBuilder().build().getTaskRoleCredentialsPort()).isEqualTo(51680);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT", "51680");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT", "51679");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT");
    }

    @Test
    void shouldApplyTaskRoleCredentialsProxyImage() {
        EcsConfig defaults = EcsConfig.builder().build();
        assertThat(defaults.getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:local");

        EcsConfig config = EcsConfig.builder().taskRoleCredentialsProxyImage("floci/network-helper:1.0").build();
        assertThat(config.getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:1.0");
        assertThat(config.toBuilder().build().getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:1.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE", "floci/network-helper:1.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE", "floci/network-helper:local");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE");
    }

}
