package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EmrConfigTest {

    @Test
    void shouldApplyDefaultEmrConfig() {
        EmrConfig config = EmrConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultReleaseLabel()).isEqualTo("emr-7.5.0");
        assertThat(config.getClusterStartupDelaySeconds()).isZero();
        assertThat(config.getStepDurationSeconds()).isEqualTo(0);
        assertThat(config.getClusterTerminationDelaySeconds()).isEqualTo(0);
        assertThat(config.getRunnerMode()).isEqualTo("immediate");
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.getImageTemplate()).isEmpty();
        assertThat(config.getHiveImageTemplate()).isEmpty();
        assertThat(config.getYarnNodeMemoryMb()).isEqualTo(4096);
        assertThat(config.getYarnNodeVcores()).isEqualTo(4);
        assertThat(config.getYarnMaxWorkerNodes()).isEqualTo(4);
        assertThat(config.isReconcileContainersOnStartup()).isTrue();
    }

    @Test
    void shouldApplyCustomEmrConfig() {
        EmrConfig config = EmrConfig.builder()
                .enabled(false)
                .defaultReleaseLabel("emr-7.8.0")
                .clusterStartupDelaySeconds(10)
                .stepDurationSeconds(5)
                .clusterTerminationDelaySeconds(6)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .yarnNodeMemoryMb(2048)
                .yarnNodeVcores(2)
                .yarnMaxWorkerNodes(1)
                .reconcileContainersOnStartup(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultReleaseLabel()).isEqualTo("emr-7.8.0");
        assertThat(config.getClusterStartupDelaySeconds()).isEqualTo(10);
        assertThat(config.getStepDurationSeconds()).isEqualTo(5);
        assertThat(config.getClusterTerminationDelaySeconds()).isEqualTo(6);
        assertThat(config.getRunnerMode()).isEqualTo("docker");
        assertThat(config.getDockerNetwork()).contains("my-emr-network");
        assertThat(config.getImageTemplate()).contains("registry.internal/emr-serverless/spark/%s:20250425");
        assertThat(config.getHiveImageTemplate()).contains("registry.internal/emr-serverless/hive/%s:20250425");
        assertThat(config.getYarnNodeMemoryMb()).isEqualTo(2048);
        assertThat(config.getYarnNodeVcores()).isEqualTo(2);
        assertThat(config.getYarnMaxWorkerNodes()).isEqualTo(1);
        assertThat(config.isReconcileContainersOnStartup()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", "emr-7.5.0")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS", "0")
                .containsEntry("FLOCI_SERVICES_EMR_STEP_DURATION_SECONDS", "0")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_TERMINATION_DELAY_SECONDS", "0")
                .containsEntry("FLOCI_SERVICES_EMR_RUNNER_MODE", "immediate")
                .doesNotContainKey("FLOCI_SERVICES_EMR_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_EMR_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMR_HIVE_IMAGE_TEMPLATE")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_NODE_MEMORY_MB", "4096")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_NODE_VCORES", "4")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_MAX_WORKER_NODES", "4")
                .containsEntry("FLOCI_SERVICES_EMR_RECONCILE_CONTAINERS_ON_STARTUP", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder()
                .defaultReleaseLabel("emr-7.8.0")
                .clusterStartupDelaySeconds(10)
                .stepDurationSeconds(5)
                .clusterTerminationDelaySeconds(6)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .yarnNodeMemoryMb(2048)
                .yarnNodeVcores(2)
                .yarnMaxWorkerNodes(1)
                .reconcileContainersOnStartup(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", "emr-7.8.0")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS", "10")
                .containsEntry("FLOCI_SERVICES_EMR_STEP_DURATION_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_TERMINATION_DELAY_SECONDS", "6")
                .containsEntry("FLOCI_SERVICES_EMR_RUNNER_MODE", "docker")
                .containsEntry("FLOCI_SERVICES_EMR_DOCKER_NETWORK", "my-emr-network")
                .containsEntry("FLOCI_SERVICES_EMR_IMAGE_TEMPLATE", "registry.internal/emr-serverless/spark/%s:20250425")
                .containsEntry("FLOCI_SERVICES_EMR_HIVE_IMAGE_TEMPLATE", "registry.internal/emr-serverless/hive/%s:20250425")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_NODE_MEMORY_MB", "2048")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_NODE_VCORES", "2")
                .containsEntry("FLOCI_SERVICES_EMR_YARN_MAX_WORKER_NODES", "1")
                .containsEntry("FLOCI_SERVICES_EMR_RECONCILE_CONTAINERS_ON_STARTUP", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL")
                .doesNotContainKey("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_EMR_STEP_DURATION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_EMR_CLUSTER_TERMINATION_DELAY_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_EMR_RUNNER_MODE")
                .doesNotContainKey("FLOCI_SERVICES_EMR_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_EMR_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMR_HIVE_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMR_YARN_NODE_MEMORY_MB")
                .doesNotContainKey("FLOCI_SERVICES_EMR_YARN_NODE_VCORES")
                .doesNotContainKey("FLOCI_SERVICES_EMR_YARN_MAX_WORKER_NODES")
                .doesNotContainKey("FLOCI_SERVICES_EMR_RECONCILE_CONTAINERS_ON_STARTUP");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EmrConfig config = EmrConfig.builder()
                .enabled(false)
                .defaultReleaseLabel("emr-6.0.0")
                .clusterStartupDelaySeconds(5)
                .stepDurationSeconds(5)
                .clusterTerminationDelaySeconds(6)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .yarnNodeMemoryMb(2048)
                .yarnNodeVcores(2)
                .yarnMaxWorkerNodes(1)
                .reconcileContainersOnStartup(false)
                .build();
        EmrConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultReleaseLabel()).isEqualTo("emr-6.0.0");
        assertThat(copy.getClusterStartupDelaySeconds()).isEqualTo(5);
        assertThat(copy.getStepDurationSeconds()).isEqualTo(5);
        assertThat(copy.getClusterTerminationDelaySeconds()).isEqualTo(6);
        assertThat(copy.getRunnerMode()).isEqualTo("docker");
        assertThat(copy.getDockerNetwork()).contains("my-emr-network");
        assertThat(copy.getImageTemplate()).contains("registry.internal/emr-serverless/spark/%s:20250425");
        assertThat(copy.getHiveImageTemplate()).contains("registry.internal/emr-serverless/hive/%s:20250425");
        assertThat(copy.getYarnNodeMemoryMb()).isEqualTo(2048);
        assertThat(copy.getYarnNodeVcores()).isEqualTo(2);
        assertThat(copy.getYarnMaxWorkerNodes()).isEqualTo(1);
        assertThat(copy.isReconcileContainersOnStartup()).isFalse();
    }

    @Test
    void shouldRequireDockerSocketOnlyInDockerRunnerMode() {
        assertThat(EmrConfig.builder().build().requiresDockerSocket()).isFalse();
        assertThat(EmrConfig.builder().runnerMode("docker").build().requiresDockerSocket()).isTrue();
        assertThat(EmrConfig.builder().runnerMode("docker").enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
