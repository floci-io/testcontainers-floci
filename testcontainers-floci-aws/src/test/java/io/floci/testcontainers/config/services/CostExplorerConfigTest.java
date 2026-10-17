package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CostExplorerConfigTest {

    @Test
    void shouldApplyDefaultCostExplorerConfig() {
        CostExplorerConfig config = CostExplorerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getCreditUsdMonthly()).isEqualTo(0.0);
    }

    @Test
    void shouldApplyCustomCostExplorerConfig() {
        CostExplorerConfig config = CostExplorerConfig.builder()
                .enabled(false)
                .creditUsdMonthly(500.0)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getCreditUsdMonthly()).isEqualTo(500.0);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CostExplorerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CE_CREDIT_USD_MONTHLY", "0.0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CostExplorerConfig.builder()
                .enabled(true)
                .creditUsdMonthly(250.5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CE_CREDIT_USD_MONTHLY", "250.5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CostExplorerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CostExplorerConfig config = CostExplorerConfig.builder()
                .enabled(false)
                .creditUsdMonthly(9.99)
                .build();
        CostExplorerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getCreditUsdMonthly()).isEqualTo(9.99);
    }

}
