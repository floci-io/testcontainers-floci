package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ApiGatewayConfigTest {

    @Test
    void shouldApplyDefaultApiGatewayConfig() {
        ApiGatewayConfig config = ApiGatewayConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomApiGatewayConfig() {
        ApiGatewayConfig config = ApiGatewayConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApiGatewayConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ApiGatewayConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ApiGatewayConfig config = ApiGatewayConfig.builder()
                .enabled(false)
                .build();
        ApiGatewayConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyVtlMaxLoops() {
        ApiGatewayConfig defaults = ApiGatewayConfig.builder().build();
        assertThat(defaults.getVtlMaxLoops()).isEqualTo(10000);

        ApiGatewayConfig config = ApiGatewayConfig.builder().vtlMaxLoops(500).build();
        assertThat(config.getVtlMaxLoops()).isEqualTo(500);
        assertThat(config.toBuilder().build().getVtlMaxLoops()).isEqualTo(500);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS", "500");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS", "10000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS");
    }

    @Test
    void shouldApplyVtlMaxOutputChars() {
        ApiGatewayConfig defaults = ApiGatewayConfig.builder().build();
        assertThat(defaults.getVtlMaxOutputChars()).isEqualTo(1048576);

        ApiGatewayConfig config = ApiGatewayConfig.builder().vtlMaxOutputChars(4096).build();
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(config.toBuilder().build().getVtlMaxOutputChars()).isEqualTo(4096);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS", "4096");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS", "1048576");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS");
    }

    @Test
    void shouldApplyVtlTimeoutMillis() {
        ApiGatewayConfig defaults = ApiGatewayConfig.builder().build();
        assertThat(defaults.getVtlTimeoutMillis()).isEqualTo(5000L);

        ApiGatewayConfig config = ApiGatewayConfig.builder().vtlTimeoutMillis(1000L).build();
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(1000L);
        assertThat(config.toBuilder().build().getVtlTimeoutMillis()).isEqualTo(1000L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS", "1000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS", "5000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS");
    }

}
