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
        assertThat(config.getStandardOnDemandVcpus()).isEqualTo(5);
    }

    @Test
    void shouldApplyCustomServiceQuotasConfig() {
        ServiceQuotasConfig config = ServiceQuotasConfig.builder()
                .enabled(false)
                .standardOnDemandVcpus(64)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getStandardOnDemandVcpus()).isEqualTo(64);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceQuotasConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_STANDARD_ON_DEMAND_VCPUS", "5");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceQuotasConfig.builder()
                .standardOnDemandVcpus(64)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_STANDARD_ON_DEMAND_VCPUS", "64");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceQuotasConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SERVICEQUOTAS_STANDARD_ON_DEMAND_VCPUS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceQuotasConfig config = ServiceQuotasConfig.builder()
                .enabled(false)
                .standardOnDemandVcpus(64)
                .build();
        ServiceQuotasConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getStandardOnDemandVcpus()).isEqualTo(64);
    }
}
