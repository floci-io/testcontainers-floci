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
        assertThat(config.isPublishAwsvpcPortsToHost()).isFalse();
        assertThat(config.getHostVolumeRoots()).isEmpty();
        assertThat(config.isAllowUnsafeHostVolumes()).isFalse();
        assertThat(config.isReconcileContainersOnStartup()).isTrue();
        assertThat(config.getImagePullBehavior()).isEqualTo("default");
        assertThat(config.isTaskRoleCredentialsEnabled()).isFalse();
        assertThat(config.getTaskRoleCredentialsTtlSeconds()).isEqualTo(21600L);
        assertThat(config.getTaskRoleCredentialsPort()).isEqualTo(51679);
        assertThat(config.getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:local");
    }

    @Test
    void shouldApplyCustomEcsConfig() {
        EcsConfig config = EcsConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultMemoryMb(1024)
                .defaultCpuUnits(512)
                .dockerNetwork("my-ecs-network")
                .publishAwsvpcPortsToHost(true)
                .hostVolumeRoots(List.of("/data", "/srv/shared"))
                .allowUnsafeHostVolumes(true)
                .reconcileContainersOnStartup(false)
                .imagePullBehavior("prefer-cached")
                .taskRoleCredentialsEnabled(true)
                .taskRoleCredentialsTtlSeconds(900L)
                .taskRoleCredentialsPort(51680)
                .taskRoleCredentialsProxyImage("floci/network-helper:1.0")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultMemoryMb()).isEqualTo(1024);
        assertThat(config.getDefaultCpuUnits()).isEqualTo(512);
        assertThat(config.getDockerNetwork()).isEqualTo("my-ecs-network");
        assertThat(config.isPublishAwsvpcPortsToHost()).isTrue();
        assertThat(config.getHostVolumeRoots()).contains(List.of("/data", "/srv/shared"));
        assertThat(config.isAllowUnsafeHostVolumes()).isTrue();
        assertThat(config.isReconcileContainersOnStartup()).isFalse();
        assertThat(config.getImagePullBehavior()).isEqualTo("prefer-cached");
        assertThat(config.isTaskRoleCredentialsEnabled()).isTrue();
        assertThat(config.getTaskRoleCredentialsTtlSeconds()).isEqualTo(900L);
        assertThat(config.getTaskRoleCredentialsPort()).isEqualTo(51680);
        assertThat(config.getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:1.0");
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
                .doesNotContainKey("FLOCI_SERVICES_ECS_DOCKER_NETWORK")
                .containsEntry("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST", "false")
                .doesNotContainKey("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS")
                .containsEntry("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES", "false")
                .containsEntry("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP", "true")
                .containsEntry("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR", "default")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS", "21600")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT", "51679")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE", "floci/network-helper:local");
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
                .publishAwsvpcPortsToHost(true)
                .hostVolumeRoots(List.of("/data", "/srv/shared"))
                .allowUnsafeHostVolumes(true)
                .reconcileContainersOnStartup(false)
                .imagePullBehavior("prefer-cached")
                .taskRoleCredentialsEnabled(true)
                .taskRoleCredentialsTtlSeconds(900L)
                .taskRoleCredentialsPort(51680)
                .taskRoleCredentialsProxyImage("floci/network-helper:1.0")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ECS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ECS_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_MEMORY_MB", "1024")
                .containsEntry("FLOCI_SERVICES_ECS_DEFAULT_CPU_UNITS", "512")
                .containsEntry("FLOCI_SERVICES_ECS_DOCKER_NETWORK", "my-ecs-network")
                .containsEntry("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST", "true")
                .containsEntry("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS", "/data,/srv/shared")
                .containsEntry("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES", "true")
                .containsEntry("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP", "false")
                .containsEntry("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR", "prefer-cached")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS", "900")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT", "51680")
                .containsEntry("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE", "floci/network-helper:1.0");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EcsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ECS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST")
                .doesNotContainKey("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS")
                .doesNotContainKey("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES")
                .doesNotContainKey("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP")
                .doesNotContainKey("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR")
                .doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT")
                .doesNotContainKey("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EcsConfig config = EcsConfig.builder()
                .enabled(false)
                .mock(true)
                .dockerNetwork("test-network")
                .defaultMemoryMb(256)
                .defaultCpuUnits(512)
                .publishAwsvpcPortsToHost(true)
                .hostVolumeRoots(List.of("/data", "/srv/shared"))
                .allowUnsafeHostVolumes(true)
                .reconcileContainersOnStartup(false)
                .imagePullBehavior("prefer-cached")
                .taskRoleCredentialsEnabled(true)
                .taskRoleCredentialsTtlSeconds(900L)
                .taskRoleCredentialsPort(51680)
                .taskRoleCredentialsProxyImage("floci/network-helper:1.0")
                .build();
        EcsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getDefaultMemoryMb()).isEqualTo(256);
        assertThat(copy.getDefaultCpuUnits()).isEqualTo(512);
        assertThat(copy.isPublishAwsvpcPortsToHost()).isTrue();
        assertThat(copy.getHostVolumeRoots()).contains(List.of("/data", "/srv/shared"));
        assertThat(copy.isAllowUnsafeHostVolumes()).isTrue();
        assertThat(copy.isReconcileContainersOnStartup()).isFalse();
        assertThat(copy.getImagePullBehavior()).isEqualTo("prefer-cached");
        assertThat(copy.isTaskRoleCredentialsEnabled()).isTrue();
        assertThat(copy.getTaskRoleCredentialsTtlSeconds()).isEqualTo(900L);
        assertThat(copy.getTaskRoleCredentialsPort()).isEqualTo(51680);
        assertThat(copy.getTaskRoleCredentialsProxyImage()).isEqualTo("floci/network-helper:1.0");
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(EcsConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(EcsConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(EcsConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

}
