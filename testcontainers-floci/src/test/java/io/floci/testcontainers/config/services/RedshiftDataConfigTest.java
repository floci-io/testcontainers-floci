package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftDataConfigTest {

    @Test
    void shouldApplyDefaultRedshiftDataConfig() {
        RedshiftDataConfig config = RedshiftDataConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getResultTtlHours()).isEqualTo(24);
        assertThat(config.getMaxConcurrentStatements()).isEqualTo(10);
        assertThat(config.getQueueCapacity()).isEqualTo(100);
        assertThat(config.getShutdownTimeoutSeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyCustomRedshiftDataConfig() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .resultTtlHours(1)
                .maxConcurrentStatements(4)
                .queueCapacity(20)
                .shutdownTimeoutSeconds(30)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getResultTtlHours()).isEqualTo(1);
        assertThat(config.getMaxConcurrentStatements()).isEqualTo(4);
        assertThat(config.getQueueCapacity()).isEqualTo(20);
        assertThat(config.getShutdownTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "24")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_MAX_CONCURRENT_STATEMENTS", "10")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_QUEUE_CAPACITY", "100")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_SHUTDOWN_TIMEOUT_SECONDS", "10");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder()
                .resultTtlHours(1)
                .maxConcurrentStatements(4)
                .queueCapacity(20)
                .shutdownTimeoutSeconds(30)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "1")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_MAX_CONCURRENT_STATEMENTS", "4")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_QUEUE_CAPACITY", "20")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_SHUTDOWN_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_MAX_CONCURRENT_STATEMENTS")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_QUEUE_CAPACITY")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_SHUTDOWN_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .resultTtlHours(1)
                .maxConcurrentStatements(4)
                .queueCapacity(20)
                .shutdownTimeoutSeconds(30)
                .build();
        RedshiftDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getResultTtlHours()).isEqualTo(1);
        assertThat(copy.getMaxConcurrentStatements()).isEqualTo(4);
        assertThat(copy.getQueueCapacity()).isEqualTo(20);
        assertThat(copy.getShutdownTimeoutSeconds()).isEqualTo(30);
    }

}
