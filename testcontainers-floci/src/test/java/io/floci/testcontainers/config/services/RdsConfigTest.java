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
                .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", container.getHost());
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
    void shouldDefaultEndpointHostToContainerHost() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", container.getHost());
        }
    }

    @Test
    void shouldPreserveConfiguredEndpointHostWhenDisablingAllServices() {
        try (FlociContainer container = new FlociContainer()
                .withRdsConfig(c -> c.endpointHost("rds.example.com"))) {
            container.disableAllServices();

            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", "rds.example.com");
        }
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

    @Test
    void shouldApplyDefaultSqlServerImage() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.getDefaultSqlServerImage()).isEqualTo("mcr.microsoft.com/mssql/server:2022-latest");

        RdsConfig config = RdsConfig.builder().defaultSqlServerImage("mcr.microsoft.com/mssql/server:2019-latest").build();
        assertThat(config.getDefaultSqlServerImage()).isEqualTo("mcr.microsoft.com/mssql/server:2019-latest");
        assertThat(config.toBuilder().build().getDefaultSqlServerImage()).isEqualTo("mcr.microsoft.com/mssql/server:2019-latest");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_DEFAULT_SQL_SERVER_IMAGE", "mcr.microsoft.com/mssql/server:2019-latest");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_DEFAULT_SQL_SERVER_IMAGE", "mcr.microsoft.com/mssql/server:2022-latest");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_DEFAULT_SQL_SERVER_IMAGE");
    }

    @Test
    void shouldApplyIamTokenEndpointBinding() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.isIamTokenEndpointBinding()).isEqualTo(true);

        RdsConfig config = RdsConfig.builder().iamTokenEndpointBinding(false).build();
        assertThat(config.isIamTokenEndpointBinding()).isEqualTo(false);
        assertThat(config.toBuilder().build().isIamTokenEndpointBinding()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_IAM_TOKEN_ENDPOINT_BINDING", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_IAM_TOKEN_ENDPOINT_BINDING", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_IAM_TOKEN_ENDPOINT_BINDING");
    }

    @Test
    void shouldApplyProxyHandshakeTimeoutMillis() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.getProxyHandshakeTimeoutMillis()).isEqualTo(10000);

        RdsConfig config = RdsConfig.builder().proxyHandshakeTimeoutMillis(2000).build();
        assertThat(config.getProxyHandshakeTimeoutMillis()).isEqualTo(2000);
        assertThat(config.toBuilder().build().getProxyHandshakeTimeoutMillis()).isEqualTo(2000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "2000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "10000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_PROXY_HANDSHAKE_TIMEOUT_MILLIS");
    }

    @Test
    void shouldApplyProxyBackendConnectTimeoutMillis() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.getProxyBackendConnectTimeoutMillis()).isEqualTo(5000);

        RdsConfig config = RdsConfig.builder().proxyBackendConnectTimeoutMillis(1000).build();
        assertThat(config.getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);
        assertThat(config.toBuilder().build().getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "1000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "5000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS");
    }

    @Test
    void shouldApplyProxyMaxConnections() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.getProxyMaxConnections()).isEqualTo(100);

        RdsConfig config = RdsConfig.builder().proxyMaxConnections(20).build();
        assertThat(config.getProxyMaxConnections()).isEqualTo(20);
        assertThat(config.toBuilder().build().getProxyMaxConnections()).isEqualTo(20);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_MAX_CONNECTIONS", "20");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_PROXY_MAX_CONNECTIONS", "100");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_PROXY_MAX_CONNECTIONS");
    }

    @Test
    void shouldApplyAuroraAutoPauseEnabled() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.isAuroraAutoPauseEnabled()).isEqualTo(true);

        RdsConfig config = RdsConfig.builder().auroraAutoPauseEnabled(false).build();
        assertThat(config.isAuroraAutoPauseEnabled()).isEqualTo(false);
        assertThat(config.toBuilder().build().isAuroraAutoPauseEnabled()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_AURORA_AUTO_PAUSE_ENABLED", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_AURORA_AUTO_PAUSE_ENABLED", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_AURORA_AUTO_PAUSE_ENABLED");
    }

    @Test
    void shouldApplyAuroraResumeDelayMillis() {
        RdsConfig defaults = RdsConfig.builder().build();
        assertThat(defaults.getAuroraResumeDelayMillis()).isEqualTo(0);

        RdsConfig config = RdsConfig.builder().auroraResumeDelayMillis(15000).build();
        assertThat(config.getAuroraResumeDelayMillis()).isEqualTo(15000);
        assertThat(config.toBuilder().build().getAuroraResumeDelayMillis()).isEqualTo(15000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_AURORA_RESUME_DELAY_MILLIS", "15000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_RDS_AURORA_RESUME_DELAY_MILLIS", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_RDS_AURORA_RESUME_DELAY_MILLIS");
    }

}
