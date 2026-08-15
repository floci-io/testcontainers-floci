package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class StepFunctionsConfigTest {

    @Test
    void shouldApplyDefaultStepFunctionsConfig() {
        StepFunctionsConfig config = StepFunctionsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isAllowPlaintextHttp()).isTrue();
    }

    @Test
    void shouldApplyCustomStepFunctionsConfig() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .allowPlaintextHttp(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isAllowPlaintextHttp()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().allowPlaintextHttp(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .allowPlaintextHttp(false)
                .build();
        StepFunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isAllowPlaintextHttp()).isFalse();
    }

}
