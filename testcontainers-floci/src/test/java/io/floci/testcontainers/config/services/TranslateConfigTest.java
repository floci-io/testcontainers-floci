package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TranslateConfigTest {

    @Test
    void shouldApplyDefaultTranslateConfig() {
        TranslateConfig config = TranslateConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomTranslateConfig() {
        TranslateConfig config = TranslateConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TranslateConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSLATE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TranslateConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSLATE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TranslateConfig config = TranslateConfig.builder()
                .enabled(false)
                .build();
        TranslateConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
