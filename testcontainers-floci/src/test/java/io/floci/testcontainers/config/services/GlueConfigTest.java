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
        assertThat(config.getJobRunDurationSeconds()).isEqualTo(0);
        assertThat(config.getCrawlerRunDurationSeconds()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomGlueConfig() {
        GlueConfig config = GlueConfig.builder()
                .enabled(false)
                .jobRunDurationSeconds(5)
                .crawlerRunDurationSeconds(5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getJobRunDurationSeconds()).isEqualTo(5);
        assertThat(config.getCrawlerRunDurationSeconds()).isEqualTo(5);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GlueConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_GLUE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS", "0")
                .containsEntry("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GlueConfig.builder()
                .jobRunDurationSeconds(5)
                .crawlerRunDurationSeconds(5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS", "5");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GlueConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_GLUE_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GlueConfig config = GlueConfig.builder()
                .enabled(false)
                .jobRunDurationSeconds(5)
                .crawlerRunDurationSeconds(5)
                .build();
        GlueConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getJobRunDurationSeconds()).isEqualTo(5);
        assertThat(copy.getCrawlerRunDurationSeconds()).isEqualTo(5);
    }

}
