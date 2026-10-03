package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PipesConfigTest {

    @Test
    void shouldApplyDefaultPipesConfig() {
        PipesConfig config = PipesConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:latest");
        assertThat(config.getKafkaRestBridgeHostPortBase()).isEqualTo(9500);
        assertThat(config.getKafkaRestBridgeHostPortsCount()).isEqualTo(10);
        assertThat(config.getKafkaRestBridgeHostPortMax()).isEqualTo(9509);
    }

    @Test
    void shouldApplyCustomPipesConfig() {
        PipesConfig config = PipesConfig.builder()
                .enabled(false)
                .kafkaRestBridgeDefaultImage("ghcr.io/aiven-open/karapace:5.0.0")
                .kafkaRestBridgeHostPortRange(9700, 20)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:5.0.0");
        assertThat(config.getKafkaRestBridgeHostPortBase()).isEqualTo(9700);
        assertThat(config.getKafkaRestBridgeHostPortsCount()).isEqualTo(20);
        assertThat(config.getKafkaRestBridgeHostPortMax()).isEqualTo(9719);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PipesConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_PIPES_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE", "ghcr.io/aiven-open/karapace:latest")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE", "9500")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX", "9509");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PipesConfig.builder()
                .kafkaRestBridgeDefaultImage("ghcr.io/aiven-open/karapace:5.0.0")
                .kafkaRestBridgeHostPortRange(9700, 20)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE", "ghcr.io/aiven-open/karapace:5.0.0")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE", "9700")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX", "9719");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PipesConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_PIPES_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE")
                .doesNotContainKey("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PipesConfig config = PipesConfig.builder()
                .enabled(false)
                .kafkaRestBridgeDefaultImage("ghcr.io/aiven-open/karapace:5.0.0")
                .kafkaRestBridgeHostPortRange(9700, 20)
                .build();
        PipesConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:5.0.0");
        assertThat(copy.getKafkaRestBridgeHostPortBase()).isEqualTo(9700);
        assertThat(copy.getKafkaRestBridgeHostPortsCount()).isEqualTo(20);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(PipesConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(PipesConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
