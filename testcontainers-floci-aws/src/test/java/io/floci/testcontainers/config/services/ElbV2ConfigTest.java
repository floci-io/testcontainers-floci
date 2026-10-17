package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ElbV2ConfigTest {

    @Test
    void shouldApplyDefaultElbV2Config() {
        ElbV2Config config = ElbV2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
    }

    @Test
    void shouldApplyCustomElbV2Config() {
        ElbV2Config config = ElbV2Config.builder()
                .enabled(false)
                .mock(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_MOCK", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder()
                .enabled(true)
                .mock(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_MOCK", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "false");
    }

    @Test
    void shouldExposeListenerPortsOnContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder()
                .listenerPort(8080)
                .listenerPort(8443)
                .build()
                .applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).contains(8080, 8443);
    }

    @Test
    void shouldNotExposeListenerPortsWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder()
                .enabled(false)
                .listenerPort(8080)
                .build()
                .applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).doesNotContain(8080);
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ElbV2Config config = ElbV2Config.builder()
                .enabled(false)
                .mock(true)
                .listenerPort(8080)
                .build();
        ElbV2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getListenerPorts()).contains(8080);
    }

}
