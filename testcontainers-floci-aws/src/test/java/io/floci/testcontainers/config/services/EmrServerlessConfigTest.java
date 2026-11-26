package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EmrServerlessConfigTest {

    @Test
    void shouldApplyDefaultEmrServerlessConfig() {
        EmrServerlessConfig config = EmrServerlessConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getJobRunDurationSeconds()).isEqualTo(0);
        assertThat(config.getRunnerMode()).isEqualTo("immediate");
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.getImageTemplate()).isEmpty();
        assertThat(config.getHiveImageTemplate()).isEmpty();
        assertThat(config.getExecutorContainers()).isEqualTo(0);
        assertThat(config.isHonourArchitectures()).isFalse();
        assertThat(config.isReconcileContainersOnStartup()).isTrue();
    }

    @Test
    void shouldApplyCustomEmrServerlessConfig() {
        EmrServerlessConfig config = EmrServerlessConfig.builder()
                .enabled(false)
                .jobRunDurationSeconds(5)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .executorContainers(2)
                .honourArchitectures(true)
                .reconcileContainersOnStartup(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getJobRunDurationSeconds()).isEqualTo(5);
        assertThat(config.getRunnerMode()).isEqualTo("docker");
        assertThat(config.getDockerNetwork()).contains("my-emr-network");
        assertThat(config.getImageTemplate()).contains("registry.internal/emr-serverless/spark/%s:20250425");
        assertThat(config.getHiveImageTemplate()).contains("registry.internal/emr-serverless/hive/%s:20250425");
        assertThat(config.getExecutorContainers()).isEqualTo(2);
        assertThat(config.isHonourArchitectures()).isTrue();
        assertThat(config.isReconcileContainersOnStartup()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrServerlessConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_JOB_RUN_DURATION_SECONDS", "0")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_RUNNER_MODE", "immediate")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_HIVE_IMAGE_TEMPLATE")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_EXECUTOR_CONTAINERS", "0")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_HONOUR_ARCHITECTURES", "false")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_RECONCILE_CONTAINERS_ON_STARTUP", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrServerlessConfig.builder()
                .jobRunDurationSeconds(5)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .executorContainers(2)
                .honourArchitectures(true)
                .reconcileContainersOnStartup(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_JOB_RUN_DURATION_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_RUNNER_MODE", "docker")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_DOCKER_NETWORK", "my-emr-network")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_IMAGE_TEMPLATE", "registry.internal/emr-serverless/spark/%s:20250425")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_HIVE_IMAGE_TEMPLATE", "registry.internal/emr-serverless/hive/%s:20250425")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_EXECUTOR_CONTAINERS", "2")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_HONOUR_ARCHITECTURES", "true")
                .containsEntry("FLOCI_SERVICES_EMRSERVERLESS_RECONCILE_CONTAINERS_ON_STARTUP", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrServerlessConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_JOB_RUN_DURATION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_RUNNER_MODE")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_HIVE_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_EXECUTOR_CONTAINERS")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_HONOUR_ARCHITECTURES")
                .doesNotContainKey("FLOCI_SERVICES_EMRSERVERLESS_RECONCILE_CONTAINERS_ON_STARTUP");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EmrServerlessConfig config = EmrServerlessConfig.builder()
                .enabled(false)
                .jobRunDurationSeconds(5)
                .runnerMode("docker")
                .dockerNetwork("my-emr-network")
                .imageTemplate("registry.internal/emr-serverless/spark/%s:20250425")
                .hiveImageTemplate("registry.internal/emr-serverless/hive/%s:20250425")
                .executorContainers(2)
                .honourArchitectures(true)
                .reconcileContainersOnStartup(false)
                .build();
        EmrServerlessConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getJobRunDurationSeconds()).isEqualTo(5);
        assertThat(copy.getRunnerMode()).isEqualTo("docker");
        assertThat(copy.getDockerNetwork()).contains("my-emr-network");
        assertThat(copy.getImageTemplate()).contains("registry.internal/emr-serverless/spark/%s:20250425");
        assertThat(copy.getHiveImageTemplate()).contains("registry.internal/emr-serverless/hive/%s:20250425");
        assertThat(copy.getExecutorContainers()).isEqualTo(2);
        assertThat(copy.isHonourArchitectures()).isTrue();
        assertThat(copy.isReconcileContainersOnStartup()).isFalse();
    }

    @Test
    void shouldRequireDockerSocketOnlyInDockerRunnerMode() {
        assertThat(EmrServerlessConfig.builder().build().requiresDockerSocket()).isFalse();
        assertThat(EmrServerlessConfig.builder().runnerMode("docker").build().requiresDockerSocket()).isTrue();
        assertThat(EmrServerlessConfig.builder().runnerMode("docker").enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
