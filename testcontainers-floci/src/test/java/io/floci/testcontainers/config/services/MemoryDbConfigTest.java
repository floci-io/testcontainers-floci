package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MemoryDbConfigTest {

    @Test
    void shouldApplyDefaultMemoryDbConfig() {
        MemoryDbConfig config = MemoryDbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getProxyBasePort()).isEqualTo(6400);
        assertThat(config.getProxyMaxPort()).isEqualTo(6409);
        assertThat(config.getProxyPortsCount()).isEqualTo(10);
        assertThat(config.getDefaultImage()).isEqualTo("valkey/valkey:8");
        assertThat(config.getDockerNetwork()).isEmpty();
    }

    @Test
    void shouldApplyCustomMemoryDbConfig() {
        MemoryDbConfig config = MemoryDbConfig.builder()
                .enabled(false)
                .mock(true)
                .proxyPortRange(7000, 20)
                .defaultImage("valkey/valkey:9")
                .dockerNetwork("custom-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getProxyBasePort()).isEqualTo(7000);
        assertThat(config.getProxyMaxPort()).isEqualTo(7019);
        assertThat(config.getProxyPortsCount()).isEqualTo(20);
        assertThat(config.getDefaultImage()).isEqualTo("valkey/valkey:9");
        assertThat(config.getDockerNetwork()).contains("custom-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MemoryDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MEMORYDB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT", "6400")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_PROXY_MAX_PORT", "6409")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_DEFAULT_IMAGE", "valkey/valkey:8");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MemoryDbConfig.builder()
                .mock(true)
                .proxyPortRange(7000, 20)
                .defaultImage("valkey/valkey:9")
                .dockerNetwork("custom-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MEMORYDB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT", "7000")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_PROXY_MAX_PORT", "7019")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_DEFAULT_IMAGE", "valkey/valkey:9")
                .containsEntry("FLOCI_SERVICES_MEMORYDB_DOCKER_NETWORK", "custom-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MemoryDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MEMORYDB_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_MEMORYDB_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT")
                .doesNotContainKey("FLOCI_SERVICES_MEMORYDB_DOCKER_NETWORK");
    }

    @Test
    void shouldNotApplyDockerNetworkEnvVarWhenNotSet() {
        GenericContainer<?> container = genericContainer();
        MemoryDbConfig.builder().dockerNetwork(null).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_MEMORYDB_DOCKER_NETWORK");
    }

    @Test
    void shouldNotExposeMemoryDbPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withMemoryDbConfig(c -> c.enabled(false).proxyPortRange(8100, 20));

            var env = container.getEnvMap();
            assertThat(env).containsEntry("FLOCI_SERVICES_MEMORYDB_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(8100);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MemoryDbConfig config = MemoryDbConfig.builder()
                .enabled(false)
                .mock(true)
                .proxyPortRange(6500, 5)
                .defaultImage("test-image")
                .dockerNetwork("test-network")
                .build();
        MemoryDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getProxyBasePort()).isEqualTo(6500);
        assertThat(copy.getProxyPortsCount()).isEqualTo(5);
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getDockerNetwork()).contains("test-network");
    }

}
