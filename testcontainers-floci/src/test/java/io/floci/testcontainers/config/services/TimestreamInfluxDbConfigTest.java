package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TimestreamInfluxDbConfigTest {

    @Test
    void shouldApplyDefaultTimestreamInfluxDbConfig() {
        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("influxdb:2.7");
        assertThat(config.getReadinessTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.getHostPortBase()).isEqualTo(8086);
        assertThat(config.getHostPortsCount()).isEqualTo(10);
        assertThat(config.getHostPortMax()).isEqualTo(8095);
    }

    @Test
    void shouldApplyCustomTimestreamInfluxDbConfig() {
        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("influxdb:2.8")
                .readinessTimeoutSeconds(60)
                .dockerNetwork("my-network")
                .hostPortRange(9000, 5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("influxdb:2.8");
        assertThat(config.getReadinessTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDockerNetwork()).contains("my-network");
        assertThat(config.getHostPortBase()).isEqualTo(9000);
        assertThat(config.getHostPortsCount()).isEqualTo(5);
        assertThat(config.getHostPortMax()).isEqualTo(9004);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TimestreamInfluxDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE", "influxdb:2.7")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS", "120")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE", "8086")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX", "8095");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TimestreamInfluxDbConfig.builder()
                .mock(true)
                .defaultImage("influxdb:2.8")
                .readinessTimeoutSeconds(60)
                .dockerNetwork("my-network")
                .hostPortRange(9000, 5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE", "influxdb:2.8")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK", "my-network")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE", "9000")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX", "9004");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TimestreamInfluxDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE")
                .doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("influxdb:2.8")
                .readinessTimeoutSeconds(60)
                .dockerNetwork("my-network")
                .hostPortRange(9000, 5)
                .build();
        TimestreamInfluxDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("influxdb:2.8");
        assertThat(copy.getReadinessTimeoutSeconds()).isEqualTo(60);
        assertThat(copy.getDockerNetwork()).contains("my-network");
        assertThat(copy.getHostPortBase()).isEqualTo(9000);
        assertThat(copy.getHostPortsCount()).isEqualTo(5);
    }

    @Test
    void shouldRequireDockerSocketUnlessMocked() {
        assertThat(TimestreamInfluxDbConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(TimestreamInfluxDbConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(TimestreamInfluxDbConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
