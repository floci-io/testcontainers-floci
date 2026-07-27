package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MskConfigTest {

    @Test
    void shouldApplyDefaultMskConfig() {
        MskConfig config = MskConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("redpandadata/redpanda:latest");
        assertThat(config.getKafkaHostPortBase()).isEqualTo(9300);
        assertThat(config.getKafkaHostPortMax()).isEqualTo(9309);
        assertThat(config.getKafkaHostPortsCount()).isEqualTo(10);
    }

    @Test
    void shouldApplyCustomMskConfig() {
        MskConfig config = MskConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("redpandadata/redpanda:v24")
                .kafkaHostPortRange(9500, 20)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("redpandadata/redpanda:v24");
        assertThat(config.getKafkaHostPortBase()).isEqualTo(9500);
        assertThat(config.getKafkaHostPortMax()).isEqualTo(9519);
        assertThat(config.getKafkaHostPortsCount()).isEqualTo(20);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MskConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MSK_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_MSK_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_MSK_DEFAULT_IMAGE", "redpandadata/redpanda:latest")
                .containsEntry("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_BASE", "9300")
                .containsEntry("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_MAX", "9309");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MskConfig.builder()
                .enabled(true)
                .mock(true)
                .defaultImage("redpandadata/redpanda:v24")
                .kafkaHostPortRange(9500, 20)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MSK_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_MSK_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_MSK_DEFAULT_IMAGE", "redpandadata/redpanda:v24")
                .containsEntry("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_BASE", "9500")
                .containsEntry("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_MAX", "9519");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MskConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_MSK_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_MSK_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_MSK_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_BASE")
                .doesNotContainKey("FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_MAX");
    }

    @Test
    void shouldNotExposeMskPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withMskConfig(c -> c.enabled(false).kafkaHostPortRange(9500, 20));

            var env = container.getEnvMap();
            assertThat(env).containsEntry("FLOCI_SERVICES_MSK_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(9500);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MskConfig config = MskConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("test-image")
                .kafkaHostPortRange(9400, 5)
                .build();
        MskConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getKafkaHostPortBase()).isEqualTo(9400);
        assertThat(copy.getKafkaHostPortsCount()).isEqualTo(5);
    }

}
