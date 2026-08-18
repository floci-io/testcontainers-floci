package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MwaaConfigTest {

    @Test
    void shouldApplyDefaultMwaaConfig() {
        MwaaConfig config = MwaaConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultPostgresImage()).isEqualTo("postgres:16-alpine");
        assertThat(config.getSupportedVersions()).containsExactly("2.10.5", "2.9.3", "2.8.4");
        assertThat(config.getDefaultVersion()).isEqualTo("2.10.5");
        assertThat(config.getProxyBasePort()).isEqualTo(8700);
        assertThat(config.getProxyMaxPort()).isEqualTo(8709);
        assertThat(config.getProxyPortsCount()).isEqualTo(10);
        assertThat(config.getDataPath()).isEqualTo("./data/mwaa");
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.getDagSyncIntervalSeconds()).isEqualTo(30);
        assertThat(config.isInstallRequirements()).isTrue();
    }

    @Test
    void shouldApplyCustomMwaaConfig() {
        MwaaConfig config = MwaaConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultPostgresImage("postgres:15")
                .supportedVersions(List.of("2.10.5"))
                .defaultVersion("2.10.5")
                .proxyPortRange(9000, 100)
                .dataPath("/tmp/mwaa")
                .dockerNetwork("my-mwaa-network")
                .dagSyncIntervalSeconds(10)
                .installRequirements(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultPostgresImage()).isEqualTo("postgres:15");
        assertThat(config.getSupportedVersions()).containsExactly("2.10.5");
        assertThat(config.getDefaultVersion()).isEqualTo("2.10.5");
        assertThat(config.getProxyBasePort()).isEqualTo(9000);
        assertThat(config.getProxyMaxPort()).isEqualTo(9099);
        assertThat(config.getProxyPortsCount()).isEqualTo(100);
        assertThat(config.getDataPath()).isEqualTo("/tmp/mwaa");
        assertThat(config.getDockerNetwork()).contains("my-mwaa-network");
        assertThat(config.getDagSyncIntervalSeconds()).isEqualTo(10);
        assertThat(config.isInstallRequirements()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MwaaConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MWAA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_MWAA_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_MWAA_DEFAULT_POSTGRES_IMAGE", "postgres:16-alpine")
                .containsEntry("FLOCI_SERVICES_MWAA_SUPPORTED_VERSIONS", "2.10.5,2.9.3,2.8.4")
                .containsEntry("FLOCI_SERVICES_MWAA_DEFAULT_VERSION", "2.10.5")
                .containsEntry("FLOCI_SERVICES_MWAA_PROXY_BASE_PORT", "8700")
                .containsEntry("FLOCI_SERVICES_MWAA_PROXY_MAX_PORT", "8709")
                .containsEntry("FLOCI_SERVICES_MWAA_DATA_PATH", "./data/mwaa")
                .containsEntry("FLOCI_SERVICES_MWAA_DAG_SYNC_INTERVAL_SECONDS", "30")
                .containsEntry("FLOCI_SERVICES_MWAA_INSTALL_REQUIREMENTS", "true")
                .doesNotContainKey("FLOCI_SERVICES_MWAA_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MwaaConfig.builder()
                .mock(true)
                .dockerNetwork("my-mwaa-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MWAA_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_MWAA_DOCKER_NETWORK", "my-mwaa-network");
    }

    @Test
    void shouldNotApplyServiceEnvVarsWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        MwaaConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MWAA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_MWAA_MOCK");
    }

    @Test
    void shouldApplyExposedPorts() {
        GenericContainer<?> container = genericContainer();
        MwaaConfig.builder()
                .proxyPortRange(9000, 3)
                .build()
                .applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).contains(9000, 9001, 9002);
    }

    @Test
    void shouldNotApplyExposedPortsWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        MwaaConfig.builder().enabled(false).build().applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).doesNotContain(8700);
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MwaaConfig config = MwaaConfig.builder()
                .mock(true)
                .dockerNetwork("my-mwaa-network")
                .build();

        MwaaConfig copy = config.toBuilder().build();

        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDockerNetwork()).contains("my-mwaa-network");
    }

}
