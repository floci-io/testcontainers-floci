package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DsqlConfigTest {

    @Test
    void shouldApplyDefaultDsqlConfig() {
        DsqlConfig config = DsqlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getPort()).isEqualTo(5432);
        assertThat(config.getEndpointSuffix()).isEqualTo("localhost.floci.io");
        assertThat(config.getImage()).isEqualTo("postgres:16-alpine");
        assertThat(config.getDockerNetwork()).isEmpty();
    }

    @Test
    void shouldApplyCustomDsqlConfig() {
        DsqlConfig config = DsqlConfig.builder()
                .enabled(false)
                .mock(true)
                .port(15432)
                .endpointSuffix("dsql.example.test")
                .image("postgres:16")
                .dockerNetwork("my-dsql-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getPort()).isEqualTo(15432);
        assertThat(config.getEndpointSuffix()).isEqualTo("dsql.example.test");
        assertThat(config.getImage()).isEqualTo("postgres:16");
        assertThat(config.getDockerNetwork()).contains("my-dsql-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DsqlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DSQL_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_DSQL_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_DSQL_PORT", "5432")
                .containsEntry("FLOCI_SERVICES_DSQL_ENDPOINT_SUFFIX", "localhost.floci.io")
                .containsEntry("FLOCI_SERVICES_DSQL_IMAGE", "postgres:16-alpine")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DsqlConfig.builder()
                .mock(true)
                .port(15432)
                .endpointSuffix("dsql.example.test")
                .image("postgres:16")
                .dockerNetwork("my-dsql-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DSQL_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_DSQL_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_DSQL_PORT", "15432")
                .containsEntry("FLOCI_SERVICES_DSQL_ENDPOINT_SUFFIX", "dsql.example.test")
                .containsEntry("FLOCI_SERVICES_DSQL_IMAGE", "postgres:16")
                .containsEntry("FLOCI_SERVICES_DSQL_DOCKER_NETWORK", "my-dsql-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DsqlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DSQL_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_PORT")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_ENDPOINT_SUFFIX")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_DSQL_DOCKER_NETWORK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DsqlConfig config = DsqlConfig.builder()
                .enabled(false)
                .mock(true)
                .port(15432)
                .endpointSuffix("dsql.example.test")
                .image("postgres:16")
                .dockerNetwork("my-dsql-network")
                .build();
        DsqlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getPort()).isEqualTo(15432);
        assertThat(copy.getEndpointSuffix()).isEqualTo("dsql.example.test");
        assertThat(copy.getImage()).isEqualTo("postgres:16");
        assertThat(copy.getDockerNetwork()).contains("my-dsql-network");
    }

    @Test
    void shouldExposeDsqlPort() {
        GenericContainer<?> container = genericContainer();
        DsqlConfig.builder().port(15432).build().applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).contains(15432);
    }

    @Test
    void shouldNotExposeDsqlPortWhenDisabledOrMocked() {
        GenericContainer<?> disabled = genericContainer();
        DsqlConfig.builder().enabled(false).build().applyExposedPortsToContainer(disabled);
        GenericContainer<?> mocked = genericContainer();
        DsqlConfig.builder().mock(true).build().applyExposedPortsToContainer(mocked);

        assertThat(disabled.getExposedPorts()).doesNotContain(5432);
        assertThat(mocked.getExposedPorts()).doesNotContain(5432);
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(DsqlConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(DsqlConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(DsqlConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

}
