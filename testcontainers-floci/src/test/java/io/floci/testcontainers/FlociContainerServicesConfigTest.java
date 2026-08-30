package io.floci.testcontainers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerServicesConfigTest {

    @Test
    void shouldStoreAcmConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAcmConfig(c -> c.validationWaitSeconds(5));

            assertThat(container.getAcmConfig().getValidationWaitSeconds()).isEqualTo(5);
        }
    }

    @Test
    void shouldStoreApiGatewayConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withApiGatewayConfig(c -> c.enabled(false));

            assertThat(container.getApiGatewayConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreApiGatewayV2ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withApiGatewayV2Config(c -> c.enabled(false));

            assertThat(container.getApiGatewayV2Config().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreAppConfigConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAppConfigConfig(c -> c.enabled(false));

            assertThat(container.getAppConfigConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreAppConfigDataConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAppConfigDataConfig(c -> c.enabled(false));

            assertThat(container.getAppConfigDataConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreAppSyncConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAppSyncConfig(c -> c.enabled(false));

            assertThat(container.getAppSyncConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCloudFormationConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudFormationConfig(c -> c.enabled(false));

            assertThat(container.getCloudFormationConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCloudMapConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudMapConfig(c -> c.operationCompletionDelaySeconds(5));

            assertThat(container.getCloudMapConfig().getOperationCompletionDelaySeconds()).isEqualTo(5);
        }
    }

    @Test
    void shouldStoreCloudFrontConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudFrontConfig(c -> c.domainSuffix("example.cloudfront.net"));

            assertThat(container.getCloudFrontConfig().getDomainSuffix()).isEqualTo("example.cloudfront.net");
        }
    }

    @Test
    void shouldStoreCloudWatchLogsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudWatchLogsConfig(c -> c.maxEventsPerQuery(5000));

            assertThat(container.getCloudWatchLogsConfig().getMaxEventsPerQuery()).isEqualTo(5000);
        }
    }

    @Test
    void shouldStoreCloudWatchMetricsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudWatchMetricsConfig(c -> c.enabled(false));

            assertThat(container.getCloudWatchMetricsConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCognitoConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCognitoConfig(c -> c.enabled(false));

            assertThat(container.getCognitoConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreConfigServiceConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withConfigServiceConfig(c -> c.enabled(false));

            assertThat(container.getConfigServiceConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreDynamoDbConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDynamoDbConfig(c -> c.enabled(false));

            assertThat(container.getDynamoDbConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreEc2ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEc2Config(c -> c.enabled(false));

            assertThat(container.getEc2Config().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreEventBridgeConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEventBridgeConfig(c -> c.enabled(false));

            assertThat(container.getEventBridgeConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreIamConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withIamConfig(c -> c.enabled(false));

            assertThat(container.getIamConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreKinesisConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withKinesisConfig(c -> c.enabled(false));

            assertThat(container.getKinesisConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreKmsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withKmsConfig(c -> c.enabled(false));

            assertThat(container.getKmsConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreLambdaConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withLambdaConfig(c -> c
                    .defaultMemoryMb(512)
                    .ephemeral(true));

            assertThat(container.getLambdaConfig().getDefaultMemoryMb()).isEqualTo(512);
            assertThat(container.getLambdaConfig().isEphemeral()).isTrue();
        }
    }

    @Test
    void shouldStoreRdsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRdsConfig(c -> c
                    .defaultPostgresImage("postgres:15")
                    .proxyPortRange(8000, 100));

            assertThat(container.getRdsConfig().getDefaultPostgresImage()).isEqualTo("postgres:15");
            assertThat(container.getRdsConfig().getProxyBasePort()).isEqualTo(8000);
            assertThat(container.getRdsConfig().getProxyPortsCount()).isEqualTo(100);
        }
    }

    @Test
    void shouldStoreS3ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withS3Config(c -> c.defaultPresignExpirySeconds(7200).enforceAuth(true));

            assertThat(container.getS3Config().getDefaultPresignExpirySeconds()).isEqualTo(7200);
            assertThat(container.getS3Config().isEnforceAuth()).isTrue();
        }
    }

    @Test
    void shouldStoreSchedulerConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSchedulerConfig(c -> c.enabled(false));

            assertThat(container.getSchedulerConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreSecretsManagerConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSecretsManagerConfig(c -> c.defaultRecoveryWindowDays(7));

            assertThat(container.getSecretsManagerConfig().getDefaultRecoveryWindowDays()).isEqualTo(7);
        }
    }

    @Test
    void shouldStoreSesConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSesConfig(c -> c.enabled(false));

            assertThat(container.getSesConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreSnsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSnsConfig(c -> c.enabled(false));

            assertThat(container.getSnsConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreSqsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSqsConfig(c -> c
                    .defaultVisibilityTimeout(60)
                    .maxMessageSize(131072));

            assertThat(container.getSqsConfig().getDefaultVisibilityTimeout()).isEqualTo(60);
            assertThat(container.getSqsConfig().getMaxMessageSize()).isEqualTo(131072);
        }
    }

    @Test
    void shouldStoreSsmConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSsmConfig(c -> c.maxParameterHistory(10));

            assertThat(container.getSsmConfig().getMaxParameterHistory()).isEqualTo(10);
        }
    }

    @Test
    void shouldStorePricingConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withPricingConfig(c -> c.snapshotPath("/data/pricing"));

            assertThat(container.getPricingConfig().getSnapshotPath()).contains("/data/pricing");
        }
    }

    @Test
    void shouldStoreAthenaConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAthenaConfig(c -> c.mock(true));

            assertThat(container.getAthenaConfig().isMock()).isTrue();
        }
    }

    @Test
    void shouldStoreBackupConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBackupConfig(c -> c.jobCompletionDelaySeconds(5));

            assertThat(container.getBackupConfig().getJobCompletionDelaySeconds()).isEqualTo(5);
        }
    }

    @Test
    void shouldStoreBedrockAgentCoreConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBedrockAgentCoreConfig(c -> c.validateRuntimeExists(true));

            assertThat(container.getBedrockAgentCoreConfig().isValidateRuntimeExists()).isTrue();
        }
    }

    @Test
    void shouldStoreBedrockAgentCoreControlConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBedrockAgentCoreControlConfig(c -> c.enabled(false));

            assertThat(container.getBedrockAgentCoreControlConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreBedrockRuntimeConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBedrockRuntimeConfig(c -> c.enabled(false));

            assertThat(container.getBedrockRuntimeConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCodeBuildConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCodeBuildConfig(c -> c.dockerNetwork("my-network"));

            assertThat(container.getCodeBuildConfig().getDockerNetwork()).isEqualTo("my-network");
        }
    }

    @Test
    void shouldStoreCodeDeployConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCodeDeployConfig(c -> c.enabled(false));

            assertThat(container.getCodeDeployConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreEcrConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEcrConfig(c -> c
                    .registryPortRange(5200, 5)
                    .registryImage("registry:3"));

            assertThat(container.getEcrConfig().getRegistryBasePort()).isEqualTo(5200);
            assertThat(container.getEcrConfig().getRegistryPortsCount()).isEqualTo(5);
            assertThat(container.getEcrConfig().getRegistryImage()).isEqualTo("registry:3");
        }
    }

    @Test
    void shouldStoreEcsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEcsConfig(c -> c
                    .mock(true)
                    .defaultMemoryMb(1024));

            assertThat(container.getEcsConfig().isMock()).isTrue();
            assertThat(container.getEcsConfig().getDefaultMemoryMb()).isEqualTo(1024);
        }
    }

    @Test
    void shouldStoreEksConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEksConfig(c -> c
                    .mock(true)
                    .apiServerPortRange(6600, 5));

            assertThat(container.getEksConfig().isMock()).isTrue();
            assertThat(container.getEksConfig().getApiServerBasePort()).isEqualTo(6600);
            assertThat(container.getEksConfig().getApiServerPortsCount()).isEqualTo(5);
        }
    }

    @Test
    void shouldStoreElastiCacheConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withElastiCacheConfig(c -> c
                    .proxyPortRange(7000, 5)
                    .defaultImage("valkey/valkey:9"));

            assertThat(container.getElastiCacheConfig().getProxyBasePort()).isEqualTo(7000);
            assertThat(container.getElastiCacheConfig().getProxyPortsCount()).isEqualTo(5);
            assertThat(container.getElastiCacheConfig().getDefaultImage()).isEqualTo("valkey/valkey:9");
        }
    }

    @Test
    void shouldStoreElbV2ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withElbV2Config(c -> c
                    .mock(true)
                    .listenerPort(8080));

            assertThat(container.getElbV2Config().isMock()).isTrue();
            assertThat(container.getElbV2Config().getListenerPorts()).containsExactly(8080);
        }
    }

    @Test
    void shouldStoreFirehoseConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withFirehoseConfig(c -> c.enabled(false));

            assertThat(container.getFirehoseConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreGlueConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withGlueConfig(c -> c.enabled(false));

            assertThat(container.getGlueConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreMskConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withMskConfig(c -> c
                    .mock(true)
                    .defaultImage("redpandadata/redpanda:v24")
                    .kafkaHostPortRange(9500, 20));

            assertThat(container.getMskConfig().isMock()).isTrue();
            assertThat(container.getMskConfig().getDefaultImage()).isEqualTo("redpandadata/redpanda:v24");
            assertThat(container.getMskConfig().getKafkaHostPortBase()).isEqualTo(9500);
            assertThat(container.getMskConfig().getKafkaHostPortsCount()).isEqualTo(20);
            assertThat(container.getMskConfig().getKafkaHostPortMax()).isEqualTo(9519);
        }
    }

    @Test
    void shouldStoreOpenSearchConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withOpenSearchConfig(c -> c
                    .mock(true)
                    .proxyPortRange(9500, 5));

            assertThat(container.getOpenSearchConfig().isMock()).isTrue();
            assertThat(container.getOpenSearchConfig().getProxyBasePort()).isEqualTo(9500);
            assertThat(container.getOpenSearchConfig().getProxyPortsCount()).isEqualTo(5);
        }
    }

    @Test
    void shouldStorePipesConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withPipesConfig(c -> c.enabled(false));

            assertThat(container.getPipesConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreResourceGroupsTaggingConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withResourceGroupsTaggingConfig(c -> c.enabled(false));

            assertThat(container.getResourceGroupsTaggingConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreRoute53ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRoute53Config(c -> c.defaultNameserver1("ns1.example.com"));

            assertThat(container.getRoute53Config().getDefaultNameserver1()).isEqualTo("ns1.example.com");
        }
    }

    @Test
    void shouldStoreStepFunctionsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withStepFunctionsConfig(c -> c.enabled(false));

            assertThat(container.getStepFunctionsConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreTextractConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withTextractConfig(c -> c.enabled(false));

            assertThat(container.getTextractConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreTransferFamilyConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withTransferFamilyConfig(c -> c.enabled(false));

            assertThat(container.getTransferFamilyConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreNeptuneConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withNeptuneConfig(c -> c
                    .proxyPortRange(9000, 51)
                    .dbType("neo4j")
                    .defaultImage("tinkerpop/gremlin-server:3.8.0")
                    .defaultNeo4jImage("neo4j:5.24-community"));

            assertThat(container.getNeptuneConfig().getProxyBasePort()).isEqualTo(9000);
            assertThat(container.getNeptuneConfig().getProxyPortsCount()).isEqualTo(51);
            assertThat(container.getNeptuneConfig().getProxyMaxPort()).isEqualTo(9050);
            assertThat(container.getNeptuneConfig().getDbType()).isEqualTo("neo4j");
            assertThat(container.getNeptuneConfig().getDefaultImage()).isEqualTo("tinkerpop/gremlin-server:3.8.0");
            assertThat(container.getNeptuneConfig().getDefaultNeo4jImage()).isEqualTo("neo4j:5.24-community");
        }
    }

    @Test
    void shouldStoreCostExplorerConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCostExplorerConfig(c -> c.creditUsdMonthly(250.0));

            assertThat(container.getCostExplorerConfig().getCreditUsdMonthly()).isEqualTo(250.0);
        }
    }

    @Test
    void shouldStoreCurConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCurConfig(c -> c
                    .emitMode("daily")
                    .stagingBucket("my-staging"));

            assertThat(container.getCurConfig().getEmitMode()).isEqualTo("daily");
            assertThat(container.getCurConfig().getStagingBucket()).isEqualTo("my-staging");
        }
    }

    @Test
    void shouldStoreCloudTrailConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudTrailConfig(c -> c.enabled(false));

            assertThat(container.getCloudTrailConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreBcmDataExportsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBcmDataExportsConfig(c -> c.emitMode("off"));

            assertThat(container.getBcmDataExportsConfig().getEmitMode()).isEqualTo("off");
        }
    }

    @Test
    void shouldStoreBatchConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withBatchConfig(c -> c
                    .runnerMode("deferred")
                    .dockerNetwork("my-batch-network"));

            assertThat(container.getBatchConfig().getRunnerMode()).isEqualTo("deferred");
            assertThat(container.getBatchConfig().getDockerNetwork()).isEqualTo("my-batch-network");
        }
    }

    @Test
    void shouldStoreRdsDataConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRdsDataConfig(c -> c.transactionTtlSeconds(300));

            assertThat(container.getRdsDataConfig().getTransactionTtlSeconds()).isEqualTo(300);
        }
    }

    @Test
    void shouldStoreDocumentDbConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDocumentDbConfig(c -> c
                    .mock(true)
                    .defaultImage("mongo:8.0")
                    .dockerNetwork("my-docdb-network"));

            assertThat(container.getDocumentDbConfig().isMock()).isTrue();
            assertThat(container.getDocumentDbConfig().getDefaultImage()).isEqualTo("mongo:8.0");
            assertThat(container.getDocumentDbConfig().getDockerNetwork()).isEqualTo("my-docdb-network");
        }
    }

    @Test
    void shouldStoreEmrConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEmrConfig(c -> c
                    .defaultReleaseLabel("emr-7.8.0")
                    .clusterStartupDelaySeconds(10));

            assertThat(container.getEmrConfig().getDefaultReleaseLabel()).isEqualTo("emr-7.8.0");
            assertThat(container.getEmrConfig().getClusterStartupDelaySeconds()).isEqualTo(10);
        }
    }

    @Test
    void shouldStoreWafV2ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withWafV2Config(c -> c.enabled(false));

            assertThat(container.getWafV2Config().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreIotConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withIotConfig(c -> c
                    .mqttAutoStart(true)
                    .mqttHost("127.0.0.1")
                    .mqttPort(18830));

            assertThat(container.getIotConfig().isMqttAutoStart()).isTrue();
            assertThat(container.getIotConfig().getMqttHost()).isEqualTo("127.0.0.1");
            assertThat(container.getIotConfig().getMqttPort()).isEqualTo(18830);
        }
    }

    @Test
    void shouldStoreIotDataConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withIotDataConfig(c -> c.enabled(false));

            assertThat(container.getIotDataConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreLightsailConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withLightsailConfig(c -> c.enabled(false));

            assertThat(container.getLightsailConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCloudControlConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudControlConfig(c -> c.enabled(false));

            assertThat(container.getCloudControlConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreS3VectorsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withS3VectorsConfig(c -> c.enabled(false));

            assertThat(container.getS3VectorsConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreElasticBeanstalkConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withElasticBeanstalkConfig(c -> c.enabled(false));

            assertThat(container.getElasticBeanstalkConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCodePipelineConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCodePipelineConfig(c -> c.enabled(false));

            assertThat(container.getCodePipelineConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreAmazonMqConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAmazonMqConfig(c -> c
                    .mock(true)
                    .defaultImage("rabbitmq:4-management"));

            assertThat(container.getAmazonMqConfig().isMock()).isTrue();
            assertThat(container.getAmazonMqConfig().getDefaultImage()).isEqualTo("rabbitmq:4-management");
        }
    }

    @Test
    void shouldStoreMemoryDbConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withMemoryDbConfig(c -> c
                    .mock(true)
                    .proxyPortRange(7000, 20)
                    .defaultImage("valkey/valkey:8.1"));

            assertThat(container.getMemoryDbConfig().isMock()).isTrue();
            assertThat(container.getMemoryDbConfig().getProxyBasePort()).isEqualTo(7000);
            assertThat(container.getMemoryDbConfig().getProxyMaxPort()).isEqualTo(7019);
            assertThat(container.getMemoryDbConfig().getProxyPortsCount()).isEqualTo(20);
            assertThat(container.getMemoryDbConfig().getDefaultImage()).isEqualTo("valkey/valkey:8.1");
        }
    }

    @Test
    void shouldStoreRumConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRumConfig(c -> c.enabled(false));

            assertThat(container.getRumConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreS3TablesConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withS3TablesConfig(c -> c.enabled(false));

            assertThat(container.getS3TablesConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreApplicationAutoScalingConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withApplicationAutoScalingConfig(c -> c.enabled(false));

            assertThat(container.getApplicationAutoScalingConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreSwfConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSwfConfig(c -> c
                    .timeoutSweepEnabled(false)
                    .timeoutSweepIntervalSeconds(5));

            assertThat(container.getSwfConfig().isTimeoutSweepEnabled()).isFalse();
            assertThat(container.getSwfConfig().getTimeoutSweepIntervalSeconds()).isEqualTo(5);
        }
    }

    @Test
    void shouldStoreKinesisAnalyticsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withKinesisAnalyticsConfig(c -> c
                    .mock(true)
                    .defaultImage("apache/flink:1.20"));

            assertThat(container.getKinesisAnalyticsConfig().isMock()).isTrue();
            assertThat(container.getKinesisAnalyticsConfig().getDefaultImage()).contains("apache/flink:1.20");
        }
    }

    @Test
    void shouldStoreMwaaConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withMwaaConfig(c -> c
                    .mock(true)
                    .proxyPortRange(9000, 100)
                    .defaultVersion("2.10.5"));

            assertThat(container.getMwaaConfig().isMock()).isTrue();
            assertThat(container.getMwaaConfig().getProxyBasePort()).isEqualTo(9000);
            assertThat(container.getMwaaConfig().getProxyMaxPort()).isEqualTo(9099);
            assertThat(container.getMwaaConfig().getProxyPortsCount()).isEqualTo(100);
            assertThat(container.getMwaaConfig().getDefaultVersion()).isEqualTo("2.10.5");
        }
    }

    @Test
    void shouldStoreGuardDutyConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withGuardDutyConfig(c -> c.enabled(false));

            assertThat(container.getGuardDutyConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreCloudHsmV2ConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withCloudHsmV2Config(c -> c.enabled(false));

            assertThat(container.getCloudHsmV2Config().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreEmrServerlessConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEmrServerlessConfig(c -> c.enabled(false));

            assertThat(container.getEmrServerlessConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreFisConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withFisConfig(c -> c.enabled(false));

            assertThat(container.getFisConfig().isEnabled()).isFalse();
        }
    }

    @Test
    void shouldStoreDuckDbConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withDuckDbConfig(c -> c
                    .url("http://custom-duckdb:8080")
                    .defaultImage("floci/floci-duck:1.5.18"));

            assertThat(container.getDuckDbConfig().getUrl()).isEqualTo("http://custom-duckdb:8080");
            assertThat(container.getDuckDbConfig().getDefaultImage()).isEqualTo("floci/floci-duck:1.5.18");
        }
    }

    @Test
    void shouldStoreSecurityConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withSecurityConfig(c -> c.disableCorsHeaders(true));

            assertThat(container.getSecurityConfig().isDisableCorsHeaders()).isTrue();
        }
    }

    @Test
    void shouldStoreProtocolsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withProtocolsConfig(c -> c.strictClaiming(true));

            assertThat(container.getProtocolsConfig().isStrictClaiming()).isTrue();
        }
    }

    @Test
    void shouldStoreAuthConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withAuthConfig(c -> c.validateSignatures(true).presignSecret("custom-secret"));

            assertThat(container.getAuthConfig().isValidateSignatures()).isTrue();
            assertThat(container.getAuthConfig().getPresignSecret()).isEqualTo("custom-secret");
        }
    }

    @Test
    void shouldStoreInitHooksConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withInitHooksConfig(c -> c
                    .shellExecutable("/bin/bash")
                    .shutdownGracePeriodSeconds(5)
                    .timeoutSeconds(60));

            assertThat(container.getInitHooksConfig().getShellExecutable()).isEqualTo("/bin/bash");
            assertThat(container.getInitHooksConfig().getShutdownGracePeriodSeconds()).isEqualTo(5);
            assertThat(container.getInitHooksConfig().getTimeoutSeconds()).isEqualTo(60);
        }
    }

    @Test
    void shouldStoreConnectConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withConnectConfig(c -> c.enabled(false));

            assertThat(container.getConnectConfig().isEnabled()).isFalse();
        }
    }

}
