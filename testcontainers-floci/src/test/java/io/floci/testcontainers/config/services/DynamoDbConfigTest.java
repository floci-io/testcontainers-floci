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
    }

    @Test
    void shouldApplyCustomDynamoDbConfig() {
        DynamoDbConfig config = DynamoDbConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DynamoDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DynamoDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DynamoDbConfig config = DynamoDbConfig.builder()
                .enabled(false)
                .build();
        DynamoDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyVectorIndexAllocationSeconds() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getVectorIndexAllocationSeconds()).isEqualTo(4);

        DynamoDbConfig config = DynamoDbConfig.builder().vectorIndexAllocationSeconds(1).build();
        assertThat(config.getVectorIndexAllocationSeconds()).isEqualTo(1);
        assertThat(config.toBuilder().build().getVectorIndexAllocationSeconds()).isEqualTo(1);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS", "1");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS", "4");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS");
    }

    @Test
    void shouldApplyVectorIndexBackfillSeconds() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getVectorIndexBackfillSeconds()).isEqualTo(10);

        DynamoDbConfig config = DynamoDbConfig.builder().vectorIndexBackfillSeconds(2).build();
        assertThat(config.getVectorIndexBackfillSeconds()).isEqualTo(2);
        assertThat(config.toBuilder().build().getVectorIndexBackfillSeconds()).isEqualTo(2);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS", "2");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS", "10");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS");
    }

    @Test
    void shouldApplyBackend() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getBackend()).isEqualTo("native");

        DynamoDbConfig config = DynamoDbConfig.builder().backend("local").build();
        assertThat(config.getBackend()).isEqualTo("local");
        assertThat(config.toBuilder().build().getBackend()).isEqualTo("local");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_BACKEND", "local");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_BACKEND", "native");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_BACKEND");
    }

    @Test
    void shouldApplyLocalEndpoint() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getLocalEndpoint()).isEmpty();

        DynamoDbConfig config = DynamoDbConfig.builder().localEndpoint("http://dynamodb-local:8000").build();
        assertThat(config.getLocalEndpoint()).contains("http://dynamodb-local:8000");
        assertThat(config.toBuilder().build().getLocalEndpoint()).contains("http://dynamodb-local:8000");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT", "http://dynamodb-local:8000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT");
    }

    @Test
    void shouldApplyLocalConnectTimeoutSeconds() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getLocalConnectTimeoutSeconds()).isEqualTo(2);

        DynamoDbConfig config = DynamoDbConfig.builder().localConnectTimeoutSeconds(5).build();
        assertThat(config.getLocalConnectTimeoutSeconds()).isEqualTo(5);
        assertThat(config.toBuilder().build().getLocalConnectTimeoutSeconds()).isEqualTo(5);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS", "5");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS", "2");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS");
    }

    @Test
    void shouldApplyLocalRequestTimeoutSeconds() {
        DynamoDbConfig defaults = DynamoDbConfig.builder().build();
        assertThat(defaults.getLocalRequestTimeoutSeconds()).isEqualTo(10);

        DynamoDbConfig config = DynamoDbConfig.builder().localRequestTimeoutSeconds(30).build();
        assertThat(config.getLocalRequestTimeoutSeconds()).isEqualTo(30);
        assertThat(config.toBuilder().build().getLocalRequestTimeoutSeconds()).isEqualTo(30);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS", "30");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS", "10");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS");
    }

}
