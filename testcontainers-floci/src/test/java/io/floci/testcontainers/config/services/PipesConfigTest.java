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
    }

    @Test
    void shouldApplyCustomPipesConfig() {
        PipesConfig config = PipesConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PipesConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PIPES_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PipesConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PIPES_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PipesConfig config = PipesConfig.builder()
                .enabled(false)
                .build();
        PipesConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyKafkaRestBridgeDefaultImage() {
        PipesConfig defaults = PipesConfig.builder().build();
        assertThat(defaults.getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:latest");

        PipesConfig config = PipesConfig.builder().kafkaRestBridgeDefaultImage("ghcr.io/aiven-open/karapace:5.0.0").build();
        assertThat(config.getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:5.0.0");
        assertThat(config.toBuilder().build().getKafkaRestBridgeDefaultImage()).isEqualTo("ghcr.io/aiven-open/karapace:5.0.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE", "ghcr.io/aiven-open/karapace:5.0.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE", "ghcr.io/aiven-open/karapace:latest");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_DEFAULT_IMAGE");
    }

    @Test
    void shouldApplyKafkaRestBridgeHostPortRange() {
        PipesConfig defaults = PipesConfig.builder().build();
        assertThat(defaults.getKafkaRestBridgeHostPortBase()).isEqualTo(9500);
        assertThat(defaults.getKafkaRestBridgeHostPortsCount()).isEqualTo(100);
        assertThat(defaults.getKafkaRestBridgeHostPortMax()).isEqualTo(9599);

        PipesConfig config = PipesConfig.builder().kafkaRestBridgeHostPortRange(9700, 10).build();
        PipesConfig copy = config.toBuilder().build();
        assertThat(copy.getKafkaRestBridgeHostPortBase()).isEqualTo(9700);
        assertThat(copy.getKafkaRestBridgeHostPortMax()).isEqualTo(9709);

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap())
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE", "9500")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX", "9599");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE", "9700")
                .containsEntry("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_MAX", "9709");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_PIPES_KAFKA_REST_BRIDGE_HOST_PORT_BASE");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(PipesConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(PipesConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
