package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IotConfigTest {

    @Test
    void shouldApplyDefaultIotConfig() {
        IotConfig config = IotConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMqttEnabled()).isTrue();
        assertThat(config.isMqttAutoStart()).isFalse();
        assertThat(config.getMqttHost()).isEqualTo("0.0.0.0");
        assertThat(config.getMqttPort()).isEqualTo(1883);
    }

    @Test
    void shouldApplyCustomIotConfig() {
        IotConfig config = IotConfig.builder()
                .enabled(false)
                .mqttEnabled(false)
                .mqttAutoStart(true)
                .mqttHost("127.0.0.1")
                .mqttPort(18830)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMqttEnabled()).isFalse();
        assertThat(config.isMqttAutoStart()).isTrue();
        assertThat(config.getMqttHost()).isEqualTo("127.0.0.1");
        assertThat(config.getMqttPort()).isEqualTo(18830);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IOT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_AUTO_START", "false")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_HOST", "0.0.0.0")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_PORT", "1883");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder()
                .mqttAutoStart(true)
                .mqttHost("127.0.0.1")
                .mqttPort(18830)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IOT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_AUTO_START", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_HOST", "127.0.0.1")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_PORT", "18830");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IOT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_AUTO_START")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_HOST")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_PORT");
    }

    @Test
    void shouldExposeMqttPortWhenEnabled() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().mqttPort(18830).build().applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).contains(18830);
    }

    @Test
    void shouldNotExposeMqttPortWhenMqttDisabled() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().mqttEnabled(false).build().applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).doesNotContain(1883);
    }

    @Test
    void shouldNotExposeMqttPortWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().enabled(false).build().applyExposedPortsToContainer(container);

        assertThat(container.getExposedPorts()).doesNotContain(1883);
    }
}
