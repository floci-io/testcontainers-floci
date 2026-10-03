package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AppSyncConfigTest {

    @Test
    void shouldApplyDefaultAppSyncConfig() {
        AppSyncConfig config = AppSyncConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getSchemaWorkerThreads()).isEqualTo(4);
        assertThat(config.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyCustomAppSyncConfig() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSchemaWorkerThreads()).isEqualTo(8);
        assertThat(config.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "4")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder()
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "8")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(2)
                .schemaWorkerShutdownTimeoutSeconds(10)
                .build();
        AppSyncConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSchemaWorkerThreads()).isEqualTo(2);
        assertThat(copy.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyVtlMaxLoops() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getVtlMaxLoops()).isEqualTo(10000);

        AppSyncConfig config = AppSyncConfig.builder().vtlMaxLoops(500).build();
        assertThat(config.getVtlMaxLoops()).isEqualTo(500);
        assertThat(config.toBuilder().build().getVtlMaxLoops()).isEqualTo(500);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS", "500");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS", "10000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS");
    }

    @Test
    void shouldApplyVtlMaxOutputChars() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getVtlMaxOutputChars()).isEqualTo(1048576);

        AppSyncConfig config = AppSyncConfig.builder().vtlMaxOutputChars(4096).build();
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(config.toBuilder().build().getVtlMaxOutputChars()).isEqualTo(4096);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS", "4096");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS", "1048576");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS");
    }

    @Test
    void shouldApplyVtlTimeoutMillis() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getVtlTimeoutMillis()).isEqualTo(5000L);

        AppSyncConfig config = AppSyncConfig.builder().vtlTimeoutMillis(1000L).build();
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(1000L);
        assertThat(config.toBuilder().build().getVtlTimeoutMillis()).isEqualTo(1000L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS", "1000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS", "5000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS");
    }

    @Test
    void shouldApplyGraphqlUrl() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getGraphqlUrl()).isEmpty();

        AppSyncConfig config = AppSyncConfig.builder().graphqlUrl("http://graphql-sidecar:8080").build();
        assertThat(config.getGraphqlUrl()).contains("http://graphql-sidecar:8080");
        assertThat(config.toBuilder().build().getGraphqlUrl()).contains("http://graphql-sidecar:8080");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL", "http://graphql-sidecar:8080");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL");
    }

    @Test
    void shouldApplyGraphqlImage() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.2.0");

        AppSyncConfig config = AppSyncConfig.builder().graphqlImage("floci/floci-sidecar-graphql:0.3.0").build();
        assertThat(config.getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.3.0");
        assertThat(config.toBuilder().build().getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.3.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE", "floci/floci-sidecar-graphql:0.3.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE", "floci/floci-sidecar-graphql:0.2.0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE");
    }

    @Test
    void shouldApplyJsRuntimeEnabled() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.isJsRuntimeEnabled()).isEqualTo(true);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeEnabled(false).build();
        assertThat(config.isJsRuntimeEnabled()).isEqualTo(false);
        assertThat(config.toBuilder().build().isJsRuntimeEnabled()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED");
    }

    @Test
    void shouldApplyJsRuntimeUrl() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeUrl()).isEmpty();

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeUrl("http://appsync-js-runtime:3000").build();
        assertThat(config.getJsRuntimeUrl()).contains("http://appsync-js-runtime:3000");
        assertThat(config.toBuilder().build().getJsRuntimeUrl()).contains("http://appsync-js-runtime:3000");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL", "http://appsync-js-runtime:3000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL");
    }

    @Test
    void shouldApplyJsRuntimeImage() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeImage()).isEqualTo("node:22-alpine");

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeImage("node:24-alpine").build();
        assertThat(config.getJsRuntimeImage()).isEqualTo("node:24-alpine");
        assertThat(config.toBuilder().build().getJsRuntimeImage()).isEqualTo("node:24-alpine");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE", "node:24-alpine");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE", "node:22-alpine");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE");
    }

    @Test
    void shouldApplyJsRuntimeContainerName() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeContainerName()).isEqualTo("appsync-js-runtime");

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeContainerName("my-js-runtime").build();
        assertThat(config.getJsRuntimeContainerName()).isEqualTo("my-js-runtime");
        assertThat(config.toBuilder().build().getJsRuntimeContainerName()).isEqualTo("my-js-runtime");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME", "my-js-runtime");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME", "appsync-js-runtime");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME");
    }

    @Test
    void shouldApplyJsRuntimePort() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimePort()).isEqualTo(0);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimePort(3000).build();
        assertThat(config.getJsRuntimePort()).isEqualTo(3000);
        assertThat(config.toBuilder().build().getJsRuntimePort()).isEqualTo(3000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT", "3000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT");
    }

    @Test
    void shouldApplyJsRuntimeStartTimeoutSeconds() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeStartTimeoutSeconds()).isEqualTo(60);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeStartTimeoutSeconds(120).build();
        assertThat(config.getJsRuntimeStartTimeoutSeconds()).isEqualTo(120);
        assertThat(config.toBuilder().build().getJsRuntimeStartTimeoutSeconds()).isEqualTo(120);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS", "120");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS", "60");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS");
    }

    @Test
    void shouldApplyJsRuntimeEvaluationTimeoutSeconds() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(30);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeEvaluationTimeoutSeconds(10).build();
        assertThat(config.getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(10);
        assertThat(config.toBuilder().build().getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(10);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS", "10");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS", "30");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS");
    }

    @Test
    void shouldApplyJsRuntimeEnforceAppsyncSubset() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.isJsRuntimeEnforceAppsyncSubset()).isEqualTo(true);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeEnforceAppsyncSubset(false).build();
        assertThat(config.isJsRuntimeEnforceAppsyncSubset()).isEqualTo(false);
        assertThat(config.toBuilder().build().isJsRuntimeEnforceAppsyncSubset()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET");
    }

    @Test
    void shouldApplyJsRuntimeKeepRunningOnShutdown() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.isJsRuntimeKeepRunningOnShutdown()).isEqualTo(false);

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeKeepRunningOnShutdown(true).build();
        assertThat(config.isJsRuntimeKeepRunningOnShutdown()).isEqualTo(true);
        assertThat(config.toBuilder().build().isJsRuntimeKeepRunningOnShutdown()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN");
    }

    @Test
    void shouldApplyJsRuntimeDockerNetwork() {
        AppSyncConfig defaults = AppSyncConfig.builder().build();
        assertThat(defaults.getJsRuntimeDockerNetwork()).isEmpty();

        AppSyncConfig config = AppSyncConfig.builder().jsRuntimeDockerNetwork("my-network").build();
        assertThat(config.getJsRuntimeDockerNetwork()).contains("my-network");
        assertThat(config.toBuilder().build().getJsRuntimeDockerNetwork()).contains("my-network");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK", "my-network");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK");
    }

    @Test
    void shouldRequireDockerSocketForSidecars() {
        assertThat(AppSyncConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(AppSyncConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(AppSyncConfig.builder().graphqlUrl("http://graphql:8080").build().requiresDockerSocket()).isTrue();
        assertThat(AppSyncConfig.builder()
                .graphqlUrl("http://graphql:8080")
                .jsRuntimeEnabled(false)
                .build().requiresDockerSocket()).isFalse();
        assertThat(AppSyncConfig.builder()
                .graphqlUrl("http://graphql:8080")
                .jsRuntimeUrl("http://js-runtime:3000")
                .build().requiresDockerSocket()).isFalse();
    }

}
