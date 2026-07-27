package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SqsConfigTest {

    @Test
    void shouldApplyDefaultSqsConfig() {
        SqsConfig config = SqsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultVisibilityTimeout()).isEqualTo(30);
        assertThat(config.getMaxMessageSize()).isEqualTo(1048576);
        assertThat(config.isClearFifoDeduplicationCacheOnPurge()).isTrue();
    }

    @Test
    void shouldApplyCustomSqsConfig() {
        SqsConfig config = SqsConfig.builder()
                .enabled(false)
                .defaultVisibilityTimeout(60)
                .maxMessageSize(131072)
                .clearFifoDeduplicationCacheOnPurge(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultVisibilityTimeout()).isEqualTo(60);
        assertThat(config.getMaxMessageSize()).isEqualTo(131072);
        assertThat(config.isClearFifoDeduplicationCacheOnPurge()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SQS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", "30")
                .containsEntry("FLOCI_SERVICES_SQS_MAX_MESSAGE_SIZE", "1048576")
                .containsEntry("FLOCI_SERVICES_SQS_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder()
                .defaultVisibilityTimeout(60)
                .maxMessageSize(131072)
                .clearFifoDeduplicationCacheOnPurge(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", "60")
                .containsEntry("FLOCI_SERVICES_SQS_MAX_MESSAGE_SIZE", "131072")
                .containsEntry("FLOCI_SERVICES_SQS_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SQS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SqsConfig config = SqsConfig.builder()
                .enabled(false)
                .defaultVisibilityTimeout(60)
                .maxMessageSize(65536)
                .clearFifoDeduplicationCacheOnPurge(false)
                .build();
        SqsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultVisibilityTimeout()).isEqualTo(60);
        assertThat(copy.getMaxMessageSize()).isEqualTo(65536);
        assertThat(copy.isClearFifoDeduplicationCacheOnPurge()).isFalse();
    }

}
