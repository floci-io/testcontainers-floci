package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class LambdaConfigTest {

    @Test
    void shouldApplyDefaultLambdaConfig() {
        LambdaConfig config = LambdaConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isEphemeral()).isFalse();
        assertThat(config.isExposeRuntimePorts()).isFalse();
        assertThat(config.getDefaultMemoryMb()).isEqualTo(128);
        assertThat(config.getDefaultTimeoutSeconds()).isEqualTo(3);
        assertThat(config.getDockerNetwork()).isNull();
        assertThat(config.getRuntimeApiBasePort()).isEqualTo(9200);
        assertThat(config.getRuntimeApiMaxPort()).isEqualTo(9209);
        assertThat(config.getRuntimeApiPortsCount()).isEqualTo(10);
        assertThat(config.getPollIntervalMs()).isEqualTo(1000);
        assertThat(config.getContainerIdleTimeoutSeconds()).isEqualTo(300);
        assertThat(config.getRegionConcurrencyLimit()).isEqualTo(1000);
        assertThat(config.getUnreservedConcurrencyMin()).isEqualTo(100);
        assertThat(config.getAwsConfigPath()).isNull();
        assertThat(config.getExtraHosts()).isEmpty();
        assertThat(config.getEcrBaseUri()).isEqualTo("public.ecr.aws");
        assertThat(config.getContainerNamePrefix()).isEmpty();
    }

    @Test
    void shouldApplyCustomLambdaConfig() {
        LambdaConfig config = LambdaConfig.builder()
                .enabled(false)
                .ephemeral(true)
                .exposeRuntimePorts(true)
                .defaultMemoryMb(256)
                .defaultTimeoutSeconds(10)
                .dockerNetwork("my-network")
                .runtimeApiPortRange(9300, 50)
                .pollIntervalMs(500)
                .containerIdleTimeoutSeconds(600)
                .regionConcurrencyLimit(500)
                .unreservedConcurrencyMin(50)
                .awsConfigPath("/home/user/.aws")
                .extraHosts(java.util.List.of("host.docker.internal:host-gateway", "my-service:172.17.0.1"))
                .ecrBaseUri("123456789012.dkr.ecr.us-east-1.amazonaws.com")
                .containerNamePrefix("acme")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isEphemeral()).isTrue();
        assertThat(config.isExposeRuntimePorts()).isTrue();
        assertThat(config.getDefaultMemoryMb()).isEqualTo(256);
        assertThat(config.getDefaultTimeoutSeconds()).isEqualTo(10);
        assertThat(config.getDockerNetwork()).isEqualTo("my-network");
        assertThat(config.getRuntimeApiBasePort()).isEqualTo(9300);
        assertThat(config.getRuntimeApiMaxPort()).isEqualTo(9349);
        assertThat(config.getRuntimeApiPortsCount()).isEqualTo(50);
        assertThat(config.getPollIntervalMs()).isEqualTo(500);
        assertThat(config.getContainerIdleTimeoutSeconds()).isEqualTo(600);
        assertThat(config.getRegionConcurrencyLimit()).isEqualTo(500);
        assertThat(config.getUnreservedConcurrencyMin()).isEqualTo(50);
        assertThat(config.getAwsConfigPath()).isEqualTo("/home/user/.aws");
        assertThat(config.getExtraHosts()).isPresent();
        assertThat(config.getExtraHosts().get())
                .containsExactly("host.docker.internal:host-gateway", "my-service:172.17.0.1");
        assertThat(config.getEcrBaseUri()).isEqualTo("123456789012.dkr.ecr.us-east-1.amazonaws.com");
        assertThat(config.getContainerNamePrefix()).contains("acme");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_LAMBDA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_LAMBDA_EPHEMERAL", "false")
                .containsEntry("FLOCI_SERVICES_LAMBDA_DEFAULT_MEMORY_MB", "128")
                .containsEntry("FLOCI_SERVICES_LAMBDA_DEFAULT_TIMEOUT_SECONDS", "3")
                .containsEntry("FLOCI_SERVICES_LAMBDA_RUNTIME_API_BASE_PORT", "9200")
                .containsEntry("FLOCI_SERVICES_LAMBDA_RUNTIME_API_MAX_PORT", "9209")
                .containsEntry("FLOCI_SERVICES_LAMBDA_POLL_INTERVAL_MS", "1000")
                .containsEntry("FLOCI_SERVICES_LAMBDA_CONTAINER_IDLE_TIMEOUT_SECONDS", "300")
                .containsEntry("FLOCI_SERVICES_LAMBDA_REGION_CONCURRENCY_LIMIT", "1000")
                .containsEntry("FLOCI_SERVICES_LAMBDA_UNRESERVED_CONCURRENCY_MIN", "100")
                .containsEntry("FLOCI_ECR_BASE_URI", "public.ecr.aws")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_AWS_CONFIG_PATH")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_EXTRA_HOSTS")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_CONTAINER_NAME_PREFIX");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .enabled(true)
                .ephemeral(true)
                .defaultMemoryMb(256)
                .defaultTimeoutSeconds(30)
                .runtimeApiPortRange(9500, 50)
                .pollIntervalMs(500)
                .containerIdleTimeoutSeconds(600)
                .regionConcurrencyLimit(500)
                .unreservedConcurrencyMin(50)
                .dockerNetwork("my-network")
                .awsConfigPath("/home/user/.aws")
                .extraHosts(java.util.List.of("host.docker.internal:host-gateway", "my-service:172.17.0.1"))
                .ecrBaseUri("123456789012.dkr.ecr.us-east-1.amazonaws.com")
                .containerNamePrefix("acme")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_LAMBDA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_LAMBDA_DEFAULT_MEMORY_MB", "256")
                .containsEntry("FLOCI_SERVICES_LAMBDA_EPHEMERAL", "true")
                .containsEntry("FLOCI_SERVICES_LAMBDA_DEFAULT_TIMEOUT_SECONDS", "30")
                .containsEntry("FLOCI_SERVICES_LAMBDA_RUNTIME_API_BASE_PORT", "9500")
                .containsEntry("FLOCI_SERVICES_LAMBDA_RUNTIME_API_MAX_PORT", "9549")
                .containsEntry("FLOCI_SERVICES_LAMBDA_POLL_INTERVAL_MS", "500")
                .containsEntry("FLOCI_SERVICES_LAMBDA_CONTAINER_IDLE_TIMEOUT_SECONDS", "600")
                .containsEntry("FLOCI_SERVICES_LAMBDA_REGION_CONCURRENCY_LIMIT", "500")
                .containsEntry("FLOCI_SERVICES_LAMBDA_UNRESERVED_CONCURRENCY_MIN", "50")
                .containsEntry("FLOCI_SERVICES_LAMBDA_DOCKER_NETWORK", "my-network")
                .containsEntry("FLOCI_SERVICES_LAMBDA_AWS_CONFIG_PATH", "/home/user/.aws")
                .containsEntry("FLOCI_SERVICES_LAMBDA_EXTRA_HOSTS", "host.docker.internal:host-gateway,my-service:172.17.0.1")
                .containsEntry("FLOCI_ECR_BASE_URI", "123456789012.dkr.ecr.us-east-1.amazonaws.com")
                .containsEntry("FLOCI_SERVICES_LAMBDA_CONTAINER_NAME_PREFIX", "acme");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LAMBDA_ENABLED", "false");
    }

    @Test
    void shouldExposeLambdaRuntimeApiPortsWhenEnabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withLambdaConfig(c -> c
                    .exposeRuntimePorts(true)
                    .runtimeApiPortRange(9500, 10));

            var ports = container.getExposedPorts();
            for (int port = 9500; port < 9510; port++) {
                assertThat(ports).contains(port);
            }
        }
    }

    @Test
    void shouldNotExposeLambdaRuntimeApiPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withLambdaConfig(c -> c
                    .enabled(false)
                    .runtimeApiPortRange(9500, 10));

            assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LAMBDA_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(9500);
        }
    }

    @Test
    void shouldApplyDefaultHotReloadConfig() {
        LambdaConfig config = LambdaConfig.builder().build();
        assertThat(config.getHotReload().enabled()).isFalse();
        assertThat(config.getHotReload().allowedPaths()).isEmpty();
    }

    @Test
    void shouldApplyEnabledHotReloadConfig() {
        LambdaConfig config = LambdaConfig.builder()
                .hotReload(true)
                .build();
        assertThat(config.getHotReload().enabled()).isTrue();
        assertThat(config.getHotReload().allowedPaths()).isEmpty();
    }

    @Test
    void shouldApplyHotReloadConfigWithAllowedPaths() {
        var allowedPaths = java.util.List.of("/home/user/code", "/opt/projects");
        LambdaConfig config = LambdaConfig.builder()
                .hotReload(true, allowedPaths)
                .build();
        assertThat(config.getHotReload().enabled()).isTrue();
        assertThat(config.getHotReload().allowedPaths()).isPresent();
        assertThat(config.getHotReload().allowedPaths().get()).containsExactly("/home/user/code", "/opt/projects");
    }

    @Test
    void shouldApplyHotReloadEnvVarsWithDefaultConfig() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ALLOWED_PATHS");
    }

    @Test
    void shouldApplyHotReloadEnvVarsWhenEnabled() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .hotReload(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ALLOWED_PATHS");
    }

    @Test
    void shouldApplyHotReloadEnvVarsWithAllowedPaths() {
        var allowedPaths = java.util.List.of("/home/user/code", "/opt/projects");
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .hotReload(true, allowedPaths)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_LAMBDA_HOT_RELOAD_ALLOWED_PATHS", "/home/user/code,/opt/projects");
    }

    @Test
    void shouldNotApplyAwsConfigPathEnvVarWhenBlank() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .awsConfigPath("   ")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_LAMBDA_AWS_CONFIG_PATH");
    }

    @Test
    void shouldNotApplyContainerNamePrefixEnvVarWhenBlank() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .containerNamePrefix("   ")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_LAMBDA_CONTAINER_NAME_PREFIX");
    }

    @Test
    void shouldNotApplyEmptyExtraHostsEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        LambdaConfig.builder()
                .extraHosts(java.util.List.of())
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_LAMBDA_EXTRA_HOSTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        LambdaConfig config = LambdaConfig.builder()
                .enabled(false)
                .ephemeral(true)
                .exposeRuntimePorts(true)
                .defaultMemoryMb(256)
                .defaultTimeoutSeconds(10)
                .dockerNetwork("test-network")
                .runtimeApiPortRange(9300, 5)
                .pollIntervalMs(500)
                .containerIdleTimeoutSeconds(60)
                .regionConcurrencyLimit(500)
                .unreservedConcurrencyMin(50)
                .hotReload(true)
                .awsConfigPath("/test/aws-config")
                .extraHosts(java.util.List.of("host.docker.internal:host-gateway"))
                .ecrBaseUri("123456789012.dkr.ecr.us-east-1.amazonaws.com")
                .containerNamePrefix("acme-prefix")
                .build();
        LambdaConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isEphemeral()).isTrue();
        assertThat(copy.isExposeRuntimePorts()).isTrue();
        assertThat(copy.getDefaultMemoryMb()).isEqualTo(256);
        assertThat(copy.getDefaultTimeoutSeconds()).isEqualTo(10);
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getRuntimeApiBasePort()).isEqualTo(9300);
        assertThat(copy.getRuntimeApiPortsCount()).isEqualTo(5);
        assertThat(copy.getPollIntervalMs()).isEqualTo(500);
        assertThat(copy.getContainerIdleTimeoutSeconds()).isEqualTo(60);
        assertThat(copy.getRegionConcurrencyLimit()).isEqualTo(500);
        assertThat(copy.getUnreservedConcurrencyMin()).isEqualTo(50);
        assertThat(copy.getHotReload().enabled()).isTrue();
        assertThat(copy.getAwsConfigPath()).isEqualTo("/test/aws-config");
        assertThat(copy.getExtraHosts()).isPresent();
        assertThat(copy.getExtraHosts().get()).containsExactly("host.docker.internal:host-gateway");
        assertThat(copy.getEcrBaseUri()).isEqualTo("123456789012.dkr.ecr.us-east-1.amazonaws.com");
        assertThat(copy.getContainerNamePrefix()).contains("acme-prefix");
    }

}
