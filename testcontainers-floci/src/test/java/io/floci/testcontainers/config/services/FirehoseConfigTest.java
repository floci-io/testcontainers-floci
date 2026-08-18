package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FirehoseConfigTest {

    @Test
    void shouldApplyDefaultFirehoseConfig() {
        FirehoseConfig config = FirehoseConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getTickIntervalSeconds()).isEqualTo(10);
        assertThat(config.getFlushRecordCount()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomFirehoseConfig() {
        FirehoseConfig config = FirehoseConfig.builder()
                .enabled(false)
                .tickIntervalSeconds(5)
                .flushRecordCount(1)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getTickIntervalSeconds()).isEqualTo(5);
        assertThat(config.getFlushRecordCount()).isEqualTo(1);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirehoseConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_FIREHOSE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_FIREHOSE_TICK_INTERVAL_SECONDS", "10")
                .containsEntry("FLOCI_SERVICES_FIREHOSE_FLUSH_RECORD_COUNT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirehoseConfig.builder()
                .tickIntervalSeconds(5)
                .flushRecordCount(1)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_FIREHOSE_TICK_INTERVAL_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_FIREHOSE_FLUSH_RECORD_COUNT", "1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FirehoseConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_FIREHOSE_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_FIREHOSE_TICK_INTERVAL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_FIREHOSE_FLUSH_RECORD_COUNT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FirehoseConfig config = FirehoseConfig.builder()
                .enabled(false)
                .tickIntervalSeconds(5)
                .flushRecordCount(1)
                .build();
        FirehoseConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getTickIntervalSeconds()).isEqualTo(5);
        assertThat(copy.getFlushRecordCount()).isEqualTo(1);
    }

}
