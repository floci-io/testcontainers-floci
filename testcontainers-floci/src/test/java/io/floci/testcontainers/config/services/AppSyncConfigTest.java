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
        assertThat(config.getVtlMaxLoops()).isEqualTo(10000);
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(1048576);
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(5000L);
        assertThat(config.getGraphqlUrl()).isEmpty();
        assertThat(config.getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.2.0");
        assertThat(config.isJsRuntimeEnabled()).isTrue();
        assertThat(config.getJsRuntimeUrl()).isEmpty();
        assertThat(config.getJsRuntimeImage()).isEqualTo("node:22-alpine");
        assertThat(config.getJsRuntimeContainerName()).isEqualTo("appsync-js-runtime");
        assertThat(config.getJsRuntimePort()).isEqualTo(0);
        assertThat(config.getJsRuntimeStartTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(30);
        assertThat(config.isJsRuntimeEnforceAppsyncSubset()).isTrue();
        assertThat(config.isJsRuntimeKeepRunningOnShutdown()).isFalse();
        assertThat(config.getJsRuntimeDockerNetwork()).isEmpty();
    }

    @Test
    void shouldApplyCustomAppSyncConfig() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .graphqlUrl("http://graphql-sidecar:8080")
                .graphqlImage("floci/floci-sidecar-graphql:0.3.0")
                .jsRuntimeEnabled(false)
                .jsRuntimeUrl("http://appsync-js-runtime:3000")
                .jsRuntimeImage("node:24-alpine")
                .jsRuntimeContainerName("my-js-runtime")
                .jsRuntimePort(3000)
                .jsRuntimeStartTimeoutSeconds(120)
                .jsRuntimeEvaluationTimeoutSeconds(10)
                .jsRuntimeEnforceAppsyncSubset(false)
                .jsRuntimeKeepRunningOnShutdown(true)
                .jsRuntimeDockerNetwork("my-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSchemaWorkerThreads()).isEqualTo(8);
        assertThat(config.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getVtlMaxLoops()).isEqualTo(500);
        assertThat(config.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(config.getVtlTimeoutMillis()).isEqualTo(1000L);
        assertThat(config.getGraphqlUrl()).contains("http://graphql-sidecar:8080");
        assertThat(config.getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.3.0");
        assertThat(config.isJsRuntimeEnabled()).isFalse();
        assertThat(config.getJsRuntimeUrl()).contains("http://appsync-js-runtime:3000");
        assertThat(config.getJsRuntimeImage()).isEqualTo("node:24-alpine");
        assertThat(config.getJsRuntimeContainerName()).isEqualTo("my-js-runtime");
        assertThat(config.getJsRuntimePort()).isEqualTo(3000);
        assertThat(config.getJsRuntimeStartTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(10);
        assertThat(config.isJsRuntimeEnforceAppsyncSubset()).isFalse();
        assertThat(config.isJsRuntimeKeepRunningOnShutdown()).isTrue();
        assertThat(config.getJsRuntimeDockerNetwork()).contains("my-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "4")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "30")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS", "10000")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS", "1048576")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS", "5000")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL")
                .containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE", "floci/floci-sidecar-graphql:0.2.0")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE", "node:22-alpine")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME", "appsync-js-runtime")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT", "0")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS", "30")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN", "false")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder()
                .schemaWorkerThreads(8)
                .schemaWorkerShutdownTimeoutSeconds(60)
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .graphqlUrl("http://graphql-sidecar:8080")
                .graphqlImage("floci/floci-sidecar-graphql:0.3.0")
                .jsRuntimeEnabled(false)
                .jsRuntimeUrl("http://appsync-js-runtime:3000")
                .jsRuntimeImage("node:24-alpine")
                .jsRuntimeContainerName("my-js-runtime")
                .jsRuntimePort(3000)
                .jsRuntimeStartTimeoutSeconds(120)
                .jsRuntimeEvaluationTimeoutSeconds(10)
                .jsRuntimeEnforceAppsyncSubset(false)
                .jsRuntimeKeepRunningOnShutdown(true)
                .jsRuntimeDockerNetwork("my-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", "8")
                .containsEntry("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS", "500")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS", "4096")
                .containsEntry("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS", "1000")
                .containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL", "http://graphql-sidecar:8080")
                .containsEntry("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE", "floci/floci-sidecar-graphql:0.3.0")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL", "http://appsync-js-runtime:3000")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE", "node:24-alpine")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME", "my-js-runtime")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT", "3000")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS", "120")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS", "10")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET", "false")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN", "true")
                .containsEntry("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK", "my-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AppSyncConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_APPSYNC_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN")
                .doesNotContainKey("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AppSyncConfig config = AppSyncConfig.builder()
                .enabled(false)
                .schemaWorkerThreads(2)
                .schemaWorkerShutdownTimeoutSeconds(10)
                .vtlMaxLoops(500)
                .vtlMaxOutputChars(4096)
                .vtlTimeoutMillis(1000L)
                .graphqlUrl("http://graphql-sidecar:8080")
                .graphqlImage("floci/floci-sidecar-graphql:0.3.0")
                .jsRuntimeEnabled(false)
                .jsRuntimeUrl("http://appsync-js-runtime:3000")
                .jsRuntimeImage("node:24-alpine")
                .jsRuntimeContainerName("my-js-runtime")
                .jsRuntimePort(3000)
                .jsRuntimeStartTimeoutSeconds(120)
                .jsRuntimeEvaluationTimeoutSeconds(10)
                .jsRuntimeEnforceAppsyncSubset(false)
                .jsRuntimeKeepRunningOnShutdown(true)
                .jsRuntimeDockerNetwork("my-network")
                .build();
        AppSyncConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSchemaWorkerThreads()).isEqualTo(2);
        assertThat(copy.getSchemaWorkerShutdownTimeoutSeconds()).isEqualTo(10);
        assertThat(copy.getVtlMaxLoops()).isEqualTo(500);
        assertThat(copy.getVtlMaxOutputChars()).isEqualTo(4096);
        assertThat(copy.getVtlTimeoutMillis()).isEqualTo(1000L);
        assertThat(copy.getGraphqlUrl()).contains("http://graphql-sidecar:8080");
        assertThat(copy.getGraphqlImage()).isEqualTo("floci/floci-sidecar-graphql:0.3.0");
        assertThat(copy.isJsRuntimeEnabled()).isFalse();
        assertThat(copy.getJsRuntimeUrl()).contains("http://appsync-js-runtime:3000");
        assertThat(copy.getJsRuntimeImage()).isEqualTo("node:24-alpine");
        assertThat(copy.getJsRuntimeContainerName()).isEqualTo("my-js-runtime");
        assertThat(copy.getJsRuntimePort()).isEqualTo(3000);
        assertThat(copy.getJsRuntimeStartTimeoutSeconds()).isEqualTo(120);
        assertThat(copy.getJsRuntimeEvaluationTimeoutSeconds()).isEqualTo(10);
        assertThat(copy.isJsRuntimeEnforceAppsyncSubset()).isFalse();
        assertThat(copy.isJsRuntimeKeepRunningOnShutdown()).isTrue();
        assertThat(copy.getJsRuntimeDockerNetwork()).contains("my-network");
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
