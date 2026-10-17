package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbConfigTest {

    @Test
    void shouldApplyDefaultDynamoDbConfig() {
        DynamoDbConfig config = DynamoDbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getVectorIndexAllocationSeconds()).isEqualTo(4);
        assertThat(config.getVectorIndexBackfillSeconds()).isEqualTo(10);
        assertThat(config.getBackend()).isEqualTo("native");
        assertThat(config.getLocalEndpoint()).isEmpty();
        assertThat(config.getLocalConnectTimeoutSeconds()).isEqualTo(2);
        assertThat(config.getLocalRequestTimeoutSeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyCustomDynamoDbConfig() {
        DynamoDbConfig config = DynamoDbConfig.builder()
                .enabled(false)
                .vectorIndexAllocationSeconds(1)
                .vectorIndexBackfillSeconds(2)
                .backend("local")
                .localEndpoint("http://dynamodb-local:8000")
                .localConnectTimeoutSeconds(5)
                .localRequestTimeoutSeconds(30)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getVectorIndexAllocationSeconds()).isEqualTo(1);
        assertThat(config.getVectorIndexBackfillSeconds()).isEqualTo(2);
        assertThat(config.getBackend()).isEqualTo("local");
        assertThat(config.getLocalEndpoint()).contains("http://dynamodb-local:8000");
        assertThat(config.getLocalConnectTimeoutSeconds()).isEqualTo(5);
        assertThat(config.getLocalRequestTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DynamoDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DYNAMODB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS", "4")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS", "10")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_BACKEND", "native")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS", "2")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS", "10");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DynamoDbConfig.builder()
                .vectorIndexAllocationSeconds(1)
                .vectorIndexBackfillSeconds(2)
                .backend("local")
                .localEndpoint("http://dynamodb-local:8000")
                .localConnectTimeoutSeconds(5)
                .localRequestTimeoutSeconds(30)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS", "1")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS", "2")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_BACKEND", "local")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT", "http://dynamodb-local:8000")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS", "5")
                .containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DynamoDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DYNAMODB_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_BACKEND")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DynamoDbConfig config = DynamoDbConfig.builder()
                .enabled(false)
                .vectorIndexAllocationSeconds(1)
                .vectorIndexBackfillSeconds(2)
                .backend("local")
                .localEndpoint("http://dynamodb-local:8000")
                .localConnectTimeoutSeconds(5)
                .localRequestTimeoutSeconds(30)
                .build();
        DynamoDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getVectorIndexAllocationSeconds()).isEqualTo(1);
        assertThat(copy.getVectorIndexBackfillSeconds()).isEqualTo(2);
        assertThat(copy.getBackend()).isEqualTo("local");
        assertThat(copy.getLocalEndpoint()).contains("http://dynamodb-local:8000");
        assertThat(copy.getLocalConnectTimeoutSeconds()).isEqualTo(5);
        assertThat(copy.getLocalRequestTimeoutSeconds()).isEqualTo(30);
    }

}
