package io.floci.testcontainers;

import io.floci.testcontainers.config.services.AbstractServiceConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.event.Level;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.TransferableCopyInspector;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlociContainerTest {

    @Test
    void shouldCreateContainerWithDefaultImage() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getDockerImageName()).isEqualTo("floci/floci:latest");
        }
    }

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new FlociContainer(DockerImageName.parse("other/image:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldReturnDefaultRegion() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getRegion()).isEqualTo("us-east-1");
        }
    }

    @Test
    void shouldReturnCustomRegion() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRegion("eu-west-1");
            assertThat(container.getRegion()).isEqualTo("eu-west-1");
        }
    }

    @Test
    void shouldReturnDefaultAvailabilityZone() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getDefaultAvailabilityZone()).isEqualTo("us-east-1a");
        }
    }

    @Test
    void shouldReturnCustomAvailabilityZone() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDefaultAvailabilityZone("eu-west-1a");
            assertThat(container.getDefaultAvailabilityZone()).isEqualTo("eu-west-1a");
        }
    }

    @Test
    void shouldReturnDefaultAccountId() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getDefaultAccountId()).isEqualTo("000000000000");
        }
    }

    @Test
    void shouldReturnCustomAccountId() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDefaultAccountId("123456789012");
            assertThat(container.getDefaultAccountId()).isEqualTo("123456789012");
        }
    }

    @Test
    void shouldReturnDefaultCredentials() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getAccessKey()).isEqualTo("test");
            assertThat(container.getSecretKey()).isEqualTo("test");
        }
    }

    @Test
    void shouldNotConfigureAiMockConfigByDefault() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getAiMockConfigFile()).isEmpty();
            assertThat(container.getAiMockConfig()).isEmpty();
            assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_AI_MOCK_CONFIG_FILE");
        }
    }

    @Test
    void shouldUseExplicitAiMockConfigFilePath() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAiMockConfigFile("/etc/floci/AiMockConfig.json");

            assertThat(container.getAiMockConfigFile()).contains("/etc/floci/AiMockConfig.json");
            assertThat(container.getAiMockConfig()).isEmpty();
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_AI_MOCK_CONFIG_FILE", "/etc/floci/AiMockConfig.json");
            assertThat(TransferableCopyInspector.pendingCopies(container)).isEmpty();
        }
    }

    @Test
    void shouldCopyAiMockConfigContentIntoContainer() {
        String fileContent = "{\"Textract\":{},\"Comprehend\":{},\"Rekognition\":{}}";

        try (FlociContainer container = new FlociContainer()) {
            container.withAiMockConfig(fileContent);

            String containerPath = container.getAiMockConfigFile().orElseThrow();
            assertThat(containerPath).startsWith("/tmp/floci-ai-mock-config-").endsWith(".json");
            assertThat(container.getAiMockConfig()).contains(fileContent);
            assertThat(container.getEnvMap()).containsEntry("FLOCI_AI_MOCK_CONFIG_FILE", containerPath);
            assertThat(TransferableCopyInspector.contentCopiedTo(container, containerPath)).contains(fileContent);
        }
    }

    @Test
    void shouldClearAiMockConfigContentWhenExplicitPathIsSet() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAiMockConfig("{}").withAiMockConfigFile("/etc/floci/AiMockConfig.json");

            assertThat(container.getAiMockConfig()).isEmpty();
            assertThat(container.getAiMockConfigFile()).contains("/etc/floci/AiMockConfig.json");
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_AI_MOCK_CONFIG_FILE", "/etc/floci/AiMockConfig.json");
        }
    }

    @Test
    void shouldClearExplicitAiMockConfigPathWhenContentIsSet() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAiMockConfigFile("/etc/floci/AiMockConfig.json").withAiMockConfig("{}");

            assertThat(container.getAiMockConfig()).contains("{}");
            String containerPath = container.getAiMockConfigFile().orElseThrow();
            assertThat(containerPath).startsWith("/tmp/floci-ai-mock-config-");
            assertThat(container.getEnvMap()).containsEntry("FLOCI_AI_MOCK_CONFIG_FILE", containerPath);
        }
    }

    @Test
    void shouldExposeFlociPort() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getExposedPorts()).contains(FlociContainer.PORT);
        }
    }

    @Test
    void shouldConfigureRdsEndpointHost() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", container.getHost());
        }
    }

    @Test
    void shouldPreserveConfiguredRdsEndpointHostWhenDisablingAllServices() {
        try (FlociContainer container = new FlociContainer()
                .withRdsConfig(config -> config.endpointHost("rds.example.com"))) {
            container.disableAllServices();

            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_SERVICES_RDS_ENDPOINT_HOST", "rds.example.com");
        }
    }

    @Test
    void shouldReturnDefaultLogLevel() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);
        }
    }

    @Test
    void shouldReturnCustomLogLevel() {
        try (FlociContainer container = new FlociContainer()) {
            container.withLogLevel(Level.DEBUG);
            assertThat(container.getLogLevel()).isEqualTo(Level.DEBUG);
        }
    }

    @Test
    void shouldFallbackToWarnForInvalidLogLevel() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEnv("QUARKUS_LOG_CATEGORY__IO_GITHUB_HECTORVENT__LEVEL", "INVALID");
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);
        }
    }

    @Test
    void shouldConfigureDedicatedNetwork() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDedicatedNetwork();

            String networkName = container.getDedicatedNetworkName();
            assertThat(networkName).startsWith("floci-network-");
            assertThat(networkName).hasSize("floci-network-".length() + 8);
            assertThat(container.getNetwork()).isNotNull();
        }
    }

    @Test
    void shouldCreateUniqueNetworkPerCall() {
        try (FlociContainer container1 = new FlociContainer();
             FlociContainer container2 = new FlociContainer()) {
            container1.withDedicatedNetwork();
            container2.withDedicatedNetwork();

            String network1 = container1.getDedicatedNetworkName();
            String network2 = container2.getDedicatedNetworkName();
            assertThat(network1).isNotEqualTo(network2);
        }
    }

    @Test
    void shouldWaitForStartScriptsToComplete() throws Exception {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.withCopyToContainer(Transferable.of("""
                    #!/bin/sh
                    set -eu
                    sleep 2
                    touch /tmp/floci-start-script-completed
                    """, 0777), "/etc/floci/init/start.d/01-slow-start.sh");

            container.start();

            assertThat(container.execInContainer("test", "-f", "/tmp/floci-start-script-completed")
                    .getExitCode()).isZero();
        }
    }

    @Test
    void shouldRunShutdownHooksWhenStopped(@TempDir Path temporaryDirectory) {
        Path shutdownMarker = temporaryDirectory.resolve("completed");
        try (FlociContainer container = new FlociContainer().disableAllServices()
                .withFileSystemBind(temporaryDirectory.toString(), "/tmp/floci-shutdown", BindMode.READ_WRITE)) {
            container.withCopyToContainer(Transferable.of("""
                    #!/bin/sh
                    set -eu
                    touch /tmp/floci-shutdown/completed
                    """, 0777), "/etc/floci/init/shutdown.d/01-mark-shutdown.sh");

            container.start();
        }

        assertThat(shutdownMarker).exists();
    }

    @Test
    void shouldDeleteContainerOwnedPersistentStorage() throws Exception {
        try (FlociContainer container = new FlociContainer().disableAllServices()
                .withStorageConfig(c -> c.randomHostPersistentPath())) {
            Path persistentStorage = container.getStorageConfig().getHostPersistentPath().orElseThrow();
            try {
                container.start();
                var result = container.execInContainer(
                        "sh", "-c", "mkdir -p /app/data/restricted && touch /app/data/restricted/file && chmod 000 /app/data/restricted");
                assertThat(result.getExitCode()).isZero();
            } finally {
                container.stop();
            }
            assertThat(persistentStorage).doesNotExist();
        }
    }

    @Test
    void shouldDisableAllServices() {
        try (FlociContainer container = new FlociContainer()) {
            container.disableAllServices();

            assertThat(List.of(
                    container.getAcmConfig(),
                    container.getApiGatewayConfig(),
                    container.getApiGatewayV2Config(),
                    container.getAppConfigConfig(),
                    container.getAppConfigDataConfig(),
                    container.getAppSyncConfig(),
                    container.getCloudFormationConfig(),
                    container.getCloudFrontConfig(),
                    container.getCloudMapConfig(),
                    container.getCloudWatchLogsConfig(),
                    container.getCloudWatchMetricsConfig(),
                    container.getCognitoConfig(),
                    container.getConfigServiceConfig(),
                    container.getDynamoDbConfig(),
                    container.getEc2Config(),
                    container.getEcrConfig(),
                    container.getEcsConfig(),
                    container.getElastiCacheConfig(),
                    container.getEventBridgeConfig(),
                    container.getIamConfig(),
                    container.getKinesisConfig(),
                    container.getKmsConfig(),
                    container.getLambdaConfig(),
                    container.getOpenSearchConfig(),
                    container.getRdsConfig(),
                    container.getS3Config(),
                    container.getSchedulerConfig(),
                    container.getSecretsManagerConfig(),
                    container.getSesConfig(),
                    container.getSnsConfig(),
                    container.getSqsConfig(),
                    container.getSsmConfig(),
                    container.getStepFunctionsConfig(),
                    container.getMskConfig(),
                    container.getFirehoseConfig(),
                    container.getAthenaConfig(),
                    container.getGlueConfig(),
                    container.getResourceGroupsTaggingConfig(),
                    container.getBedrockAgentCoreConfig(),
                    container.getBedrockAgentCoreControlConfig(),
                    container.getBedrockRuntimeConfig(),
                    container.getPipesConfig(),
                    container.getEksConfig(),
                    container.getCodeBuildConfig(),
                    container.getCodeDeployConfig(),
                    container.getElbV2Config(),
                    container.getBackupConfig(),
                    container.getTransferFamilyConfig(),
                    container.getRoute53Config(),
                    container.getTextractConfig(),
                    container.getPricingConfig(),
                    container.getNeptuneConfig(),
                    container.getCostExplorerConfig(),
                    container.getCurConfig(),
                    container.getBcmDataExportsConfig(),
                    container.getCloudTrailConfig(),
                    container.getBatchConfig(),
                    container.getRdsDataConfig(),
                    container.getDocumentDbConfig(),
                    container.getEmrConfig(),
                    container.getWafV2Config(),
                    container.getIotConfig(),
                    container.getIotDataConfig(),
                    container.getLightsailConfig(),
                    container.getCloudControlConfig(),
                    container.getS3VectorsConfig(),
                    container.getElasticBeanstalkConfig(),
                    container.getCodePipelineConfig(),
                    container.getAmazonMqConfig(),
                    container.getMemoryDbConfig(),
                    container.getRumConfig(),
                    container.getS3TablesConfig(),
                    container.getApplicationAutoScalingConfig(),
                    container.getSwfConfig(),
                    container.getKinesisAnalyticsConfig(),
                    container.getMwaaConfig(),
                    container.getGuardDutyConfig(),
                    container.getCloudHsmV2Config(),
                    container.getEmrServerlessConfig(),
                    container.getFisConfig(),
                    container.getConnectConfig(),
                    container.getSsoAdminConfig(),
                    container.getApsConfig(),
                    container.getCodeGuruReviewerConfig(),
                    container.getControlTowerConfig(),
                    container.getRoute53ResolverConfig(),
                    container.getNetworkFirewallConfig(),
                    container.getServiceCatalogConfig(),
                    container.getServiceQuotasConfig(),
                    container.getRamConfig(),
                    container.getLakeFormationConfig(),
                    container.getEfsConfig(),
                    container.getResourceExplorer2Config(),
                    container.getRedshiftConfig(),
                    container.getOrganizationsConfig(),
                    container.getComprehendConfig(),
                    container.getRekognitionConfig(),
                    container.getTranscribeConfig()
            )).noneMatch(AbstractServiceConfig::isEnabled);
        }
    }

    @Test
    void shouldRequireDockerSocketByDefault() {
        try (FlociContainer container = new FlociContainer()) {
            container.configure();

            assertThat(container.getBinds())
                    .anyMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldNotBindDockerSocketWhenNoDockerBackedServiceIsEnabled() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.configure();
            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldBindDockerSocketOnlyOnceWhenConfiguredRepeatedly() {
        try (FlociContainer container = new FlociContainer()) {
            container.configure();
            container.configure();

            assertThat(container.getBinds())
                    .filteredOn(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()))
                    .hasSize(1);
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenNoDockerBackedServiceIsEnabled() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.withS3Config(c -> c.enabled(true))
                    .configure();

            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenDockerBackedServiceIsMocked() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.withRdsConfig(c -> c.enabled(true).mock(true))
                    .configure();

            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldRequireDockerSocketWhenDockerBackedServiceIsEnabledAndNotMocked() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.withRdsConfig(c -> c.enabled(true).mock(false))
                    .configure();

            assertThat(container.getBinds())
                    .anyMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldOverrideAutoDetectionWhenDockerSocketExplicitlyEnabled() {
        try (FlociContainer container = new FlociContainer().disableAllServices()) {
            container.withDockerSocket(true)
                    .configure();

            assertThat(container.getBinds())
                    .anyMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldOverrideAutoDetectionWhenDockerSocketExplicitlyDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDockerSocket(false)
                    .configure();

            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldUnbindDockerSocketWhenConfigChangesAfterRestart() {
        try (FlociContainer container = new FlociContainer()) {
            container.configure();

            assertThat(container.getBinds())
                    .anyMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));

            container.disableAllServices().configure();

            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldBindDockerSocketWhenConfigChangesAfterRestart() {
        try (FlociContainer container = new FlociContainer()) {
            container.disableAllServices().configure();

            assertThat(container.getBinds())
                    .noneMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));

            container.withDockerSocket(true).configure();

            assertThat(container.getBinds())
                    .anyMatch(b -> "/var/run/docker.sock".equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldMountFilesIntoContainer() {
        // A service config can mount a generated file into the container via
        // applyFileMountsToContainer(); the Step Functions mock config file is used here only as a
        // concrete example of that mechanism.
        String fileContent = "{\"StateMachines\":{},\"MockedResponses\":{}}";

        try (FlociContainer container = new FlociContainer()
                .withStepFunctionsConfig(c -> c.mockConfig(fileContent))) {

            String containerPath = container.getStepFunctionsConfig().getMockConfigFile().orElseThrow();

            assertThat(TransferableCopyInspector.contentCopiedTo(container, containerPath)).contains(fileContent);

            // File mounts survive later, unrelated service-config changes.
            container.withS3Config(c -> c.enabled(true));
            assertThat(TransferableCopyInspector.contentCopiedTo(container, containerPath)).contains(fileContent);
        }
    }
}
