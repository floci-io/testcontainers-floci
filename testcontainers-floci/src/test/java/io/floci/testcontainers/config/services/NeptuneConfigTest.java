package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class NeptuneConfigTest {

    @Test
    void shouldApplyDefaultNeptuneConfig() {
        NeptuneConfig config = NeptuneConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getProxyBasePort()).isEqualTo(8182);
        assertThat(config.getProxyPortsCount()).isEqualTo(10);
        assertThat(config.getProxyMaxPort()).isEqualTo(8191);
        assertThat(config.getDbType()).isEqualTo("gremlin");
        assertThat(config.getDefaultImage()).isEqualTo("tinkerpop/gremlin-server:3.7.3");
        assertThat(config.getDefaultNeo4jImage()).isEqualTo("neo4j:5-community");
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomNeptuneConfig() {
        NeptuneConfig config = NeptuneConfig.builder()
                .enabled(false)
                .proxyPortRange(9000, 51)
                .dbType("neo4j")
                .defaultImage("tinkerpop/gremlin-server:3.8.0")
                .defaultNeo4jImage("neo4j:5.24-community")
                .dockerNetwork("my-neptune-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getProxyBasePort()).isEqualTo(9000);
        assertThat(config.getProxyPortsCount()).isEqualTo(51);
        assertThat(config.getProxyMaxPort()).isEqualTo(9050);
        assertThat(config.getDbType()).isEqualTo("neo4j");
        assertThat(config.getDefaultImage()).isEqualTo("tinkerpop/gremlin-server:3.8.0");
        assertThat(config.getDefaultNeo4jImage()).isEqualTo("neo4j:5.24-community");
        assertThat(config.getDockerNetwork()).isEqualTo("my-neptune-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        NeptuneConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_NEPTUNE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_PROXY_BASE_PORT", "8182")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_PROXY_MAX_PORT", "8191")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DB_TYPE", "gremlin")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DEFAULT_IMAGE", "tinkerpop/gremlin-server:3.7.3")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DEFAULT_NEO4J_IMAGE", "neo4j:5-community")
                .doesNotContainKey("FLOCI_SERVICES_NEPTUNE_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        NeptuneConfig.builder()
                .enabled(true)
                .proxyPortRange(9000, 51)
                .dbType("neo4j")
                .defaultImage("tinkerpop/gremlin-server:3.8.0")
                .defaultNeo4jImage("neo4j:5.24-community")
                .dockerNetwork("my-neptune-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_NEPTUNE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_PROXY_BASE_PORT", "9000")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_PROXY_MAX_PORT", "9050")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DB_TYPE", "neo4j")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DEFAULT_IMAGE", "tinkerpop/gremlin-server:3.8.0")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DEFAULT_NEO4J_IMAGE", "neo4j:5.24-community")
                .containsEntry("FLOCI_SERVICES_NEPTUNE_DOCKER_NETWORK", "my-neptune-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        NeptuneConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_NEPTUNE_ENABLED", "false");
    }

    @Test
    void shouldNotExposeNeptunePortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withNeptuneConfig(c -> c.enabled(false).proxyPortRange(9000, 51));

            var env = container.getEnvMap();
            assertThat(env).containsEntry("FLOCI_SERVICES_NEPTUNE_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(9000);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        NeptuneConfig config = NeptuneConfig.builder()
                .enabled(false)
                .proxyPortRange(9182, 5)
                .dbType("opencypher")
                .defaultImage("test-image")
                .defaultNeo4jImage("test-neo4j")
                .dockerNetwork("test-network")
                .build();
        NeptuneConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getProxyBasePort()).isEqualTo(9182);
        assertThat(copy.getProxyPortsCount()).isEqualTo(5);
        assertThat(copy.getDbType()).isEqualTo("opencypher");
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getDefaultNeo4jImage()).isEqualTo("test-neo4j");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
    }

}
