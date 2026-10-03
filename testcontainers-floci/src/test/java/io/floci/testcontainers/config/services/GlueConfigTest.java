package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GlueConfigTest {

    @Test
    void shouldApplyDefaultGlueConfig() {
        GlueConfig config = GlueConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomGlueConfig() {
        GlueConfig config = GlueConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GlueConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GlueConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GlueConfig config = GlueConfig.builder()
                .enabled(false)
                .build();
        GlueConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyJobRunDurationSeconds() {
        GlueConfig defaults = GlueConfig.builder().build();
        assertThat(defaults.getJobRunDurationSeconds()).isEqualTo(0);

        GlueConfig config = GlueConfig.builder().jobRunDurationSeconds(5).build();
        assertThat(config.getJobRunDurationSeconds()).isEqualTo(5);
        assertThat(config.toBuilder().build().getJobRunDurationSeconds()).isEqualTo(5);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS", "5");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS");
    }

    @Test
    void shouldApplyCrawlerRunDurationSeconds() {
        GlueConfig defaults = GlueConfig.builder().build();
        assertThat(defaults.getCrawlerRunDurationSeconds()).isEqualTo(0);

        GlueConfig config = GlueConfig.builder().crawlerRunDurationSeconds(5).build();
        assertThat(config.getCrawlerRunDurationSeconds()).isEqualTo(5);
        assertThat(config.toBuilder().build().getCrawlerRunDurationSeconds()).isEqualTo(5);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS", "5");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS");
    }

}
