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

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IotConfig config = IotConfig.builder()
                .enabled(false)
                .mqttEnabled(false)
                .mqttAutoStart(true)
                .mqttHost("127.0.0.1")
                .mqttPort(1884)
                .build();
        IotConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMqttEnabled()).isFalse();
        assertThat(copy.isMqttAutoStart()).isTrue();
        assertThat(copy.getMqttHost()).isEqualTo("127.0.0.1");
        assertThat(copy.getMqttPort()).isEqualTo(1884);
    }

    @Test
    void shouldApplyRuleSqlStrict() {
        IotConfig defaults = IotConfig.builder().build();
        assertThat(defaults.isRuleSqlStrict()).isEqualTo(false);

        IotConfig config = IotConfig.builder().ruleSqlStrict(true).build();
        assertThat(config.isRuleSqlStrict()).isEqualTo(true);
        assertThat(config.toBuilder().build().isRuleSqlStrict()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_RULE_SQL_STRICT", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_RULE_SQL_STRICT", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_RULE_SQL_STRICT");
    }

    @Test
    void shouldApplyEndpointAddress() {
        IotConfig defaults = IotConfig.builder().build();
        assertThat(defaults.getEndpointAddress()).isEmpty();

        IotConfig config = IotConfig.builder().endpointAddress("iot.example.com").build();
        assertThat(config.getEndpointAddress()).contains("iot.example.com");
        assertThat(config.toBuilder().build().getEndpointAddress()).contains("iot.example.com");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS", "iot.example.com");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS");
    }

    @Test
    void shouldApplyMqttTlsPort() {
        IotConfig defaults = IotConfig.builder().build();
        assertThat(defaults.getMqttTlsPort()).isEqualTo(8883);

        IotConfig config = IotConfig.builder().mqttTlsPort(8884).build();
        assertThat(config.getMqttTlsPort()).isEqualTo(8884);
        assertThat(config.toBuilder().build().getMqttTlsPort()).isEqualTo(8884);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_MQTT_TLS_PORT", "8884");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_MQTT_TLS_PORT", "8883");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");
    }

    @Test
    void shouldExposeMqttTlsPort() {
        GenericContainer<?> container = genericContainer();
        IotConfig.builder().build().applyExposedPortsToContainer(container);
        assertThat(container.getExposedPorts()).contains(1883, 8883);

        GenericContainer<?> disabledTlsContainer = genericContainer();
        IotConfig.builder().mqttTlsPort(0).build().applyExposedPortsToContainer(disabledTlsContainer);
        assertThat(disabledTlsContainer.getExposedPorts()).contains(1883).doesNotContain(0, 8883);
    }

}
