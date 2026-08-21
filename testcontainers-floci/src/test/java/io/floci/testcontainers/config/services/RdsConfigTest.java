package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RdsConfigTest {

    @Test
    void shouldApplyDefaultRdsConfig() {
        RdsConfig config = RdsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getProxyBasePort()).isEqualTo(7000);
        assertThat(config.getProxyMaxPort()).isEqualTo(7009);
        assertThat(config.getProxyPortsCount()).isEqualTo(10);
        assertThat(config.getDefaultPostgresImage()).isNull();
        assertThat(config.getDefaultMysqlImage()).isNull();
        assertThat(config.getDefaultMariadbImage()).isNull();
        assertThat(config.getDockerNetwork()).isNull();
        assertThat(config.getEndpointHost()).isNull();
    }

    @Test
    void shouldApplyCustomRdsConfig() {
        RdsConfig config = RdsConfig.builder()
                .enabled(false)
                .mock(true)
                .proxyPortRange(8000, 100)
                .defaultPostgresImage("postgres:15")
                .defaultMysqlImage("mysql:9.0")
                .defaultMariadbImage("mariadb:10")
                .dockerNetwork("my-rds-network")
                .endpointHost("rds.example.com")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getProxyBasePort()).isEqualTo(8000);
        assertThat(config.getProxyMaxPort()).isEqualTo(8099);
        assertThat(config.getProxyPortsCount()).isEqualTo(100);
        assertThat(config.getDefaultPostgresImage()).isEqualTo("postgres:15");
        assertThat(config.getDefaultMysqlImage()).isEqualTo("mysql:9.0");
        assertThat(config.getDefaultMariadbImage()).isEqualTo("mariadb:10");
        assertThat(config.getDockerNetwork()).isEqualTo("my-rds-network");
        assertThat(config.getEndpointHost()).isEqualTo("rds.example.com");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_RDS_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_RDS_PROXY_BASE_PORT", "7000")
                .containsEntry("FLOCI_SERVICES_RDS_PROXY_MAX_PORT", "7009")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DEFAULT_POSTGRES_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DEFAULT_MYSQL_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DEFAULT_MARIADB_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_RDS_ENDPOINT_HOST");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsConfig.builder()
                .enabled(true)
                .mock(true)
                .proxyPortRange(8000, 100)
                .defaultPostgresImage("postgres:15")
                .defaultMysqlImage("mysql:9.0")
                .defaultMariadbImage("mariadb:10")
                .dockerNetwork("my-rds-network")
                .endpointHost("rds.example.com")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_RDS_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_RDS_PROXY_BASE_PORT", "8000")
                .containsEntry("FLOCI_SERVICES_RDS_PROXY_MAX_PORT", "8099")
                .containsEntry("FLOCI_SERVICES_RDS_DEFAULT_POSTGRES_IMAGE", "postgres:15")
                .containsEntry("FLOCI_SERVICES_RDS_DEFAULT_MYSQL_IMAGE", "mysql:9.0")
                .containsEntry("FLOCI_SERVICES_RDS_DEFAULT_MARIADB_IMAGE", "mariadb:10")
                .containsEntry("FLOCI_SERVICES_RDS_DOCKER_NETWORK", "my-rds-network")
                .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", "rds.example.com");
    }

    @Test
    void shouldNotExposeRdsPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRdsConfig(c -> c.enabled(false).proxyPortRange(8000, 100));

            var env = container.getEnvMap();
            assertThat(env).containsEntry("FLOCI_SERVICES_RDS_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(8000);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RdsConfig config = RdsConfig.builder()
                .enabled(false)
                .mock(true)
                .proxyPortRange(7100, 5)
                .defaultPostgresImage("test-postgres")
                .defaultMysqlImage("test-mysql")
                .defaultMariadbImage("test-mariadb")
                .dockerNetwork("test-network")
                .endpointHost("test-host")
                .build();
        RdsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getProxyBasePort()).isEqualTo(7100);
        assertThat(copy.getProxyPortsCount()).isEqualTo(5);
        assertThat(copy.getDefaultPostgresImage()).isEqualTo("test-postgres");
        assertThat(copy.getDefaultMysqlImage()).isEqualTo("test-mysql");
        assertThat(copy.getDefaultMariadbImage()).isEqualTo("test-mariadb");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getEndpointHost()).isEqualTo("test-host");
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(RdsConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(RdsConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(RdsConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

}
