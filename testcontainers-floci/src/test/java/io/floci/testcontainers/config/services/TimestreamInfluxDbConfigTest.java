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
    }

    @Test
    void shouldApplyCustomTimestreamInfluxDbConfig() {
        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TimestreamInfluxDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TimestreamInfluxDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder()
                .enabled(false)
                .build();
        TimestreamInfluxDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyMock() {
        TimestreamInfluxDbConfig defaults = TimestreamInfluxDbConfig.builder().build();
        assertThat(defaults.isMock()).isEqualTo(false);

        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().mock(true).build();
        assertThat(config.isMock()).isEqualTo(true);
        assertThat(config.toBuilder().build().isMock()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_MOCK");
    }

    @Test
    void shouldApplyDefaultImage() {
        TimestreamInfluxDbConfig defaults = TimestreamInfluxDbConfig.builder().build();
        assertThat(defaults.getDefaultImage()).isEqualTo("influxdb:2.7");

        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().defaultImage("influxdb:2.8").build();
        assertThat(config.getDefaultImage()).isEqualTo("influxdb:2.8");
        assertThat(config.toBuilder().build().getDefaultImage()).isEqualTo("influxdb:2.8");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE", "influxdb:2.8");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE", "influxdb:2.7");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DEFAULT_IMAGE");
    }

    @Test
    void shouldApplyReadinessTimeoutSeconds() {
        TimestreamInfluxDbConfig defaults = TimestreamInfluxDbConfig.builder().build();
        assertThat(defaults.getReadinessTimeoutSeconds()).isEqualTo(120);

        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().readinessTimeoutSeconds(60).build();
        assertThat(config.getReadinessTimeoutSeconds()).isEqualTo(60);
        assertThat(config.toBuilder().build().getReadinessTimeoutSeconds()).isEqualTo(60);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS", "60");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS", "120");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_READINESS_TIMEOUT_SECONDS");
    }

    @Test
    void shouldApplyDockerNetwork() {
        TimestreamInfluxDbConfig defaults = TimestreamInfluxDbConfig.builder().build();
        assertThat(defaults.getDockerNetwork()).isEmpty();

        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().dockerNetwork("my-network").build();
        assertThat(config.getDockerNetwork()).contains("my-network");
        assertThat(config.toBuilder().build().getDockerNetwork()).contains("my-network");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK", "my-network");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyHostPortRange() {
        TimestreamInfluxDbConfig defaults = TimestreamInfluxDbConfig.builder().build();
        assertThat(defaults.getHostPortBase()).isEqualTo(8086);
        assertThat(defaults.getHostPortsCount()).isEqualTo(100);
        assertThat(defaults.getHostPortMax()).isEqualTo(8185);

        TimestreamInfluxDbConfig config = TimestreamInfluxDbConfig.builder().hostPortRange(9000, 5).build();
        TimestreamInfluxDbConfig copy = config.toBuilder().build();
        assertThat(copy.getHostPortBase()).isEqualTo(9000);
        assertThat(copy.getHostPortMax()).isEqualTo(9004);

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap())
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE", "8086")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX", "8185");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE", "9000")
                .containsEntry("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_MAX", "9004");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_TIMESTREAM_INFLUXDB_HOST_PORT_BASE");
    }

    @Test
    void shouldRequireDockerSocketUnlessMocked() {
        assertThat(TimestreamInfluxDbConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(TimestreamInfluxDbConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(TimestreamInfluxDbConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
