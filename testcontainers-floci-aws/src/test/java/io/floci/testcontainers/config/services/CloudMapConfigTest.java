package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudMapConfigTest {

    @Test
    void shouldApplyDefaultCloudMapConfig() {
        CloudMapConfig config = CloudMapConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getOperationCompletionDelaySeconds()).isZero();
    }

    @Test
    void shouldApplyCustomCloudMapConfig() {
        CloudMapConfig config = CloudMapConfig.builder()
                .enabled(false)
                .operationCompletionDelaySeconds(5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getOperationCompletionDelaySeconds()).isEqualTo(5);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudMapConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDMAP_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDMAP_OPERATION_COMPLETION_DELAY_SECONDS", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudMapConfig.builder()
                .operationCompletionDelaySeconds(5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDMAP_OPERATION_COMPLETION_DELAY_SECONDS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudMapConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDMAP_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDMAP_OPERATION_COMPLETION_DELAY_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudMapConfig config = CloudMapConfig.builder()
                .enabled(false)
                .operationCompletionDelaySeconds(5)
                .build();
        CloudMapConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getOperationCompletionDelaySeconds()).isEqualTo(5);
    }

}
