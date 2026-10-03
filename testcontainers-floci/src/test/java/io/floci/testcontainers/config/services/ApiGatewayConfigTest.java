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
        assertThat(config.getVtlMaxLoops()).isEqualTo(10000);
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(1048576);
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(5000L);
    }

    @Test
    void shouldApplyCustomApiGatewayConfig() {
        ApiGatewayConfig config = ApiGatewayConfig.builder()
                .enabled(false)
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getVtlMaxLoops()).isEqualTo(500);
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(1000L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApiGatewayConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS", "10000")
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS", "1048576")
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS", "5000");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ApiGatewayConfig.builder()
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS", "500")
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS", "4096")
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS", "1000");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ApiGatewayConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APIGATEWAY_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS")
                .doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS")
                .doesNotContainKey("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ApiGatewayConfig config = ApiGatewayConfig.builder()
                .enabled(false)
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .build();
        ApiGatewayConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getVtlMaxLoops()).isEqualTo(500);
        assertThat(copy.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(copy.getVtlTimeoutMillis()).isEqualTo(1000L);
    }

}
