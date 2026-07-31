package io.floci.testcontainers;

import io.floci.testcontainers.config.services.AbstractServiceConfig;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
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
                    container.getCloudMapConfig(),
                    container.getCloudWatchLogsConfig(),
                    container.getCloudWatchMetricsConfig(),
                    container.getCognitoConfig(),
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
                    container.getMemoryDbConfig()
            )).noneMatch(AbstractServiceConfig::isEnabled);
        }
    }


}
