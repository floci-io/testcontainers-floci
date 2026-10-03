package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ControlCatalogConfigTest {

    @Test
    void shouldApplyDefaultControlCatalogConfig() {
        ControlCatalogConfig config = ControlCatalogConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomControlCatalogConfig() {
        ControlCatalogConfig config = ControlCatalogConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlCatalogConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONTROLCATALOG_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ControlCatalogConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONTROLCATALOG_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ControlCatalogConfig config = ControlCatalogConfig.builder()
                .enabled(false)
                .build();
        ControlCatalogConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
