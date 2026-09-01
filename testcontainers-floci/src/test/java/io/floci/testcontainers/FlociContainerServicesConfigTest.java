package io.floci.testcontainers;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every configuration exposed by {@link FlociContainer} is actually picked up by the
 * container: the changed value survives the {@code with*Config(...)} round-trip, the matching
 * {@code FLOCI_*} environment variable is applied, and the container's port mappings are wired.
 *
 * <p>There is exactly one test method per service config class in {@code config/services/} plus one
 * per cross-cutting config class in {@code config/}. When adding a new service, add a method here —
 * see the "FlociContainerServicesConfigTest" note in {@code AGENTS.md} for the exact recipe.
 */
class FlociContainerServicesConfigTest {

    /**
     * Applies {@code configurer} to a fresh {@link FlociContainer}, then asserts that:
     * <ol>
     *   <li>the changed property is readable again via {@code get*Config()}
     *       ({@code actualValue} equals {@code expectedValue}),</li>
     *   <li>the container has the environment variable {@code envKey=envValue}, and</li>
     *   <li>the container exposes {@code exposedPort}.</li>
     * </ol>
     */
    private static void assertConfigWired(
            Consumer<FlociContainer> configurer,
            Function<FlociContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue,
            int exposedPort) {
        try (FlociContainer container = new FlociContainer()) {
            configurer.accept(container);

            assertThat(actualValue.apply(container))
                    .as("value retrieved via get*Config()")
                    .isEqualTo(expectedValue);
            assertThat(container.getEnvMap())
                    .as("environment variable applied to the Floci container")
                    .containsEntry(envKey, envValue);
            assertThat(container.getExposedPorts())
                    .as("port mapping configured on the Floci container")
                    .contains(exposedPort);
        }
    }

    /**
     * Same as {@link #assertConfigWired(Consumer, Function, Object, String, String, int)} but asserts
     * the always-present main Floci port. Used for services that do not expose extra ports of their
     * own.
     */
    private static void assertConfigWired(
            Consumer<FlociContainer> configurer,
            Function<FlociContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue) {
        assertConfigWired(configurer, actualValue, expectedValue, envKey, envValue, FlociContainer.PORT);
    }

    // --- Service configs (config/services/) -------------------------------------------------------

    @Test
    void shouldWireAcmConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAcmConfig(cfg -> cfg.validationWaitSeconds(5)),
                c -> c.getAcmConfig().getValidationWaitSeconds(), 5,
                "FLOCI_SERVICES_ACM_VALIDATION_WAIT_SECONDS", "5");
    }

    @Test
    void shouldWireApiGatewayConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApiGatewayConfig(cfg -> cfg.enabled(false)),
                c -> c.getApiGatewayConfig().isEnabled(), false,
                "FLOCI_SERVICES_APIGATEWAY_ENABLED", "false");
    }

    @Test
    void shouldWireApiGatewayV2ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApiGatewayV2Config(cfg -> cfg.enabled(false)),
                c -> c.getApiGatewayV2Config().isEnabled(), false,
                "FLOCI_SERVICES_APIGATEWAYV2_ENABLED", "false");
    }

    @Test
    void shouldWireAppConfigConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAppConfigConfig(cfg -> cfg.enabled(false)),
                c -> c.getAppConfigConfig().isEnabled(), false,
                "FLOCI_SERVICES_APPCONFIG_ENABLED", "false");
    }

    @Test
    void shouldWireAppConfigDataConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAppConfigDataConfig(cfg -> cfg.enabled(false)),
                c -> c.getAppConfigDataConfig().isEnabled(), false,
                "FLOCI_SERVICES_APPCONFIGDATA_ENABLED", "false");
    }

    @Test
    void shouldWireAppSyncConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAppSyncConfig(cfg -> cfg.enabled(false)),
                c -> c.getAppSyncConfig().isEnabled(), false,
                "FLOCI_SERVICES_APPSYNC_ENABLED", "false");
    }

    @Test
    void shouldWireCloudFormationConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudFormationConfig(cfg -> cfg.allowStubLambdaCode(true)),
                c -> c.getCloudFormationConfig().isAllowStubLambdaCode(), true,
                "FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_LAMBDA_CODE", "true");
    }

    @Test
    void shouldWireCloudMapConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudMapConfig(cfg -> cfg.operationCompletionDelaySeconds(5)),
                c -> c.getCloudMapConfig().getOperationCompletionDelaySeconds(), 5,
                "FLOCI_SERVICES_CLOUDMAP_OPERATION_COMPLETION_DELAY_SECONDS", "5");
    }

    @Test
    void shouldWireCloudFrontConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudFrontConfig(cfg -> cfg.domainSuffix("example.cloudfront.net")),
                c -> c.getCloudFrontConfig().getDomainSuffix(), "example.cloudfront.net",
                "FLOCI_SERVICES_CLOUDFRONT_DOMAIN_SUFFIX", "example.cloudfront.net");
    }

    @Test
    void shouldWireCloudWatchLogsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudWatchLogsConfig(cfg -> cfg.maxEventsPerQuery(5000)),
                c -> c.getCloudWatchLogsConfig().getMaxEventsPerQuery(), 5000,
                "FLOCI_SERVICES_CLOUDWATCHLOGS_MAX_EVENTS_PER_QUERY", "5000");
    }

    @Test
    void shouldWireCloudWatchMetricsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudWatchMetricsConfig(cfg -> cfg.enabled(false)),
                c -> c.getCloudWatchMetricsConfig().isEnabled(), false,
                "FLOCI_SERVICES_CLOUDWATCHMETRICS_ENABLED", "false");
    }

    @Test
    void shouldWireCognitoConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCognitoConfig(cfg -> cfg.enabled(false)),
                c -> c.getCognitoConfig().isEnabled(), false,
                "FLOCI_SERVICES_COGNITO_ENABLED", "false");
    }

    @Test
    void shouldWireConfigServiceConfigIntoContainer() {
        assertConfigWired(
                c -> c.withConfigServiceConfig(cfg -> cfg.enabled(false)),
                c -> c.getConfigServiceConfig().isEnabled(), false,
                "FLOCI_SERVICES_CONFIGSERVICE_ENABLED", "false");
    }

    @Test
    void shouldWireDynamoDbConfigIntoContainer() {
        assertConfigWired(
                c -> c.withDynamoDbConfig(cfg -> cfg.enabled(false)),
                c -> c.getDynamoDbConfig().isEnabled(), false,
                "FLOCI_SERVICES_DYNAMODB_ENABLED", "false");
    }

    @Test
    void shouldWireEc2ConfigIntoContainer() {
        // EC2 exposes the IMDS port, so assert that port rather than the main Floci port.
        assertConfigWired(
                c -> c.withEc2Config(cfg -> cfg.imdsPort(9999)),
                c -> c.getEc2Config().getImdsPort(), 9999,
                "FLOCI_SERVICES_EC2_IMDS_PORT", "9999", 9999);
    }

    @Test
    void shouldWireEventBridgeConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEventBridgeConfig(cfg -> cfg.enabled(false)),
                c -> c.getEventBridgeConfig().isEnabled(), false,
                "FLOCI_SERVICES_EVENTBRIDGE_ENABLED", "false");
    }

    @Test
    void shouldWireIamConfigIntoContainer() {
        assertConfigWired(
                c -> c.withIamConfig(cfg -> cfg.enabled(false)),
                c -> c.getIamConfig().isEnabled(), false,
                "FLOCI_SERVICES_IAM_ENABLED", "false");
    }

    @Test
    void shouldWireKinesisConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKinesisConfig(cfg -> cfg.enabled(false)),
                c -> c.getKinesisConfig().isEnabled(), false,
                "FLOCI_SERVICES_KINESIS_ENABLED", "false");
    }

    @Test
    void shouldWireKmsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKmsConfig(cfg -> cfg.enabled(false)),
                c -> c.getKmsConfig().isEnabled(), false,
                "FLOCI_SERVICES_KMS_ENABLED", "false");
    }

    @Test
    void shouldWireLambdaConfigIntoContainer() {
        // Lambda only exposes its Runtime API ports when exposeRuntimePorts is on.
        assertConfigWired(
                c -> c.withLambdaConfig(cfg -> cfg.exposeRuntimePorts(true).runtimeApiPortRange(13000, 5)),
                c -> c.getLambdaConfig().getRuntimeApiBasePort(), 13000,
                "FLOCI_SERVICES_LAMBDA_RUNTIME_API_BASE_PORT", "13000", 13000);
    }

    @Test
    void shouldWireRdsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRdsConfig(cfg -> cfg.proxyPortRange(8000, 100)),
                c -> c.getRdsConfig().getProxyBasePort(), 8000,
                "FLOCI_SERVICES_RDS_PROXY_BASE_PORT", "8000", 8000);
    }

    @Test
    void shouldWireS3ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withS3Config(cfg -> cfg.defaultPresignExpirySeconds(7200)),
                c -> c.getS3Config().getDefaultPresignExpirySeconds(), 7200,
                "FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", "7200");
    }

    @Test
    void shouldWireSchedulerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSchedulerConfig(cfg -> cfg.enabled(false)),
                c -> c.getSchedulerConfig().isEnabled(), false,
                "FLOCI_SERVICES_SCHEDULER_ENABLED", "false");
    }

    @Test
    void shouldWireSecretsManagerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSecretsManagerConfig(cfg -> cfg.defaultRecoveryWindowDays(7)),
                c -> c.getSecretsManagerConfig().getDefaultRecoveryWindowDays(), 7,
                "FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", "7");
    }

    @Test
    void shouldWireSesConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSesConfig(cfg -> cfg.enabled(false)),
                c -> c.getSesConfig().isEnabled(), false,
                "FLOCI_SERVICES_SES_ENABLED", "false");
    }

    @Test
    void shouldWireSnsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSnsConfig(cfg -> cfg.enabled(false)),
                c -> c.getSnsConfig().isEnabled(), false,
                "FLOCI_SERVICES_SNS_ENABLED", "false");
    }

    @Test
    void shouldWireSqsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSqsConfig(cfg -> cfg.defaultVisibilityTimeout(60)),
                c -> c.getSqsConfig().getDefaultVisibilityTimeout(), 60,
                "FLOCI_SERVICES_SQS_DEFAULT_VISIBILITY_TIMEOUT", "60");
    }

    @Test
    void shouldWireSsmConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSsmConfig(cfg -> cfg.maxParameterHistory(10)),
                c -> c.getSsmConfig().getMaxParameterHistory(), 10,
                "FLOCI_SERVICES_SSM_MAX_PARAMETER_HISTORY", "10");
    }

    @Test
    void shouldWirePricingConfigIntoContainer() {
        assertConfigWired(
                c -> c.withPricingConfig(cfg -> cfg.snapshotPath("/data/pricing")),
                c -> c.getPricingConfig().getSnapshotPath(), Optional.of("/data/pricing"),
                "FLOCI_SERVICES_PRICING_SNAPSHOT_PATH", "/data/pricing");
    }

    @Test
    void shouldWireAthenaConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAthenaConfig(cfg -> cfg.mock(true)),
                c -> c.getAthenaConfig().isMock(), true,
                "FLOCI_SERVICES_ATHENA_MOCK", "true");
    }

    @Test
    void shouldWireBackupConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBackupConfig(cfg -> cfg.jobCompletionDelaySeconds(5)),
                c -> c.getBackupConfig().getJobCompletionDelaySeconds(), 5,
                "FLOCI_SERVICES_BACKUP_JOB_COMPLETION_DELAY_SECONDS", "5");
    }

    @Test
    void shouldWireBedrockAgentCoreConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBedrockAgentCoreConfig(cfg -> cfg.validateRuntimeExists(true)),
                c -> c.getBedrockAgentCoreConfig().isValidateRuntimeExists(), true,
                "FLOCI_SERVICES_BEDROCK_AGENT_CORE_VALIDATE_RUNTIME_EXISTS", "true");
    }

    @Test
    void shouldWireBedrockAgentCoreControlConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBedrockAgentCoreControlConfig(cfg -> cfg.enabled(false)),
                c -> c.getBedrockAgentCoreControlConfig().isEnabled(), false,
                "FLOCI_SERVICES_BEDROCK_AGENT_CORE_CONTROL_ENABLED", "false");
    }

    @Test
    void shouldWireBedrockRuntimeConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBedrockRuntimeConfig(cfg -> cfg.enabled(false)),
                c -> c.getBedrockRuntimeConfig().isEnabled(), false,
                "FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", "false");
    }

    @Test
    void shouldWireCodeBuildConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCodeBuildConfig(cfg -> cfg.dockerNetwork("my-network")),
                c -> c.getCodeBuildConfig().getDockerNetwork(), "my-network",
                "FLOCI_SERVICES_CODEBUILD_DOCKER_NETWORK", "my-network");
    }

    @Test
    void shouldWireCodeDeployConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCodeDeployConfig(cfg -> cfg.enabled(false)),
                c -> c.getCodeDeployConfig().isEnabled(), false,
                "FLOCI_SERVICES_CODEDEPLOY_ENABLED", "false");
    }

    @Test
    void shouldWireEcrConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEcrConfig(cfg -> cfg.registryPortRange(5200, 5)),
                c -> c.getEcrConfig().getRegistryBasePort(), 5200,
                "FLOCI_SERVICES_ECR_REGISTRY_BASE_PORT", "5200", 5200);
    }

    @Test
    void shouldWireEcsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEcsConfig(cfg -> cfg.mock(true)),
                c -> c.getEcsConfig().isMock(), true,
                "FLOCI_SERVICES_ECS_MOCK", "true");
    }

    @Test
    void shouldWireEksConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEksConfig(cfg -> cfg.apiServerPortRange(6600, 5)),
                c -> c.getEksConfig().getApiServerBasePort(), 6600,
                "FLOCI_SERVICES_EKS_API_SERVER_BASE_PORT", "6600", 6600);
    }

    @Test
    void shouldWireElastiCacheConfigIntoContainer() {
        assertConfigWired(
                c -> c.withElastiCacheConfig(cfg -> cfg.proxyPortRange(7100, 5)),
                c -> c.getElastiCacheConfig().getProxyBasePort(), 7100,
                "FLOCI_SERVICES_ELASTICACHE_PROXY_BASE_PORT", "7100", 7100);
    }

    @Test
    void shouldWireElbV2ConfigIntoContainer() {
        // ELBv2 has no per-listener env var, so change mock for the env-var check and a listener
        // port for the port-mapping check.
        assertConfigWired(
                c -> c.withElbV2Config(cfg -> cfg.mock(true).listenerPort(8085)),
                c -> c.getElbV2Config().isMock(), true,
                "FLOCI_SERVICES_ELBV2_MOCK", "true", 8085);
    }

    @Test
    void shouldWireFirehoseConfigIntoContainer() {
        assertConfigWired(
                c -> c.withFirehoseConfig(cfg -> cfg.enabled(false)),
                c -> c.getFirehoseConfig().isEnabled(), false,
                "FLOCI_SERVICES_FIREHOSE_ENABLED", "false");
    }

    @Test
    void shouldWireGlueConfigIntoContainer() {
        assertConfigWired(
                c -> c.withGlueConfig(cfg -> cfg.enabled(false)),
                c -> c.getGlueConfig().isEnabled(), false,
                "FLOCI_SERVICES_GLUE_ENABLED", "false");
    }

    @Test
    void shouldWireMskConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMskConfig(cfg -> cfg.kafkaHostPortRange(9600, 20)),
                c -> c.getMskConfig().getKafkaHostPortBase(), 9600,
                "FLOCI_SERVICES_MSK_KAFKA_HOST_PORT_BASE", "9600", 9600);
    }

    @Test
    void shouldWireOpenSearchConfigIntoContainer() {
        assertConfigWired(
                c -> c.withOpenSearchConfig(cfg -> cfg.mock(true)),
                c -> c.getOpenSearchConfig().isMock(), true,
                "FLOCI_SERVICES_OPENSEARCH_MOCK", "true");
    }

    @Test
    void shouldWirePipesConfigIntoContainer() {
        assertConfigWired(
                c -> c.withPipesConfig(cfg -> cfg.enabled(false)),
                c -> c.getPipesConfig().isEnabled(), false,
                "FLOCI_SERVICES_PIPES_ENABLED", "false");
    }

    @Test
    void shouldWireResourceGroupsTaggingConfigIntoContainer() {
        assertConfigWired(
                c -> c.withResourceGroupsTaggingConfig(cfg -> cfg.enabled(false)),
                c -> c.getResourceGroupsTaggingConfig().isEnabled(), false,
                "FLOCI_SERVICES_TAGGING_ENABLED", "false");
    }

    @Test
    void shouldWireRoute53ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRoute53Config(cfg -> cfg.defaultNameserver1("ns1.example.com")),
                c -> c.getRoute53Config().getDefaultNameserver1(), "ns1.example.com",
                "FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", "ns1.example.com");
    }

    @Test
    void shouldWireStepFunctionsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withStepFunctionsConfig(cfg -> cfg.enabled(false)),
                c -> c.getStepFunctionsConfig().isEnabled(), false,
                "FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "false");
    }

    @Test
    void shouldWireTextractConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTextractConfig(cfg -> cfg.enabled(false)),
                c -> c.getTextractConfig().isEnabled(), false,
                "FLOCI_SERVICES_TEXTRACT_ENABLED", "false");
    }

    @Test
    void shouldWireTransferFamilyConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTransferFamilyConfig(cfg -> cfg.enabled(false)),
                c -> c.getTransferFamilyConfig().isEnabled(), false,
                "FLOCI_SERVICES_TRANSFER_ENABLED", "false");
    }

    @Test
    void shouldWireNeptuneConfigIntoContainer() {
        assertConfigWired(
                c -> c.withNeptuneConfig(cfg -> cfg.proxyPortRange(9200, 51)),
                c -> c.getNeptuneConfig().getProxyBasePort(), 9200,
                "FLOCI_SERVICES_NEPTUNE_PROXY_BASE_PORT", "9200", 9200);
    }

    @Test
    void shouldWireCostExplorerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCostExplorerConfig(cfg -> cfg.creditUsdMonthly(250.0)),
                c -> c.getCostExplorerConfig().getCreditUsdMonthly(), 250.0,
                "FLOCI_SERVICES_CE_CREDIT_USD_MONTHLY", "250.0");
    }

    @Test
    void shouldWireCurConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCurConfig(cfg -> cfg.emitMode("daily")),
                c -> c.getCurConfig().getEmitMode(), "daily",
                "FLOCI_SERVICES_CUR_EMIT_MODE", "daily");
    }

    @Test
    void shouldWireCloudTrailConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudTrailConfig(cfg -> cfg.enabled(false)),
                c -> c.getCloudTrailConfig().isEnabled(), false,
                "FLOCI_SERVICES_CLOUDTRAIL_ENABLED", "false");
    }

    @Test
    void shouldWireBcmDataExportsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBcmDataExportsConfig(cfg -> cfg.emitMode("off")),
                c -> c.getBcmDataExportsConfig().getEmitMode(), "off",
                "FLOCI_SERVICES_BCM_DATA_EXPORTS_EMIT_MODE", "off");
    }

    @Test
    void shouldWireBatchConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBatchConfig(cfg -> cfg.runnerMode("deferred")),
                c -> c.getBatchConfig().getRunnerMode(), "deferred",
                "FLOCI_SERVICES_BATCH_RUNNER_MODE", "deferred");
    }

    @Test
    void shouldWireRdsDataConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRdsDataConfig(cfg -> cfg.transactionTtlSeconds(300)),
                c -> c.getRdsDataConfig().getTransactionTtlSeconds(), 300L,
                "FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", "300");
    }

    @Test
    void shouldWireDocumentDbConfigIntoContainer() {
        assertConfigWired(
                c -> c.withDocumentDbConfig(cfg -> cfg.mock(true)),
                c -> c.getDocumentDbConfig().isMock(), true,
                "FLOCI_SERVICES_DOCDB_MOCK", "true");
    }

    @Test
    void shouldWireEmrConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEmrConfig(cfg -> cfg.defaultReleaseLabel("emr-7.8.0")),
                c -> c.getEmrConfig().getDefaultReleaseLabel(), "emr-7.8.0",
                "FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", "emr-7.8.0");
    }

    @Test
    void shouldWireWafV2ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withWafV2Config(cfg -> cfg.enabled(false)),
                c -> c.getWafV2Config().isEnabled(), false,
                "FLOCI_SERVICES_WAFV2_ENABLED", "false");
    }

    @Test
    void shouldWireIotConfigIntoContainer() {
        assertConfigWired(
                c -> c.withIotConfig(cfg -> cfg.mqttPort(11883)),
                c -> c.getIotConfig().getMqttPort(), 11883,
                "FLOCI_SERVICES_IOT_MQTT_PORT", "11883", 11883);
    }

    @Test
    void shouldWireIotDataConfigIntoContainer() {
        assertConfigWired(
                c -> c.withIotDataConfig(cfg -> cfg.enabled(false)),
                c -> c.getIotDataConfig().isEnabled(), false,
                "FLOCI_SERVICES_IOTDATA_ENABLED", "false");
    }

    @Test
    void shouldWireLightsailConfigIntoContainer() {
        assertConfigWired(
                c -> c.withLightsailConfig(cfg -> cfg.enabled(false)),
                c -> c.getLightsailConfig().isEnabled(), false,
                "FLOCI_SERVICES_LIGHTSAIL_ENABLED", "false");
    }

    @Test
    void shouldWireCloudControlConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudControlConfig(cfg -> cfg.enabled(false)),
                c -> c.getCloudControlConfig().isEnabled(), false,
                "FLOCI_SERVICES_CLOUDCONTROL_ENABLED", "false");
    }

    @Test
    void shouldWireS3VectorsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withS3VectorsConfig(cfg -> cfg.enabled(false)),
                c -> c.getS3VectorsConfig().isEnabled(), false,
                "FLOCI_SERVICES_S3VECTORS_ENABLED", "false");
    }

    @Test
    void shouldWireElasticBeanstalkConfigIntoContainer() {
        assertConfigWired(
                c -> c.withElasticBeanstalkConfig(cfg -> cfg.enabled(false)),
                c -> c.getElasticBeanstalkConfig().isEnabled(), false,
                "FLOCI_SERVICES_ELASTICBEANSTALK_ENABLED", "false");
    }

    @Test
    void shouldWireCodePipelineConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCodePipelineConfig(cfg -> cfg.enabled(false)),
                c -> c.getCodePipelineConfig().isEnabled(), false,
                "FLOCI_SERVICES_CODEPIPELINE_ENABLED", "false");
    }

    @Test
    void shouldWireAmazonMqConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAmazonMqConfig(cfg -> cfg.mock(true)),
                c -> c.getAmazonMqConfig().isMock(), true,
                "FLOCI_SERVICES_AMAZONMQ_MOCK", "true");
    }

    @Test
    void shouldWireMemoryDbConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMemoryDbConfig(cfg -> cfg.proxyPortRange(7200, 20)),
                c -> c.getMemoryDbConfig().getProxyBasePort(), 7200,
                "FLOCI_SERVICES_MEMORYDB_PROXY_BASE_PORT", "7200", 7200);
    }

    @Test
    void shouldWireRumConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRumConfig(cfg -> cfg.enabled(false)),
                c -> c.getRumConfig().isEnabled(), false,
                "FLOCI_SERVICES_RUM_ENABLED", "false");
    }

    @Test
    void shouldWireS3TablesConfigIntoContainer() {
        assertConfigWired(
                c -> c.withS3TablesConfig(cfg -> cfg.enabled(false)),
                c -> c.getS3TablesConfig().isEnabled(), false,
                "FLOCI_SERVICES_S3TABLES_ENABLED", "false");
    }

    @Test
    void shouldWireApplicationAutoScalingConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApplicationAutoScalingConfig(cfg -> cfg.enabled(false)),
                c -> c.getApplicationAutoScalingConfig().isEnabled(), false,
                "FLOCI_SERVICES_APPLICATIONAUTOSCALING_ENABLED", "false");
    }

    @Test
    void shouldWireSwfConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSwfConfig(cfg -> cfg.timeoutSweepEnabled(false)),
                c -> c.getSwfConfig().isTimeoutSweepEnabled(), false,
                "FLOCI_SERVICES_SWF_TIMEOUT_SWEEP_ENABLED", "false");
    }

    @Test
    void shouldWireKinesisAnalyticsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKinesisAnalyticsConfig(cfg -> cfg.mock(true)),
                c -> c.getKinesisAnalyticsConfig().isMock(), true,
                "FLOCI_SERVICES_KINESIS_ANALYTICS_MOCK", "true");
    }

    @Test
    void shouldWireMwaaConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMwaaConfig(cfg -> cfg.proxyPortRange(9100, 100)),
                c -> c.getMwaaConfig().getProxyBasePort(), 9100,
                "FLOCI_SERVICES_MWAA_PROXY_BASE_PORT", "9100", 9100);
    }

    @Test
    void shouldWireGuardDutyConfigIntoContainer() {
        assertConfigWired(
                c -> c.withGuardDutyConfig(cfg -> cfg.enabled(false)),
                c -> c.getGuardDutyConfig().isEnabled(), false,
                "FLOCI_SERVICES_GUARDDUTY_ENABLED", "false");
    }

    @Test
    void shouldWireCloudHsmV2ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudHsmV2Config(cfg -> cfg.enabled(false)),
                c -> c.getCloudHsmV2Config().isEnabled(), false,
                "FLOCI_SERVICES_CLOUDHSMV2_ENABLED", "false");
    }

    @Test
    void shouldWireEmrServerlessConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEmrServerlessConfig(cfg -> cfg.enabled(false)),
                c -> c.getEmrServerlessConfig().isEnabled(), false,
                "FLOCI_SERVICES_EMRSERVERLESS_ENABLED", "false");
    }

    @Test
    void shouldWireFisConfigIntoContainer() {
        assertConfigWired(
                c -> c.withFisConfig(cfg -> cfg.enabled(false)),
                c -> c.getFisConfig().isEnabled(), false,
                "FLOCI_SERVICES_FIS_ENABLED", "false");
    }

    @Test
    void shouldWireConnectConfigIntoContainer() {
        assertConfigWired(
                c -> c.withConnectConfig(cfg -> cfg.enabled(false)),
                c -> c.getConnectConfig().isEnabled(), false,
                "FLOCI_SERVICES_CONNECT_ENABLED", "false");
    }

    @Test
    void shouldWireSsoAdminConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSsoAdminConfig(cfg -> cfg.enabled(false)),
                c -> c.getSsoAdminConfig().isEnabled(), false,
                "FLOCI_SERVICES_SSOADMIN_ENABLED", "false");
    }

    @Test
    void shouldWireApsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApsConfig(cfg -> cfg.enabled(false)),
                c -> c.getApsConfig().isEnabled(), false,
                "FLOCI_SERVICES_APS_ENABLED", "false");
    }

    @Test
    void shouldWireCodeGuruReviewerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCodeGuruReviewerConfig(cfg -> cfg.enabled(false)),
                c -> c.getCodeGuruReviewerConfig().isEnabled(), false,
                "FLOCI_SERVICES_CODEGURUREVIEWER_ENABLED", "false");
    }

    @Test
    void shouldWireControlTowerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withControlTowerConfig(cfg -> cfg.enabled(false)),
                c -> c.getControlTowerConfig().isEnabled(), false,
                "FLOCI_SERVICES_CONTROLTOWER_ENABLED", "false");
    }

    @Test
    void shouldWireRoute53ResolverConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRoute53ResolverConfig(cfg -> cfg.enabled(false)),
                c -> c.getRoute53ResolverConfig().isEnabled(), false,
                "FLOCI_SERVICES_ROUTE53RESOLVER_ENABLED", "false");
    }

    @Test
    void shouldWireNetworkFirewallConfigIntoContainer() {
        assertConfigWired(
                c -> c.withNetworkFirewallConfig(cfg -> cfg.enabled(false)),
                c -> c.getNetworkFirewallConfig().isEnabled(), false,
                "FLOCI_SERVICES_NETWORKFIREWALL_ENABLED", "false");
    }

    @Test
    void shouldWireServiceCatalogConfigIntoContainer() {
        assertConfigWired(
                c -> c.withServiceCatalogConfig(cfg -> cfg.enabled(false)),
                c -> c.getServiceCatalogConfig().isEnabled(), false,
                "FLOCI_SERVICES_SERVICECATALOG_ENABLED", "false");
    }

    @Test
    void shouldWireServiceQuotasConfigIntoContainer() {
        assertConfigWired(
                c -> c.withServiceQuotasConfig(cfg -> cfg.enabled(false)),
                c -> c.getServiceQuotasConfig().isEnabled(), false,
                "FLOCI_SERVICES_SERVICEQUOTAS_ENABLED", "false");
    }

    @Test
    void shouldWireRamConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRamConfig(cfg -> cfg.enabled(false)),
                c -> c.getRamConfig().isEnabled(), false,
                "FLOCI_SERVICES_RAM_ENABLED", "false");
    }

    @Test
    void shouldWireLakeFormationConfigIntoContainer() {
        assertConfigWired(
                c -> c.withLakeFormationConfig(cfg -> cfg.enabled(false)),
                c -> c.getLakeFormationConfig().isEnabled(), false,
                "FLOCI_SERVICES_LAKEFORMATION_ENABLED", "false");
    }

    @Test
    void shouldWireEfsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEfsConfig(cfg -> cfg.enabled(false)),
                c -> c.getEfsConfig().isEnabled(), false,
                "FLOCI_SERVICES_EFS_ENABLED", "false");
    }

    @Test
    void shouldWireResourceExplorer2ConfigIntoContainer() {
        assertConfigWired(
                c -> c.withResourceExplorer2Config(cfg -> cfg.enabled(false)),
                c -> c.getResourceExplorer2Config().isEnabled(), false,
                "FLOCI_SERVICES_RESOURCEEXPLORER2_ENABLED", "false");
    }

    @Test
    void shouldWireRedshiftConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRedshiftConfig(cfg -> cfg.defaultPort(5000)),
                c -> c.getRedshiftConfig().getDefaultPort(), 5000,
                "FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", "5000");
    }

    @Test
    void shouldWireOrganizationsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withOrganizationsConfig(cfg -> cfg.scpEnforcementEnabled(true)),
                c -> c.getOrganizationsConfig().isScpEnforcementEnabled(), true,
                "FLOCI_SERVICES_ORGANIZATIONS_SCP_ENFORCEMENT_ENABLED", "true");
    }

    @Test
    void shouldWireComprehendConfigIntoContainer() {
        assertConfigWired(
                c -> c.withComprehendConfig(cfg -> cfg.enabled(false)),
                c -> c.getComprehendConfig().isEnabled(), false,
                "FLOCI_SERVICES_COMPREHEND_ENABLED", "false");
    }

    @Test
    void shouldWireRekognitionConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRekognitionConfig(cfg -> cfg.enabled(false)),
                c -> c.getRekognitionConfig().isEnabled(), false,
                "FLOCI_SERVICES_REKOGNITION_ENABLED", "false");
    }

    @Test
    void shouldWireTranscribeConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTranscribeConfig(cfg -> cfg.enabled(false)),
                c -> c.getTranscribeConfig().isEnabled(), false,
                "FLOCI_SERVICES_TRANSCRIBE_ENABLED", "false");
    }

    // --- Cross-cutting configs (config/) --------------------------------------------------------

    @Test
    void shouldWireDuckDbConfigIntoContainer() {
        assertConfigWired(
                c -> c.withDuckDbConfig(cfg -> cfg.url("http://custom-duckdb:8080")),
                c -> c.getDuckDbConfig().getUrl(), "http://custom-duckdb:8080",
                "FLOCI_SERVICES_DUCK_URL", "http://custom-duckdb:8080");
    }

    @Test
    void shouldWireSecurityConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSecurityConfig(cfg -> cfg.disableCorsHeaders(true)),
                c -> c.getSecurityConfig().isDisableCorsHeaders(), true,
                "FLOCI_SECURITY_DISABLE_CORS_HEADERS", "true");
    }

    @Test
    void shouldWireProtocolsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withProtocolsConfig(cfg -> cfg.strictClaiming(true)),
                c -> c.getProtocolsConfig().isStrictClaiming(), true,
                "FLOCI_PROTOCOLS_STRICT_CLAIMING", "true");
    }

    @Test
    void shouldWireAuthConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAuthConfig(cfg -> cfg.validateSignatures(true)),
                c -> c.getAuthConfig().isValidateSignatures(), true,
                "FLOCI_AUTH_VALIDATE_SIGNATURES", "true");
    }

    @Test
    void shouldWireInitHooksConfigIntoContainer() {
        assertConfigWired(
                c -> c.withInitHooksConfig(cfg -> cfg.timeoutSeconds(60)),
                c -> c.getInitHooksConfig().getTimeoutSeconds(), 60L,
                "FLOCI_INIT_HOOKS_TIMEOUT_SECONDS", "60");
    }

}
