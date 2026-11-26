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
        assertThat(config.isBackendKeepAlive()).isTrue();
        assertThat(config.getBackendKeepAliveTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyCustomElbV2Config() {
        ElbV2Config config = ElbV2Config.builder()
                .enabled(false)
                .mock(true)
                .backendKeepAlive(false)
                .backendKeepAliveTimeoutSeconds(5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.isBackendKeepAlive()).isFalse();
        assertThat(config.getBackendKeepAliveTimeoutSeconds()).isEqualTo(5);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder()
                .enabled(true)
                .mock(true)
                .backendKeepAlive(false)
                .backendKeepAliveTimeoutSeconds(5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE", "false")
                .containsEntry("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbV2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ELBV2_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE")
                .doesNotContainKey("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS");
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
                .backendKeepAlive(false)
                .backendKeepAliveTimeoutSeconds(5)
                .build();
        ElbV2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getListenerPorts()).contains(8080);
        assertThat(copy.isBackendKeepAlive()).isFalse();
        assertThat(copy.getBackendKeepAliveTimeoutSeconds()).isEqualTo(5);
    }

}
