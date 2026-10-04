package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ComputeConfigTest {

    @Test
    void shouldApplyDefaultComputeConfig() {
        ComputeConfig config = ComputeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getOperationDelayMs()).isEqualTo(50);
        assertThat(config.getRegions()).containsExactly("us-central1", "europe-west1");
    }

    @Test
    void shouldApplyCustomComputeConfig() {
        ComputeConfig config = ComputeConfig.builder()
                .enabled(false)
                .operationDelayMs(0)
                .regions(List.of("europe-west3"))
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getOperationDelayMs()).isZero();
        assertThat(config.getRegions()).containsExactly("europe-west3");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", "50")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", "us-central1,europe-west1");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder()
                .operationDelayMs(0)
                .regions(List.of("europe-west3", "us-east1"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", "0")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", "europe-west3,us-east1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_REGIONS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ComputeConfig config = ComputeConfig.builder()
                .enabled(false)
                .operationDelayMs(0)
                .regions(List.of("europe-west3"))
                .build();
        ComputeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getOperationDelayMs()).isZero();
        assertThat(copy.getRegions()).containsExactly("europe-west3");
    }
}
