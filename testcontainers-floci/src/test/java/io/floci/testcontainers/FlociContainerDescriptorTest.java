package io.floci.testcontainers;

import org.junit.jupiter.api.Test;

import static io.floci.testcontainers.core.testing.DockerSocketInspector.configsRequiringDockerSocket;
import static io.floci.testcontainers.core.testing.DockerSocketInspector.serviceConfigs;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the {@link FlociContainer#DESCRIPTOR} and the Docker socket services against the reference of the
 * specification, which the other language ports check their tables against.
 */
class FlociContainerDescriptorTest {

    @Test
    void shouldDescribeFloci() {
        assertThat(FlociContainer.DESCRIPTOR.getName()).isEqualTo("aws");
        assertThat(FlociContainer.DESCRIPTOR.getImage()).isEqualTo("floci/floci");
        assertThat(FlociContainer.DESCRIPTOR.getPort()).isEqualTo(4566);
        assertThat(FlociContainer.DESCRIPTOR.getEnvPrefix()).isEqualTo("FLOCI_");
        assertThat(FlociContainer.DESCRIPTOR.getHealthPath()).isEqualTo("/_floci/health");
        assertThat(FlociContainer.DESCRIPTOR.getResetPath()).isEqualTo("/_floci/state/reset");
        assertThat(FlociContainer.DESCRIPTOR.getLogLevelEnvVar()).isEqualTo("QUARKUS_LOG_CATEGORY__IO_GITHUB_HECTORVENT__LEVEL");
        assertThat(FlociContainer.DESCRIPTOR.getNetworkEnvVar()).isEqualTo("FLOCI_SERVICES_DOCKER_NETWORK");
        assertThat(FlociContainer.DESCRIPTOR.getResourceNamespaceEnvVar()).isEqualTo("FLOCI_DOCKER_RESOURCE_NAMESPACE");
        assertThat(FlociContainer.DESCRIPTOR.getTlsHttpsPortEnvVar()).isEqualTo("FLOCI_TLS_AWS_HTTPS_PORT");
        assertThat(FlociContainer.DESCRIPTOR.getDefaultEnv()).isEmpty();
    }

    @Test
    void shouldRequireDockerSocketForReferenceServicesByDefault() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(configsRequiringDockerSocket(container)).containsExactlyInAnyOrder(
                    "AmazonMqConfig", "AppSyncConfig", "AthenaConfig", "CodeArtifactConfig",
                    "CodeBuildConfig", "DocumentDbConfig", "Ec2Config", "EcrConfig", "EcsConfig", "EksConfig",
                    "ElastiCacheConfig", "KinesisAnalyticsConfig", "LambdaConfig", "MemoryDbConfig", "MskConfig",
                    "MwaaConfig", "NeptuneConfig", "OpenSearchConfig", "PipesConfig", "RdsConfig", "RedshiftConfig",
                    "SageMakerConfig", "TimestreamInfluxDbConfig", "VerifiedPermissionsConfig");
        }
    }

    @Test
    void shouldRequireDockerSocketForBatchOnlyInDockerRunnerMode() {
        try (FlociContainer container = new FlociContainer().withBatchConfig(c -> c.runnerMode("docker"))) {
            assertThat(configsRequiringDockerSocket(container)).contains("BatchConfig");
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenAllServicesAreDisabled() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            assertThat(serviceConfigs(container)).hasSizeGreaterThan(100);
            assertThat(configsRequiringDockerSocket(container)).isEmpty();
        }
    }
}
