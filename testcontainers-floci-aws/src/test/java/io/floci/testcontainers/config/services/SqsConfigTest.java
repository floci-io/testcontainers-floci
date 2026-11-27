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
        assertThat(config.getReceiptHandleSecret()).isEqualTo("local-emulator-secret");
        assertThat(config.isCloudwatchMetricsEnabled()).isTrue();
        assertThat(config.getEndpointStrategy()).isEqualTo(SqsConfig.EndpointStrategy.FLOCI);
    }

    @Test
    void shouldApplyCustomSqsConfig() {
        SqsConfig config = SqsConfig.builder()
                .enabled(false)
                .defaultVisibilityTimeout(60)
                .maxMessageSize(131072)
                .clearFifoDeduplicationCacheOnPurge(false)
                .receiptHandleSecret("my-secret")
                .cloudwatchMetricsEnabled(false)
                .endpointStrategy(SqsConfig.EndpointStrategy.AWS)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultVisibilityTimeout()).isEqualTo(60);
        assertThat(config.getMaxMessageSize()).isEqualTo(131072);
        assertThat(config.isClearFifoDeduplicationCacheOnPurge()).isFalse();
        assertThat(config.getReceiptHandleSecret()).isEqualTo("my-secret");
        assertThat(config.isCloudwatchMetricsEnabled()).isFalse();
        assertThat(config.getEndpointStrategy()).isEqualTo(SqsConfig.EndpointStrategy.AWS);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SQS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", "30")
                .containsEntry("FLOCI_SERVICES_SQS_MAX_MESSAGE_SIZE", "1048576")
                .containsEntry("FLOCI_SERVICES_SQS_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE", "true")
                .containsEntry("FLOCI_SERVICES_SQS_RECEIPT_HANDLE_SECRET", "local-emulator-secret")
                .containsEntry("FLOCI_SERVICES_SQS_CLOUDWATCH_METRICS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SQS_ENDPOINT_STRATEGY", "floci");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder()
                .defaultVisibilityTimeout(60)
                .maxMessageSize(131072)
                .clearFifoDeduplicationCacheOnPurge(false)
                .receiptHandleSecret("my-secret")
                .cloudwatchMetricsEnabled(false)
                .endpointStrategy(SqsConfig.EndpointStrategy.AWS)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", "60")
                .containsEntry("FLOCI_SERVICES_SQS_MAX_MESSAGE_SIZE", "131072")
                .containsEntry("FLOCI_SERVICES_SQS_CLEAR_FIFO_DEDUPLICATION_CACHE_ON_PURGE", "false")
                .containsEntry("FLOCI_SERVICES_SQS_RECEIPT_HANDLE_SECRET", "my-secret")
                .containsEntry("FLOCI_SERVICES_SQS_CLOUDWATCH_METRICS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_SQS_ENDPOINT_STRATEGY", "aws");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SqsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SQS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SQS_RECEIPT_HANDLE_SECRET")
                .doesNotContainKey("FLOCI_SERVICES_SQS_CLOUDWATCH_METRICS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_SQS_ENDPOINT_STRATEGY");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SqsConfig config = SqsConfig.builder()
                .enabled(false)
                .defaultVisibilityTimeout(60)
                .maxMessageSize(65536)
                .clearFifoDeduplicationCacheOnPurge(false)
                .receiptHandleSecret("my-secret")
                .cloudwatchMetricsEnabled(false)
                .endpointStrategy(SqsConfig.EndpointStrategy.AWS)
                .build();
        SqsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultVisibilityTimeout()).isEqualTo(60);
        assertThat(copy.getMaxMessageSize()).isEqualTo(65536);
        assertThat(copy.isClearFifoDeduplicationCacheOnPurge()).isFalse();
        assertThat(copy.getReceiptHandleSecret()).isEqualTo("my-secret");
        assertThat(copy.isCloudwatchMetricsEnabled()).isFalse();
        assertThat(copy.getEndpointStrategy()).isEqualTo(SqsConfig.EndpointStrategy.AWS);
    }

}
