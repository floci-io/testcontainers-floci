package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class QuickSightConfigTest {

    @Test
    void shouldApplyDefaultQuickSightConfig() {
        QuickSightConfig config = QuickSightConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getEmbedBaseUrl()).isEmpty();
    }

    @Test
    void shouldApplyCustomQuickSightConfig() {
        QuickSightConfig config = QuickSightConfig.builder()
                .enabled(false)
                .embedBaseUrl("https://quicksight.example.test")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getEmbedBaseUrl()).contains("https://quicksight.example.test");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        QuickSightConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_QUICKSIGHT_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_QUICKSIGHT_EMBED_BASE_URL");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        QuickSightConfig.builder()
                .embedBaseUrl("https://quicksight.example.test")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_QUICKSIGHT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_QUICKSIGHT_EMBED_BASE_URL", "https://quicksight.example.test");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        QuickSightConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_QUICKSIGHT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_QUICKSIGHT_EMBED_BASE_URL");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        QuickSightConfig config = QuickSightConfig.builder()
                .enabled(false)
                .embedBaseUrl("https://quicksight.example.test")
                .build();
        QuickSightConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getEmbedBaseUrl()).contains("https://quicksight.example.test");
    }

}
