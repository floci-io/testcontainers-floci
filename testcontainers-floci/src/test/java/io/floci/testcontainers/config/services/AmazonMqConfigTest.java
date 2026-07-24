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
}
