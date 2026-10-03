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
    }

    @Test
    void shouldApplyCustomAmazonMqConfig() {
        AmazonMqConfig config = AmazonMqConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("rabbitmq:4-management")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("rabbitmq:4-management");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", "rabbitmq:3-management");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder()
                .mock(true)
                .defaultImage("rabbitmq:4-management")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE", "rabbitmq:4-management");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AmazonMqConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_AMAZONMQ_DEFAULT_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AmazonMqConfig config = AmazonMqConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("test-image")
                .build();
        AmazonMqConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(AmazonMqConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(AmazonMqConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(AmazonMqConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldApplyHostPortRanges() {
        AmazonMqConfig defaults = AmazonMqConfig.builder().build();
        assertThat(defaults.getAmqpHostPortBase()).isEqualTo(5672);
        assertThat(defaults.getAmqpHostPortsCount()).isEqualTo(28);
        assertThat(defaults.getAmqpHostPortMax()).isEqualTo(5699);
        assertThat(defaults.getConsoleHostPortBase()).isEqualTo(15672);
        assertThat(defaults.getConsoleHostPortsCount()).isEqualTo(28);
        assertThat(defaults.getConsoleHostPortMax()).isEqualTo(15699);

        AmazonMqConfig config = AmazonMqConfig.builder()
                .amqpHostPortRange(6000, 5)
                .consoleHostPortRange(16000, 3)
                .build();
        AmazonMqConfig copy = config.toBuilder().build();
        assertThat(copy.getAmqpHostPortBase()).isEqualTo(6000);
        assertThat(copy.getAmqpHostPortMax()).isEqualTo(6004);
        assertThat(copy.getConsoleHostPortBase()).isEqualTo(16000);
        assertThat(copy.getConsoleHostPortMax()).isEqualTo(16002);

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", "5672")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX", "5699")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE", "15672")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX", "15699");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", "6000")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_MAX", "6004")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE", "16000")
                .containsEntry("FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_MAX", "16002");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap())
                .doesNotContainKeys("FLOCI_SERVICES_AMAZONMQ_AMQP_HOST_PORT_BASE", "FLOCI_SERVICES_AMAZONMQ_CONSOLE_HOST_PORT_BASE");
    }

}
