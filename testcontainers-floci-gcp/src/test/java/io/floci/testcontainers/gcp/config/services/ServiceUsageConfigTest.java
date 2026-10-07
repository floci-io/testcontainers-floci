package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceUsageConfigTest {

    @Test
    void shouldApplyDefaultServiceUsageConfig() {
        ServiceUsageConfig config = ServiceUsageConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomServiceUsageConfig() {
        ServiceUsageConfig config = ServiceUsageConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceUsageConfig config = ServiceUsageConfig.builder()
                .enabled(false)
                .build();
        ServiceUsageConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
