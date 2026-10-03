package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
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
        assertThat(config.isRuleSqlStrict()).isFalse();
        assertThat(config.getEndpointAddress()).isEmpty();
        assertThat(config.getMqttTlsPort()).isEqualTo(8883);
    }

    @Test
    void shouldApplyCustomIotConfig() {
        IotConfig config = IotConfig.builder()
                .enabled(false)
                .mqttEnabled(false)
                .mqttAutoStart(true)
                .mqttHost("127.0.0.1")
                .mqttPort(18830)
                .ruleSqlStrict(true)
                .endpointAddress("iot.example.com")
                .mqttTlsPort(8884)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMqttEnabled()).isFalse();
        assertThat(config.isMqttAutoStart()).isTrue();
        assertThat(config.getMqttHost()).isEqualTo("127.0.0.1");
        assertThat(config.getMqttPort()).isEqualTo(18830);
        assertThat(config.isRuleSqlStrict()).isTrue();
        assertThat(config.getEndpointAddress()).contains("iot.example.com");
        assertThat(config.getMqttTlsPort()).isEqualTo(8884);
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
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_PORT", "1883")
                .containsEntry("FLOCI_SERVICES_IOT_RULE_SQL_STRICT", "false")
                .doesNotContainKey("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        // The MQTT over TLS port is only emitted while TLS is enabled
        FlociContainer container = flociContainerWithoutIot(true);
        IotConfig.builder()
                .mqttAutoStart(true)
                .mqttHost("127.0.0.1")
                .mqttPort(18830)
                .ruleSqlStrict(true)
                .endpointAddress("iot.example.com")
                .mqttTlsPort(8884)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IOT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_AUTO_START", "true")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_HOST", "127.0.0.1")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_PORT", "18830")
                .containsEntry("FLOCI_SERVICES_IOT_RULE_SQL_STRICT", "true")
                .containsEntry("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS", "iot.example.com")
                .containsEntry("FLOCI_SERVICES_IOT_MQTT_TLS_PORT", "8884");
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
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_PORT")
                .doesNotContainKey("FLOCI_SERVICES_IOT_RULE_SQL_STRICT")
                .doesNotContainKey("FLOCI_SERVICES_IOT_ENDPOINT_ADDRESS")
                .doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");
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
                .ruleSqlStrict(true)
                .endpointAddress("iot.example.com")
                .mqttTlsPort(8884)
                .build();
        IotConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMqttEnabled()).isFalse();
        assertThat(copy.isMqttAutoStart()).isTrue();
        assertThat(copy.getMqttHost()).isEqualTo("127.0.0.1");
        assertThat(copy.getMqttPort()).isEqualTo(1884);
        assertThat(copy.isRuleSqlStrict()).isTrue();
        assertThat(copy.getEndpointAddress()).contains("iot.example.com");
        assertThat(copy.getMqttTlsPort()).isEqualTo(8884);
    }

    @Test
    void shouldExposeMqttTlsPortOnlyWhenTlsEnabled() {
        IotConfig config = IotConfig.builder().build();

        FlociContainer tlsContainer = applyToContainer(config, flociContainerWithoutIot(true));
        assertThat(tlsContainer.getExposedPorts()).contains(1883, 8883);
        assertThat(tlsContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_MQTT_TLS_PORT", "8883");

        FlociContainer noTlsContainer = applyToContainer(config, flociContainerWithoutIot(false));
        assertThat(noTlsContainer.getExposedPorts()).contains(1883).doesNotContain(8883);
        assertThat(noTlsContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");

        GenericContainer<?> genericContainer = applyToContainer(config, genericContainer());
        assertThat(genericContainer.getExposedPorts()).contains(1883).doesNotContain(8883);
        assertThat(genericContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");

        FlociContainer disabledTlsPortContainer =
                applyToContainer(IotConfig.builder().mqttTlsPort(0).build(), flociContainerWithoutIot(true));
        assertThat(disabledTlsPortContainer.getExposedPorts()).contains(1883).doesNotContain(0, 8883);
    }

    private static <T extends GenericContainer<?>> T applyToContainer(IotConfig config, T container) {
        config.applyExposedPortsToContainer(container);
        config.applyEnvVarsToContainer(container);
        return container;
    }

    /**
     * Creates a {@link FlociContainer} whose own IoT config is disabled, so that only the ports of the config
     * under test are exposed.
     */
    private static FlociContainer flociContainerWithoutIot(boolean tlsEnabled) {
        return new FlociContainer()
                .withIotConfig(iot -> iot.enabled(false))
                .withTlsConfig(tls -> tls.enabled(tlsEnabled));
    }

    @Test
    void shouldExposeMqttTlsPortWhenTlsEnabledAfterIotConfig() {
        FlociContainer container = new FlociContainer().withIotConfig(iot -> iot.mqttTlsPort(8884));
        assertThat(container.getExposedPorts()).doesNotContain(8884);
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IOT_MQTT_TLS_PORT");

        container.withTlsConfig(tls -> tls.enabled(true));
        assertThat(container.getExposedPorts()).contains(8884);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOT_MQTT_TLS_PORT", "8884");

        container.withTlsConfig(tls -> tls.enabled(false));
        assertThat(container.getExposedPorts()).doesNotContain(8884);
    }

}
