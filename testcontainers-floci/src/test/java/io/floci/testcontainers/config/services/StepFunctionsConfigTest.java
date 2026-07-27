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
    }

    @Test
    void shouldApplyCustomStepFunctionsConfig() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .build();
        StepFunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
