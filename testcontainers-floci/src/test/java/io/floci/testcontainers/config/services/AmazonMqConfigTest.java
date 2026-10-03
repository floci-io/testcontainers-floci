package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AmazonMqConfigTest {

    @Test
    void shouldApplyDefaultAmazonMqConfig() {
        AmazonMqConfig config = AmazonMqConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("rabbitmq:3-management");
        assertThat(config.getAmqpHostPortBase()).isEqualTo(5672);
        assertThat(config.getAmqpHostPortsCount()).isEqualTo(10);
        assertThat(config.getAmqpHostPortMax()).isEqualTo(5681);
        assertThat(config.getConsoleHostPortBase()).isEqualTo(15672);
        assertThat(config.getConsoleHostPortsCount()).isEqualTo(10);
        assertThat(config.getConsoleHostPortMax()).isEqualTo(15681);
    }

    @Test
    void shouldApplyCustomAmazonMqConfig() {
        AmazonMqConfig config = AmazonMqConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("rabbitmq:4-management")
                .amqpHostPortRange(6000, 5)
                .consoleHostPortRange(16000, 3)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("rabbitmq:4-management");
        assertThat(config.getAmqpHostPortBase()).isEqualTo(6000);
        assertThat(config.getAmqpHostPortsCount()).isEqualTo(5);
        assertThat(config.getAmqpHostPortMax()).isEqualTo(6004);
        assertThat(config.getConsoleHostPortBase()).isEqualTo(16000);
        assertThat(config.getConsoleHostPortsCount()).isEqualTo(3);
        assertThat(config.getConsoleHostPortMax()).isEqualTo(16002);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", "rabbitmq:3-management")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", "5672")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX", "5681")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE", "15672")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX", "15681");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder()
                .mock(true)
                .defaultImage("rabbitmq:4-management")
                .amqpHostPortRange(6000, 5)
                .consoleHostPortRange(16000, 3)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", "rabbitmq:4-management")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", "6000")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX", "6004")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE", "16000")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX", "16002");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AmazonMqConfig config = AmazonMqConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("test-image")
                .amqpHostPortRange(6000, 5)
                .consoleHostPortRange(16000, 3)
                .build();
        AmazonMqConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getAmqpHostPortBase()).isEqualTo(6000);
        assertThat(copy.getAmqpHostPortsCount()).isEqualTo(5);
        assertThat(copy.getConsoleHostPortBase()).isEqualTo(16000);
        assertThat(copy.getConsoleHostPortsCount()).isEqualTo(3);
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(AmazonMqConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(AmazonMqConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(AmazonMqConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

}
