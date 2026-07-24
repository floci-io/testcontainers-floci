package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudWatchLogsConfigTest {

    @Test
    void shouldApplyDefaultCloudWatchLogsConfig() {
        CloudWatchLogsConfig config = CloudWatchLogsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getMaxEventsPerQuery()).isEqualTo(10000);
        assertThat(config.getQueryCompletionDelayMs()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomCloudWatchLogsConfig() {
        CloudWatchLogsConfig config = CloudWatchLogsConfig.builder()
                .enabled(false)
                .maxEventsPerQuery(5000)
                .queryCompletionDelayMs(2000)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getMaxEventsPerQuery()).isEqualTo(5000);
        assertThat(config.getQueryCompletionDelayMs()).isEqualTo(2000);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudWatchLogsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_MAX_EVENTS_PER_QUERY", "10000")
                .containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_QUERY_COMPLETION_DELAY_MS", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudWatchLogsConfig.builder()
                .maxEventsPerQuery(5000)
                .queryCompletionDelayMs(2000)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_MAX_EVENTS_PER_QUERY", "5000")
                .containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_QUERY_COMPLETION_DELAY_MS", "2000");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudWatchLogsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDWATCHLOGS_ENABLED", "false");
    }
}
