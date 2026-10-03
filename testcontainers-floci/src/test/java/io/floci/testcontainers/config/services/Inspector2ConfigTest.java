package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Inspector2ConfigTest {

    @Test
    void shouldApplyDefaultInspector2Config() {
        Inspector2Config config = Inspector2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomInspector2Config() {
        Inspector2Config config = Inspector2Config.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Inspector2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_INSPECTOR2_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Inspector2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_INSPECTOR2_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Inspector2Config config = Inspector2Config.builder()
                .enabled(false)
                .build();
        Inspector2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
