package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DeploymentsConfigTest {

    @Test
    void shouldApplyDefaultDeploymentsConfig() {
        DeploymentsConfig config = DeploymentsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomDeploymentsConfig() {
        DeploymentsConfig config = DeploymentsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DeploymentsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_DEPLOYMENTS_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DeploymentsConfig.builder()
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_DEPLOYMENTS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DeploymentsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_DEPLOYMENTS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DeploymentsConfig config = DeploymentsConfig.builder()
                .enabled(false)
                .build();
        DeploymentsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
