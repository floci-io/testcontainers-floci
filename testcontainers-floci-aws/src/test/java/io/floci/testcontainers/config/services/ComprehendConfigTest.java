package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ComprehendConfigTest {

    @Test
    void shouldApplyDefaultComprehendConfig() {
        ComprehendConfig config = ComprehendConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomComprehendConfig() {
        ComprehendConfig config = ComprehendConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ComprehendConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_COMPREHEND_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ComprehendConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_COMPREHEND_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ComprehendConfig config = ComprehendConfig.builder()
                .enabled(false)
                .build();
        ComprehendConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
