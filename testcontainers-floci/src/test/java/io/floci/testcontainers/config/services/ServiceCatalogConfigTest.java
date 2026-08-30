package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceCatalogConfigTest {

    @Test
    void shouldApplyDefaultServiceCatalogConfig() {
        ServiceCatalogConfig config = ServiceCatalogConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomServiceCatalogConfig() {
        ServiceCatalogConfig config = ServiceCatalogConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceCatalogConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICECATALOG_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceCatalogConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICECATALOG_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceCatalogConfig config = ServiceCatalogConfig.builder()
                .enabled(false)
                .build();
        ServiceCatalogConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
