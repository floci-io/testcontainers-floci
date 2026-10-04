package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudSqlConfigTest {

    @Test
    void shouldApplyDefaultCloudSqlConfig() {
        CloudSqlConfig config = CloudSqlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getPostgres15Image()).isEqualTo("postgres:15.18-alpine");
        assertThat(config.getPostgres16Image()).isEqualTo("postgres:16.14-alpine");
        assertThat(config.getPostgres17Image()).isEqualTo("postgres:17.10-alpine");
        assertThat(config.getPostgres18Image()).isEqualTo("postgres:18.4-alpine");
        assertThat(config.getMysql80Image()).isEqualTo("mysql:8.0.46");
        assertThat(config.getMysql84Image()).isEqualTo("mysql:8.4.11");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(90);
    }

    @Test
    void shouldApplyCustomCloudSqlConfig() {
        CloudSqlConfig config = CloudSqlConfig.builder()
                .enabled(false)
                .mock(true)
                .postgres15Image("postgres:15-alpine")
                .postgres16Image("postgres:16-alpine")
                .postgres17Image("postgres:17-alpine")
                .postgres18Image("postgres:18-alpine")
                .mysql80Image("mysql:8.0")
                .mysql84Image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getPostgres15Image()).isEqualTo("postgres:15-alpine");
        assertThat(config.getPostgres16Image()).isEqualTo("postgres:16-alpine");
        assertThat(config.getPostgres17Image()).isEqualTo("postgres:17-alpine");
        assertThat(config.getPostgres18Image()).isEqualTo("postgres:18-alpine");
        assertThat(config.getMysql80Image()).isEqualTo("mysql:8.0");
        assertThat(config.getMysql84Image()).isEqualTo("mysql:8.4");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudSqlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES15_IMAGE", "postgres:15.18-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES16_IMAGE", "postgres:16.14-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES17_IMAGE", "postgres:17.10-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES18_IMAGE", "postgres:18.4-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL80_IMAGE", "mysql:8.0.46")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL84_IMAGE", "mysql:8.4.11")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_STARTUP_TIMEOUT_SECONDS", "90");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudSqlConfig.builder()
                .mock(true)
                .postgres15Image("postgres:15-alpine")
                .postgres16Image("postgres:16-alpine")
                .postgres17Image("postgres:17-alpine")
                .postgres18Image("postgres:18-alpine")
                .mysql80Image("mysql:8.0")
                .mysql84Image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES15_IMAGE", "postgres:15-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES16_IMAGE", "postgres:16-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES17_IMAGE", "postgres:17-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES18_IMAGE", "postgres:18-alpine")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL80_IMAGE", "mysql:8.0")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL84_IMAGE", "mysql:8.4")
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_STARTUP_TIMEOUT_SECONDS", "120");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudSqlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_CLOUDSQL_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES15_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES16_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES17_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES18_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL80_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL84_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_CLOUDSQL_STARTUP_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudSqlConfig config = CloudSqlConfig.builder()
                .enabled(false)
                .mock(true)
                .postgres15Image("postgres:15-alpine")
                .postgres16Image("postgres:16-alpine")
                .postgres17Image("postgres:17-alpine")
                .postgres18Image("postgres:18-alpine")
                .mysql80Image("mysql:8.0")
                .mysql84Image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .build();
        CloudSqlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getPostgres15Image()).isEqualTo("postgres:15-alpine");
        assertThat(copy.getPostgres16Image()).isEqualTo("postgres:16-alpine");
        assertThat(copy.getPostgres17Image()).isEqualTo("postgres:17-alpine");
        assertThat(copy.getPostgres18Image()).isEqualTo("postgres:18-alpine");
        assertThat(copy.getMysql80Image()).isEqualTo("mysql:8.0");
        assertThat(copy.getMysql84Image()).isEqualTo("mysql:8.4");
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(120);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(CloudSqlConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(CloudSqlConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(CloudSqlConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
