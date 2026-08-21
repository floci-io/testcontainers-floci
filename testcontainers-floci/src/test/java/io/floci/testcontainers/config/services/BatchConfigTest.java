package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BatchConfigTest {

    @Test
    void shouldApplyDefaultBatchConfig() {
        BatchConfig config = BatchConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getRunnerMode()).isEqualTo("immediate");
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomBatchConfig() {
        BatchConfig config = BatchConfig.builder()
                .enabled(false)
                .runnerMode("deferred")
                .dockerNetwork("my-batch-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getRunnerMode()).isEqualTo("deferred");
        assertThat(config.getDockerNetwork()).isEqualTo("my-batch-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BatchConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BATCH_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_BATCH_RUNNER_MODE", "immediate")
                .doesNotContainKey("FLOCI_SERVICES_BATCH_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BatchConfig.builder()
                .runnerMode("deferred")
                .dockerNetwork("my-batch-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BATCH_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_BATCH_RUNNER_MODE", "deferred")
                .containsEntry("FLOCI_SERVICES_BATCH_DOCKER_NETWORK", "my-batch-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BatchConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BATCH_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_BATCH_RUNNER_MODE")
                .doesNotContainKey("FLOCI_SERVICES_BATCH_DOCKER_NETWORK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BatchConfig config = BatchConfig.builder()
                .enabled(false)
                .runnerMode("deferred")
                .dockerNetwork("test-network")
                .build();
        BatchConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getRunnerMode()).isEqualTo("deferred");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
    }

    @Test
    void shouldRequireDockerSocketWhenEnabled() {
        assertThat(BatchConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(BatchConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
