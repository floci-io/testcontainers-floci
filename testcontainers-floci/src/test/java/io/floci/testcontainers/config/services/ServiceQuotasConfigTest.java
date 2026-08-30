package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceQuotasConfigTest {

    @Test
    void shouldApplyDefaultServiceQuotasConfig() {
        ServiceQuotasConfig config = ServiceQuotasConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomServiceQuotasConfig() {
        ServiceQuotasConfig config = ServiceQuotasConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceQuotasConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceQuotasConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceQuotasConfig config = ServiceQuotasConfig.builder()
                .enabled(false)
                .build();
        ServiceQuotasConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
