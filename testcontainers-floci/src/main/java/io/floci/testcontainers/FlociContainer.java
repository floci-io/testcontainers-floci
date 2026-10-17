package io.floci.testcontainers;

import io.floci.testcontainers.config.*;
import io.floci.testcontainers.config.services.*;
import io.floci.testcontainers.core.AbstractFlociContainer;
import io.floci.testcontainers.core.CloudDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Testcontainers module for <a href="https://github.com/floci-io/floci">Floci</a> — a
 * free, open-source local AWS emulator.
 *
 * <p>Starts a Floci container that exposes all emulated AWS services on a single HTTP
 * endpoint. Use {@link #getEndpoint()} to obtain the URL for configuring AWS SDK clients.
 *
 * <p>Container-based services (RDS, ElastiCache, Lambda, ECS, and others) require access to the
 * Docker daemon. This module automatically mounts the Docker socket only when at least one
 * currently enabled service actually needs it.
 * Sibling containers created by Floci (e.g. PostgreSQL for RDS) are accessible on the Docker
 * host via their mapped ports, and are removed together with their volumes when the container is stopped.
 * Use {@link #withDockerSocket(boolean)} to override the Docker socket auto-detection, e.g. to opt out entirely
 * regardless of which services are enabled.
 *
 * <pre>{@code
 * try (FlociContainer floci = new FlociContainer()) {
 *     floci.start();
 *     String endpoint = floci.getEndpoint();
 *     // configure your AWS SDK client with the endpoint
 * }
 * }</pre>
 */
public class FlociContainer extends AbstractFlociContainer<FlociContainer> {

    private static final Logger logger = LoggerFactory.getLogger(FlociContainer.class);

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("floci/floci");
    private static final String DEFAULT_TAG = "latest";

    /**
     * Default port used to startup Floci container
     */
    public static final int PORT = 4566;

    /**
     * Description of the Floci AWS emulator.
     */
    public static final CloudDescriptor DESCRIPTOR = CloudDescriptor.builder()
            .name("aws")
            .image(DEFAULT_IMAGE_NAME.getUnversionedPart())
            .port(PORT)
            .envPrefix("FLOCI_")
            .healthPath("/_floci/health")
            .resetPath("/_floci/state/reset")
            .logLevelEnvVar("QUARKUS_LOG_CATEGORY__IO_GITHUB_HECTORVENT__LEVEL")
            .networkEnvVar("FLOCI_SERVICES_DOCKER_NETWORK")
            .resourceNamespaceEnvVar("FLOCI_DOCKER_RESOURCE_NAMESPACE")
            .tlsHttpsPortEnvVar("FLOCI_TLS_AWS_HTTPS_PORT")
            .build();

    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(30);

    private static final String DEFAULT_REGION = "us-east-1";
    private static final String DEFAULT_AVAILABILITY_ZONE = "us-east-1a";
    private static final String DEFAULT_ACCOUNT_ID = "000000000000";
    private static final String DEFAULT_ACCESS_KEY = "test";
    private static final String DEFAULT_SECRET_KEY = "test";

    private static final String AI_MOCK_CONFIG_FILE_PREFIX = "/tmp/floci-ai-mock-config-";
    private static final String AI_MOCK_CONFIG_FILE_SUFFIX = ".json";

    // Shared AI mock-response configuration: either an explicit container path (withAiMockConfigFile)
    // or a generated path pointing at the content copied into the container (withAiMockConfig).
    private String aiMockConfigFile;
    private String aiMockConfig;

    private TlsConfig tlsConfig = TlsConfig.builder().build();
    private StorageConfig storageConfig = StorageConfig.builder().build();
    private DuckDbConfig duckDbConfig = DuckDbConfig.builder().build();
    private SecurityConfig securityConfig = SecurityConfig.builder().build();
    private ProtocolsConfig protocolsConfig = ProtocolsConfig.builder().build();
    private AuthConfig authConfig = AuthConfig.builder().build();
    private InitHooksConfig initHooksConfig = InitHooksConfig.builder().build();
    private PartitionsConfig partitionsConfig = PartitionsConfig.builder().build();
    private NetworkConfig networkConfig = NetworkConfig.builder().build();

    // Services config
    private final ServiceConfigRef<AcmConfig> acmConfig = registerServiceConfig(AcmConfig.builder().build());
    private final ServiceConfigRef<ApiGatewayConfig> apiGatewayConfig = registerServiceConfig(ApiGatewayConfig.builder().build());
    private final ServiceConfigRef<ApiGatewayV2Config> apiGatewayV2Config = registerServiceConfig(ApiGatewayV2Config.builder().build());
    private final ServiceConfigRef<AppConfigConfig> appConfigConfig = registerServiceConfig(AppConfigConfig.builder().build());
    private final ServiceConfigRef<AppConfigDataConfig> appConfigDataConfig = registerServiceConfig(AppConfigDataConfig.builder().build());
    private final ServiceConfigRef<AppSyncConfig> appSyncConfig = registerServiceConfig(AppSyncConfig.builder().build());
    private final ServiceConfigRef<CloudFormationConfig> cloudFormationConfig = registerServiceConfig(CloudFormationConfig.builder().build());
    private final ServiceConfigRef<CloudFrontConfig> cloudFrontConfig = registerServiceConfig(CloudFrontConfig.builder().build());
    private final ServiceConfigRef<CloudMapConfig> cloudMapConfig = registerServiceConfig(CloudMapConfig.builder().build());
    private final ServiceConfigRef<CloudTrailConfig> cloudTrailConfig = registerServiceConfig(CloudTrailConfig.builder().build());
    private final ServiceConfigRef<CloudWatchLogsConfig> cloudWatchLogsConfig = registerServiceConfig(CloudWatchLogsConfig.builder().build());
    private final ServiceConfigRef<CloudWatchMetricsConfig> cloudWatchMetricsConfig = registerServiceConfig(CloudWatchMetricsConfig.builder().build());
    private final ServiceConfigRef<CognitoConfig> cognitoConfig = registerServiceConfig(CognitoConfig.builder().build());
    private final ServiceConfigRef<ConfigServiceConfig> configServiceConfig = registerServiceConfig(ConfigServiceConfig.builder().build());
    private final ServiceConfigRef<DynamoDbConfig> dynamoDbConfig = registerServiceConfig(DynamoDbConfig.builder().build());
    private final ServiceConfigRef<Ec2Config> ec2Config = registerServiceConfig(Ec2Config.builder().build());
    private final ServiceConfigRef<EcrConfig> ecrConfig = registerServiceConfig(EcrConfig.builder().build());
    private final ServiceConfigRef<EcsConfig> ecsConfig = registerServiceConfig(EcsConfig.builder().build());
    private final ServiceConfigRef<ElastiCacheConfig> elastiCacheConfig = registerServiceConfig(ElastiCacheConfig.builder().build());
    private final ServiceConfigRef<EventBridgeConfig> eventBridgeConfig = registerServiceConfig(EventBridgeConfig.builder().build());
    private final ServiceConfigRef<IamConfig> iamConfig = registerServiceConfig(IamConfig.builder().build());
    private final ServiceConfigRef<KinesisConfig> kinesisConfig = registerServiceConfig(KinesisConfig.builder().build());
    private final ServiceConfigRef<KmsConfig> kmsConfig = registerServiceConfig(KmsConfig.builder().build());
    private final ServiceConfigRef<LambdaConfig> lambdaConfig = registerServiceConfig(LambdaConfig.builder().build());
    private final ServiceConfigRef<OpenSearchConfig> openSearchConfig = registerServiceConfig(OpenSearchConfig.builder().build());
    private final ServiceConfigRef<RdsConfig> rdsConfig = registerServiceConfig(RdsConfig.builder().build());
    private final ServiceConfigRef<S3Config> s3Config = registerServiceConfig(S3Config.builder().build());
    private final ServiceConfigRef<SchedulerConfig> schedulerConfig = registerServiceConfig(SchedulerConfig.builder().build());
    private final ServiceConfigRef<SecretsManagerConfig> secretsManagerConfig = registerServiceConfig(SecretsManagerConfig.builder().build());
    private final ServiceConfigRef<SesConfig> sesConfig = registerServiceConfig(SesConfig.builder().build());
    private final ServiceConfigRef<SnsConfig> snsConfig = registerServiceConfig(SnsConfig.builder().build());
    private final ServiceConfigRef<SqsConfig> sqsConfig = registerServiceConfig(SqsConfig.builder().build());
    private final ServiceConfigRef<SsmConfig> ssmConfig = registerServiceConfig(SsmConfig.builder().build());
    private final ServiceConfigRef<StepFunctionsConfig> stepFunctionsConfig = registerServiceConfig(StepFunctionsConfig.builder().build());
    private final ServiceConfigRef<MskConfig> mskConfig = registerServiceConfig(MskConfig.builder().build());
    private final ServiceConfigRef<FirehoseConfig> firehoseConfig = registerServiceConfig(FirehoseConfig.builder().build());
    private final ServiceConfigRef<AthenaConfig> athenaConfig = registerServiceConfig(AthenaConfig.builder().build());
    private final ServiceConfigRef<GlueConfig> glueConfig = registerServiceConfig(GlueConfig.builder().build());
    private final ServiceConfigRef<ResourceGroupsTaggingConfig> resourceGroupsTaggingConfig = registerServiceConfig(ResourceGroupsTaggingConfig.builder().build());
    private final ServiceConfigRef<BedrockAgentCoreConfig> bedrockAgentCoreConfig = registerServiceConfig(BedrockAgentCoreConfig.builder().build());
    private final ServiceConfigRef<BedrockAgentCoreControlConfig> bedrockAgentCoreControlConfig = registerServiceConfig(BedrockAgentCoreControlConfig.builder().build());
    private final ServiceConfigRef<BedrockRuntimeConfig> bedrockRuntimeConfig = registerServiceConfig(BedrockRuntimeConfig.builder().build());
    private final ServiceConfigRef<PipesConfig> pipesConfig = registerServiceConfig(PipesConfig.builder().build());
    private final ServiceConfigRef<EksConfig> eksConfig = registerServiceConfig(EksConfig.builder().build());
    private final ServiceConfigRef<CodeBuildConfig> codeBuildConfig = registerServiceConfig(CodeBuildConfig.builder().build());
    private final ServiceConfigRef<CodeDeployConfig> codeDeployConfig = registerServiceConfig(CodeDeployConfig.builder().build());
    private final ServiceConfigRef<ElbV2Config> elbV2Config = registerServiceConfig(ElbV2Config.builder().build());
    private final ServiceConfigRef<BackupConfig> backupConfig = registerServiceConfig(BackupConfig.builder().build());
    private final ServiceConfigRef<TransferFamilyConfig> transferFamilyConfig = registerServiceConfig(TransferFamilyConfig.builder().build());
    private final ServiceConfigRef<Route53Config> route53Config = registerServiceConfig(Route53Config.builder().build());
    private final ServiceConfigRef<TextractConfig> textractConfig = registerServiceConfig(TextractConfig.builder().build());
    private final ServiceConfigRef<PricingConfig> pricingConfig = registerServiceConfig(PricingConfig.builder().build());
    private final ServiceConfigRef<NeptuneConfig> neptuneConfig = registerServiceConfig(NeptuneConfig.builder().build());
    private final ServiceConfigRef<CostExplorerConfig> costExplorerConfig = registerServiceConfig(CostExplorerConfig.builder().build());
    private final ServiceConfigRef<CurConfig> curConfig = registerServiceConfig(CurConfig.builder().build());
    private final ServiceConfigRef<BcmDataExportsConfig> bcmDataExportsConfig = registerServiceConfig(BcmDataExportsConfig.builder().build());
    private final ServiceConfigRef<BatchConfig> batchConfig = registerServiceConfig(BatchConfig.builder().build());
    private final ServiceConfigRef<RdsDataConfig> rdsDataConfig = registerServiceConfig(RdsDataConfig.builder().build());
    private final ServiceConfigRef<DocumentDbConfig> documentDbConfig = registerServiceConfig(DocumentDbConfig.builder().build());
    private final ServiceConfigRef<EmrConfig> emrConfig = registerServiceConfig(EmrConfig.builder().build());
    private final ServiceConfigRef<WafV2Config> wafV2Config = registerServiceConfig(WafV2Config.builder().build());
    private final ServiceConfigRef<IotConfig> iotConfig = registerServiceConfig(IotConfig.builder().build());
    private final ServiceConfigRef<IotDataConfig> iotDataConfig = registerServiceConfig(IotDataConfig.builder().build());
    private final ServiceConfigRef<LightsailConfig> lightsailConfig = registerServiceConfig(LightsailConfig.builder().build());
    private final ServiceConfigRef<CloudControlConfig> cloudControlConfig = registerServiceConfig(CloudControlConfig.builder().build());
    private final ServiceConfigRef<S3VectorsConfig> s3VectorsConfig = registerServiceConfig(S3VectorsConfig.builder().build());
    private final ServiceConfigRef<ElasticBeanstalkConfig> elasticBeanstalkConfig = registerServiceConfig(ElasticBeanstalkConfig.builder().build());
    private final ServiceConfigRef<CodePipelineConfig> codePipelineConfig = registerServiceConfig(CodePipelineConfig.builder().build());
    private final ServiceConfigRef<AmazonMqConfig> amazonMqConfig = registerServiceConfig(AmazonMqConfig.builder().build());
    private final ServiceConfigRef<MemoryDbConfig> memoryDbConfig = registerServiceConfig(MemoryDbConfig.builder().build());
    private final ServiceConfigRef<RumConfig> rumConfig = registerServiceConfig(RumConfig.builder().build());
    private final ServiceConfigRef<S3TablesConfig> s3TablesConfig = registerServiceConfig(S3TablesConfig.builder().build());
    private final ServiceConfigRef<ApplicationAutoScalingConfig> applicationAutoScalingConfig = registerServiceConfig(ApplicationAutoScalingConfig.builder().build());
    private final ServiceConfigRef<SwfConfig> swfConfig = registerServiceConfig(SwfConfig.builder().build());
    private final ServiceConfigRef<KinesisAnalyticsConfig> kinesisAnalyticsConfig = registerServiceConfig(KinesisAnalyticsConfig.builder().build());
    private final ServiceConfigRef<MwaaConfig> mwaaConfig = registerServiceConfig(MwaaConfig.builder().build());
    private final ServiceConfigRef<GuardDutyConfig> guardDutyConfig = registerServiceConfig(GuardDutyConfig.builder().build());
    private final ServiceConfigRef<CloudHsmV2Config> cloudHsmV2Config = registerServiceConfig(CloudHsmV2Config.builder().build());
    private final ServiceConfigRef<EmrServerlessConfig> emrServerlessConfig = registerServiceConfig(EmrServerlessConfig.builder().build());
    private final ServiceConfigRef<FisConfig> fisConfig = registerServiceConfig(FisConfig.builder().build());
    private final ServiceConfigRef<ConnectConfig> connectConfig = registerServiceConfig(ConnectConfig.builder().build());
    private final ServiceConfigRef<SsoAdminConfig> ssoAdminConfig = registerServiceConfig(SsoAdminConfig.builder().build());
    private final ServiceConfigRef<ApsConfig> apsConfig = registerServiceConfig(ApsConfig.builder().build());
    private final ServiceConfigRef<CodeGuruReviewerConfig> codeGuruReviewerConfig = registerServiceConfig(CodeGuruReviewerConfig.builder().build());
    private final ServiceConfigRef<ControlTowerConfig> controlTowerConfig = registerServiceConfig(ControlTowerConfig.builder().build());
    private final ServiceConfigRef<Route53ResolverConfig> route53ResolverConfig = registerServiceConfig(Route53ResolverConfig.builder().build());
    private final ServiceConfigRef<NetworkFirewallConfig> networkFirewallConfig = registerServiceConfig(NetworkFirewallConfig.builder().build());
    private final ServiceConfigRef<ServiceCatalogConfig> serviceCatalogConfig = registerServiceConfig(ServiceCatalogConfig.builder().build());
    private final ServiceConfigRef<ServiceQuotasConfig> serviceQuotasConfig = registerServiceConfig(ServiceQuotasConfig.builder().build());
    private final ServiceConfigRef<RamConfig> ramConfig = registerServiceConfig(RamConfig.builder().build());
    private final ServiceConfigRef<LakeFormationConfig> lakeFormationConfig = registerServiceConfig(LakeFormationConfig.builder().build());
    private final ServiceConfigRef<EfsConfig> efsConfig = registerServiceConfig(EfsConfig.builder().build());
    private final ServiceConfigRef<ResourceExplorer2Config> resourceExplorer2Config = registerServiceConfig(ResourceExplorer2Config.builder().build());
    private final ServiceConfigRef<RedshiftConfig> redshiftConfig = registerServiceConfig(RedshiftConfig.builder().build());
    private final ServiceConfigRef<OrganizationsConfig> organizationsConfig = registerServiceConfig(OrganizationsConfig.builder().build());
    private final ServiceConfigRef<ComprehendConfig> comprehendConfig = registerServiceConfig(ComprehendConfig.builder().build());
    private final ServiceConfigRef<RekognitionConfig> rekognitionConfig = registerServiceConfig(RekognitionConfig.builder().build());
    private final ServiceConfigRef<TranscribeConfig> transcribeConfig = registerServiceConfig(TranscribeConfig.builder().build());
    private final ServiceConfigRef<RedshiftDataConfig> redshiftDataConfig = registerServiceConfig(RedshiftDataConfig.builder().build());
    private final ServiceConfigRef<RedshiftServerlessConfig> redshiftServerlessConfig = registerServiceConfig(RedshiftServerlessConfig.builder().build());
    private final ServiceConfigRef<BedrockConfig> bedrockConfig = registerServiceConfig(BedrockConfig.builder().build());
    private final ServiceConfigRef<TranslateConfig> translateConfig = registerServiceConfig(TranslateConfig.builder().build());
    private final ServiceConfigRef<OamConfig> oamConfig = registerServiceConfig(OamConfig.builder().build());
    private final ServiceConfigRef<BcmPricingCalculatorConfig> bcmPricingCalculatorConfig = registerServiceConfig(BcmPricingCalculatorConfig.builder().build());
    private final ServiceConfigRef<TimestreamInfluxDbConfig> timestreamInfluxDbConfig = registerServiceConfig(TimestreamInfluxDbConfig.builder().build());
    private final ServiceConfigRef<SageMakerConfig> sageMakerConfig = registerServiceConfig(SageMakerConfig.builder().build());
    private final ServiceConfigRef<SsoOidcConfig> ssoOidcConfig = registerServiceConfig(SsoOidcConfig.builder().build());
    private final ServiceConfigRef<Macie2Config> macie2Config = registerServiceConfig(Macie2Config.builder().build());
    private final ServiceConfigRef<AccountConfig> accountConfig = registerServiceConfig(AccountConfig.builder().build());
    private final ServiceConfigRef<AccessAnalyzerConfig> accessAnalyzerConfig = registerServiceConfig(AccessAnalyzerConfig.builder().build());
    private final ServiceConfigRef<IdentityStoreConfig> identityStoreConfig = registerServiceConfig(IdentityStoreConfig.builder().build());
    private final ServiceConfigRef<BudgetsConfig> budgetsConfig = registerServiceConfig(BudgetsConfig.builder().build());
    private final ServiceConfigRef<Inspector2Config> inspector2Config = registerServiceConfig(Inspector2Config.builder().build());
    private final ServiceConfigRef<SecurityHubConfig> securityHubConfig = registerServiceConfig(SecurityHubConfig.builder().build());
    private final ServiceConfigRef<DetectiveConfig> detectiveConfig = registerServiceConfig(DetectiveConfig.builder().build());
    private final ServiceConfigRef<VerifiedPermissionsConfig> verifiedPermissionsConfig = registerServiceConfig(VerifiedPermissionsConfig.builder().build());
    private final ServiceConfigRef<ControlCatalogConfig> controlCatalogConfig = registerServiceConfig(ControlCatalogConfig.builder().build());
    private final ServiceConfigRef<AppIntegrationsConfig> appIntegrationsConfig = registerServiceConfig(AppIntegrationsConfig.builder().build());
    private final ServiceConfigRef<DlmConfig> dlmConfig = registerServiceConfig(DlmConfig.builder().build());
    private final ServiceConfigRef<CognitoIdentityConfig> cognitoIdentityConfig = registerServiceConfig(CognitoIdentityConfig.builder().build());
    private final ServiceConfigRef<GlobalAcceleratorConfig> globalAcceleratorConfig = registerServiceConfig(GlobalAcceleratorConfig.builder().build());
    private final ServiceConfigRef<DataSyncConfig> dataSyncConfig = registerServiceConfig(DataSyncConfig.builder().build());
    private final ServiceConfigRef<CodeArtifactConfig> codeArtifactConfig = registerServiceConfig(CodeArtifactConfig.builder().build());
    private final ServiceConfigRef<MarketplaceConfig> marketplaceConfig = registerServiceConfig(MarketplaceConfig.builder().build());
    private final ServiceConfigRef<DmsConfig> dmsConfig = registerServiceConfig(DmsConfig.builder().build());
    private final ServiceConfigRef<ElbConfig> elbConfig = registerServiceConfig(ElbConfig.builder().build());

    /**
     * Creates a new Floci container with the default image ({@code floci/floci:latest}).
     */
    public FlociContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Creates a new Floci container with the specified image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci})
     */
    public FlociContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Creates a new Floci container with the specified Docker image name.
     *
     * @param dockerImageName the Docker image name
     */
    public FlociContainer(DockerImageName dockerImageName) {
        super(dockerImageName, DESCRIPTOR);

        // /_floci/health answers as soon as the HTTP server is up; /_floci/init reports "ready" only after the
        // ready-stage init hooks have run, so a started container means the hooks are done.
        waitingFor(Wait.forHttp("/_floci/init")
                .forPort(PORT)
                .forStatusCode(200)
                .forResponsePredicate(response -> response.matches("(?s).*\"ready\"\\s*:\\s*true.*"))
                .withStartupTimeout(STARTUP_TIMEOUT));

        // Configure ports, env vars and file mounts
        applyAllConfigs();
    }

    /**
     * Finalizes container configuration right before creation, once every {@code with*Config(...)} call
     * the caller is going to make has already happened. Used to conditionally mount the Docker socket,
     * since that decision depends on the final state of all service configurations rather than any single
     * one of them.
     */
    @Override
    protected void configure() {
        super.configure();
    }

    /**
     * Makes the files child containers created in the persistent storage deletable before Floci is stopped.
     */
    @Override
    protected void beforeStop() {
        preparePersistentStorageForCleanup();
    }

    /**
     * Deletes the persistent storage configured via {@link StorageConfig.Builder#randomHostPersistentPath()} once
     * Floci and its sibling containers are gone.
     */
    @Override
    protected void afterStop() {
        deletePersistentStorage();
    }

    /**
     * Returns the configured AWS region. Defaults to {@value DEFAULT_REGION}.
     *
     * @return the AWS region
     */
    public String getRegion() {
        return getEnvMap().getOrDefault("FLOCI_DEFAULT_REGION", DEFAULT_REGION);
    }

    /**
     * Returns the AWS access key to use with this instance. Defaults to {@value DEFAULT_ACCESS_KEY}.
     *
     * @return the access key
     */
    public String getAccessKey() {
        return DEFAULT_ACCESS_KEY;
    }

    /**
     * Returns the AWS secret key to use with this instance. Defaults to {@value DEFAULT_SECRET_KEY}.
     *
     * @return the secret key
     */
    public String getSecretKey() {
        return DEFAULT_SECRET_KEY;
    }

    /**
     * Sets the AWS region for this Floci instance.
     *
     * @param region the AWS region (e.g. {@code eu-west-1})
     * @return this container instance
     */
    public FlociContainer withRegion(String region) {
        return withEnv("FLOCI_DEFAULT_REGION", region);
    }

    /**
     * Returns the configured default availability zone. Defaults to {@value DEFAULT_AVAILABILITY_ZONE}.
     *
     * @return the availability zone
     */
    public String getDefaultAvailabilityZone() {
        return getEnvMap().getOrDefault("FLOCI_DEFAULT_AVAILABILITY_ZONE", DEFAULT_AVAILABILITY_ZONE);
    }

    /**
     * Sets the default availability zone for this Floci instance.
     *
     * @param availabilityZone the availability zone (e.g. {@code eu-west-1a})
     * @return this container instance
     */
    public FlociContainer withDefaultAvailabilityZone(String availabilityZone) {
        return withEnv("FLOCI_DEFAULT_AVAILABILITY_ZONE", availabilityZone);
    }

    /**
     * Returns the configured default AWS account ID. Defaults to {@value DEFAULT_ACCOUNT_ID}.
     *
     * @return the account ID
     */
    public String getDefaultAccountId() {
        return getEnvMap().getOrDefault("FLOCI_DEFAULT_ACCOUNT_ID", DEFAULT_ACCOUNT_ID);
    }

    /**
     * Sets the default AWS account ID for this Floci instance.
     *
     * @param accountId the AWS account ID (e.g. {@code 123456789012})
     * @return this container instance
     */
    public FlociContainer withDefaultAccountId(String accountId) {
        return withEnv("FLOCI_DEFAULT_ACCOUNT_ID", accountId);
    }

    /**
     * Sets the log level for this Floci instance. Defaults to {@link Level#WARN}.
     *
     * @param logLevel the log level
     * @return this container instance
     */
    @Override
    public FlociContainer withLogLevel(Level logLevel) {
        return super.withLogLevel(logLevel);
    }

    /**
     * Returns the path, inside the container, of the shared mock-response configuration file used by the
     * fixed-stub AI services (Textract, Comprehend, Rekognition, Translate) to return a caller-configured response
     * instead of their default canned stub.
     *
     * <p>The path is either the one passed to {@link #withAiMockConfigFile(String)} verbatim, or a
     * generated path pointing at the file whose content was passed to {@link #withAiMockConfig(String)}.
     *
     * @return the container path of the AI mock configuration file, or {@link Optional#empty()} if none is configured
     */
    public Optional<String> getAiMockConfigFile() {
        return Optional.ofNullable(aiMockConfigFile);
    }

    /**
     * Returns the raw AI mock configuration file content supplied via {@link #withAiMockConfig(String)},
     * if any. When present, this content is copied into the container at {@link #getAiMockConfigFile()}.
     *
     * @return the AI mock configuration content, or {@link Optional#empty()} if it was not configured by content
     */
    public Optional<String> getAiMockConfig() {
        return Optional.ofNullable(aiMockConfig);
    }

    /**
     * Sets the path, inside the container, of a shared mock-response configuration file that already exists
     * in the container (for example one added through a volume or another {@code withCopy*} call). The
     * fixed-stub AI services (Textract, Comprehend, Rekognition, Translate) use it to return a caller-configured
     * response instead of their default canned stub.
     *
     * <p>Use {@link #withAiMockConfig(String)} instead to hand over just the file content and let
     * {@link FlociContainer} take care of placing the file into the container.
     *
     * <p>Calling this method clears any content previously set via {@link #withAiMockConfig(String)}.
     *
     * @param aiMockConfigFile the container path of the AI mock configuration file
     * @return this container instance
     */
    public FlociContainer withAiMockConfigFile(String aiMockConfigFile) {
        this.aiMockConfigFile = aiMockConfigFile;
        this.aiMockConfig = null;
        return withEnv("FLOCI_AI_MOCK_CONFIG_FILE", aiMockConfigFile);
    }

    /**
     * Sets the content of the shared mock-response configuration file used by the fixed-stub AI services
     * (Textract, Comprehend, Rekognition, Translate) to return a caller-configured response instead of their default
     * canned stub.
     *
     * <p>The content is copied into the container under a generated, randomized path, which is then used
     * as {@link #getAiMockConfigFile()}. Callers therefore do not need to manage any files themselves.
     *
     * <p>Calling this method clears any path previously set via {@link #withAiMockConfigFile(String)}.
     *
     * @param aiMockConfig the AI mock configuration file content
     * @return this container instance
     */
    public FlociContainer withAiMockConfig(String aiMockConfig) {
        this.aiMockConfig = aiMockConfig;
        this.aiMockConfigFile = AI_MOCK_CONFIG_FILE_PREFIX + UUID.randomUUID() + AI_MOCK_CONFIG_FILE_SUFFIX;
        withCopyToContainer(Transferable.of(aiMockConfig), aiMockConfigFile);
        return withEnv("FLOCI_AI_MOCK_CONFIG_FILE", aiMockConfigFile);
    }

    /**
     * Configures a dedicated Docker network for this container that will be used by Floci itself and by all
     * services, that spin up additional containers like RDS, Lambda or ElasticCache.
     *
     * @return this container instance
     */
    @Override
    public FlociContainer withDedicatedNetwork() {
        return super.withDedicatedNetwork();
    }

    /**
     * Disables all services in this Floci container. This is useful for testing scenarios where you want to start the
     * container without any services running and without any ports being exposed (e.g. to improve startup performance)
     *
     * @return this container instance
     */
    @Override
    public FlociContainer disableAllServices() {
        return super.disableAllServices();
    }

    /**
     * Overrides whether the host Docker socket is mounted into the container, bypassing auto-detection.
     *
     * <p>By default (this method never called), the socket is mounted only if at least one currently
     * enabled service actually needs it (e.g. RDS, Lambda, ElastiCache — see
     * {@link io.floci.testcontainers.config.services.AbstractServiceConfig#requiresDockerSocket()}).
     * Calling this method overrides that auto-detection entirely, regardless of which services are
     * enabled:
     *
     * <ul>
     *     <li>{@code withDockerSocket(false)} — never mount the socket. Useful on hosts where mounting
     *     it is broken or undesired (e.g. rootless Podman with SELinux, see
     *     <a href="https://github.com/floci-io/testcontainers-floci/issues/223">#223</a>) and no
     *     Docker-backed service is actually required.</li>
     *     <li>{@code withDockerSocket(true)} — always mount the socket, even if no currently enabled
     *     service is detected as needing it.</li>
     * </ul>
     *
     * @param enabled {@code true} to always mount the Docker socket, {@code false} to never mount it
     * @return this container instance
     */
    @Override
    public FlociContainer withDockerSocket(boolean enabled) {
        return super.withDockerSocket(enabled);
    }

    /**
     * Returns the TLS configuration.
     *
     * @return the TLS configuration
     */
    public TlsConfig getTlsConfig() {
        return tlsConfig;
    }

    /**
     * Configures TLS/HTTPS for the Floci server.
     *
     * <p>By default, a server certificate issued by Floci's local root CA is auto-generated; clients
     * trust that CA (served at {@code GET /_floci/ca.pem}), not the leaf. To use a custom certificate,
     * provide paths to the PEM certificate and private key files:
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTlsConfig(c -> c.enabled(true).certPath("/certs/server.crt").keyPath("/certs/server.key"));
     * }</pre>
     *
     * <p>To enable TLS with an auto-generated certificate:
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTlsConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TlsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTlsConfig(Consumer<TlsConfig.Builder> configurer) {
        TlsConfig.Builder builder = tlsConfig.toBuilder();
        configurer.accept(builder);
        this.tlsConfig = builder.build();
        // Some service ports and env vars (e.g. IoT's MQTT over TLS listener) only apply while TLS is enabled
        applyAllConfigs();
        return this;
    }

    /**
     * Returns the storage configuration.
     *
     * @return the storage configuration
     */
    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    /**
     * Configures storage-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withStorageConfig(c -> c.pruneVolumesOnDelete(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link StorageConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withStorageConfig(Consumer<StorageConfig.Builder> configurer) {
        StorageConfig.Builder builder = storageConfig.toBuilder();
        configurer.accept(builder);
        this.storageConfig = builder.build();
        storageConfig.getHostPersistentPath().ifPresent(path ->
                withFileSystemBind(path.toString(), "/app/data", BindMode.READ_WRITE));
        storageConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current DuckDB configuration. Defaults to the default image and no custom URL.
     *
     * @return the DuckDB configuration
     */
    public DuckDbConfig getDuckDbConfig() {
        return duckDbConfig;
    }

    /**
     * Configures DuckDB-specific settings such as custom URL or default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDuckDbConfig(c -> c
     *         .url("http://duckdb:8080")
     *         .defaultImage("floci/floci-duck:x.y.z"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DuckDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDuckDbConfig(Consumer<DuckDbConfig.Builder> configurer) {
        DuckDbConfig.Builder builder = duckDbConfig.toBuilder();
        configurer.accept(builder);
        this.duckDbConfig = builder.build();
        duckDbConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current security configuration. Defaults to CORS headers enabled and no extra CORS lists.
     *
     * @return the security configuration
     */
    public SecurityConfig getSecurityConfig() {
        return securityConfig;
    }

    /**
     * Configures security settings such as CORS allowed origins, headers, and expose headers, whether JWT
     * issuers may live on private addresses, and whether Floci may listen outside loopback.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSecurityConfig(c -> c
     *         .extraCorsAllowedOrigins(List.of("https://example.com"))
     *         .disableCorsHeaders(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SecurityConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSecurityConfig(Consumer<SecurityConfig.Builder> configurer) {
        SecurityConfig.Builder builder = securityConfig.toBuilder();
        configurer.accept(builder);
        this.securityConfig = builder.build();
        securityConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current protocols configuration.
     *
     * @return the protocols configuration
     */
    public ProtocolsConfig getProtocolsConfig() {
        return protocolsConfig;
    }

    /**
     * Configures protocol-related settings such as strict RPC protocol claiming.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withProtocolsConfig(c -> c.strictClaiming(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ProtocolsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withProtocolsConfig(Consumer<ProtocolsConfig.Builder> configurer) {
        ProtocolsConfig.Builder builder = protocolsConfig.toBuilder();
        configurer.accept(builder);
        this.protocolsConfig = builder.build();
        protocolsConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current auth configuration.
     *
     * @return the auth configuration
     */
    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    /**
     * Configures authentication-related settings such as SigV4 signature validation.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAuthConfig(c -> c.validateSignatures(true));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link AuthConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAuthConfig(Consumer<AuthConfig.Builder> configurer) {
        AuthConfig.Builder builder = authConfig.toBuilder();
        configurer.accept(builder);
        this.authConfig = builder.build();
        authConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current init hooks configuration.
     *
     * @return the init hooks configuration
     */
    public InitHooksConfig getInitHooksConfig() {
        return initHooksConfig;
    }

    /**
     * Configures lifecycle init hook settings such as the shell executable and timeouts.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withInitHooksConfig(c -> c.timeoutSeconds(60));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link InitHooksConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withInitHooksConfig(Consumer<InitHooksConfig.Builder> configurer) {
        InitHooksConfig.Builder builder = initHooksConfig.toBuilder();
        configurer.accept(builder);
        this.initHooksConfig = builder.build();
        initHooksConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current partitions configuration. Defaults to deriving the AWS partition from the
     * default region, serving every enabled service in every partition and refusing unknown regions.
     *
     * @return the partitions configuration
     */
    public PartitionsConfig getPartitionsConfig() {
        return partitionsConfig;
    }

    /**
     * Configures which AWS partition Floci serves and how strictly requests are checked against it.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRegion("cn-north-1")
     *     .withPartitionsConfig(c -> c.id("aws-cn").strict(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PartitionsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withPartitionsConfig(Consumer<PartitionsConfig.Builder> configurer) {
        PartitionsConfig.Builder builder = partitionsConfig.toBuilder();
        configurer.accept(builder);
        this.partitionsConfig = builder.build();
        partitionsConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the current network configuration. Defaults to security-group enforcement disabled.
     *
     * @return the network configuration
     */
    public NetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    /**
     * Configures network settings such as security-group enforcement for Docker-backed EC2 instances and
     * ECS {@code awsvpc} tasks.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withNetworkConfig(c -> c.securityGroupEnforcementEnabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link NetworkConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withNetworkConfig(Consumer<NetworkConfig.Builder> configurer) {
        NetworkConfig.Builder builder = networkConfig.toBuilder();
        configurer.accept(builder);
        this.networkConfig = builder.build();
        networkConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * ACM-specific settings such as certificate validation timing
     *
     * @return the ACM configuration
     */
    public AcmConfig getAcmConfig() {
        return acmConfig.get();
    }

    /**
     * Configures acm-specific settings such as certificate validation timing.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAcmConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AcmConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAcmConfig(Consumer<AcmConfig.Builder> configurer) {
        return updateServiceConfig(acmConfig, configurer);
    }

    /**
     * API Gateway-specific settings
     *
     * @return the API Gateway configuration
     */
    public ApiGatewayConfig getApiGatewayConfig() {
        return apiGatewayConfig.get();
    }

    /**
     * Configures api gateway-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withApiGatewayConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ApiGatewayConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withApiGatewayConfig(Consumer<ApiGatewayConfig.Builder> configurer) {
        return updateServiceConfig(apiGatewayConfig, configurer);
    }

    /**
     * API Gateway V2-specific settings
     *
     * @return the API Gateway V2 configuration
     */
    public ApiGatewayV2Config getApiGatewayV2Config() {
        return apiGatewayV2Config.get();
    }

    /**
     * Configures api gateway v2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withApiGatewayV2Config(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ApiGatewayV2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withApiGatewayV2Config(Consumer<ApiGatewayV2Config.Builder> configurer) {
        return updateServiceConfig(apiGatewayV2Config, configurer);
    }

    /**
     * AppConfig-specific settings
     *
     * @return the AppConfig configuration
     */
    public AppConfigConfig getAppConfigConfig() {
        return appConfigConfig.get();
    }

    /**
     * Configures appconfig-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAppConfigConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AppConfigConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAppConfigConfig(Consumer<AppConfigConfig.Builder> configurer) {
        return updateServiceConfig(appConfigConfig, configurer);
    }

    /**
     * AppConfig Data-specific settings
     *
     * @return the AppConfig Data configuration
     */
    public AppConfigDataConfig getAppConfigDataConfig() {
        return appConfigDataConfig.get();
    }

    /**
     * Configures appconfig data-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAppConfigDataConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AppConfigDataConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAppConfigDataConfig(Consumer<AppConfigDataConfig.Builder> configurer) {
        return updateServiceConfig(appConfigDataConfig, configurer);
    }

    /**
     * AppSync-specific settings.
     *
     * @return the AppSync configuration
     */
    public AppSyncConfig getAppSyncConfig() {
        return appSyncConfig.get();
    }

    /**
     * Configures AppSync-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAppSyncConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AppSyncConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAppSyncConfig(Consumer<AppSyncConfig.Builder> configurer) {
        return updateServiceConfig(appSyncConfig, configurer);
    }

    /**
     * CloudFormation-specific settings
     *
     * @return the CloudFormation configuration
     */
    public CloudFormationConfig getCloudFormationConfig() {
        return cloudFormationConfig.get();
    }

    /**
     * Configures cloudformation-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudFormationConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudFormationConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudFormationConfig(Consumer<CloudFormationConfig.Builder> configurer) {
        return updateServiceConfig(cloudFormationConfig, configurer);
    }

    /**
     * CloudFront-specific settings such as domain suffix
     *
     * @return the CloudFront configuration
     */
    public CloudFrontConfig getCloudFrontConfig() {
        return cloudFrontConfig.get();
    }

    /**
     * Configures cloudfront-specific settings such as domain suffix.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudFrontConfig(c -> c.domainSuffix("example.net"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudFrontConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudFrontConfig(Consumer<CloudFrontConfig.Builder> configurer) {
        return updateServiceConfig(cloudFrontConfig, configurer);
    }

    /**
     * CloudMap-specific settings such as operation completion delay.
     *
     * @return the CloudMap configuration
     */
    public CloudMapConfig getCloudMapConfig() {
        return cloudMapConfig.get();
    }

    /**
     * Configures CloudMap-specific settings such as operation completion delay.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudMapConfig(c -> c.operationCompletionDelaySeconds(5));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudMapConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudMapConfig(Consumer<CloudMapConfig.Builder> configurer) {
        return updateServiceConfig(cloudMapConfig, configurer);
    }

    /**
     * CloudWatch Logs-specific settings such as query event limits
     *
     * @return the CloudWatch Logs configuration
     */
    public CloudWatchLogsConfig getCloudWatchLogsConfig() {
        return cloudWatchLogsConfig.get();
    }

    /**
     * Configures cloudwatch logs-specific settings such as query event limits.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudWatchLogsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudWatchLogsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudWatchLogsConfig(Consumer<CloudWatchLogsConfig.Builder> configurer) {
        return updateServiceConfig(cloudWatchLogsConfig, configurer);
    }

    /**
     * CloudWatch Metrics-specific settings
     *
     * @return the CloudWatch Metrics configuration
     */
    public CloudWatchMetricsConfig getCloudWatchMetricsConfig() {
        return cloudWatchMetricsConfig.get();
    }

    /**
     * Configures cloudwatch metrics-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudWatchMetricsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudWatchMetricsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudWatchMetricsConfig(Consumer<CloudWatchMetricsConfig.Builder> configurer) {
        return updateServiceConfig(cloudWatchMetricsConfig, configurer);
    }

    /**
     * Cognito-specific settings
     *
     * @return the Cognito configuration
     */
    public CognitoConfig getCognitoConfig() {
        return cognitoConfig.get();
    }

    /**
     * Configures cognito-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCognitoConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CognitoConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCognitoConfig(Consumer<CognitoConfig.Builder> configurer) {
        return updateServiceConfig(cognitoConfig, configurer);
    }

    /**
     * AWS Config-specific settings
     *
     * @return the AWS Config configuration
     */
    public ConfigServiceConfig getConfigServiceConfig() {
        return configServiceConfig.get();
    }

    /**
     * Configures AWS Config-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withConfigServiceConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ConfigServiceConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withConfigServiceConfig(Consumer<ConfigServiceConfig.Builder> configurer) {
        return updateServiceConfig(configServiceConfig, configurer);
    }

    /**
     * DynamoDB-specific settings
     *
     * @return the DynamoDB configuration
     */
    public DynamoDbConfig getDynamoDbConfig() {
        return dynamoDbConfig.get();
    }

    /**
     * Configures dynamodb-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDynamoDbConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DynamoDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDynamoDbConfig(Consumer<DynamoDbConfig.Builder> configurer) {
        return updateServiceConfig(dynamoDbConfig, configurer);
    }

    /**
     * EC2-specific settings
     *
     * @return the EC2 configuration
     */
    public Ec2Config getEc2Config() {
        return ec2Config.get();
    }

    /**
     * Configures ec2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEc2Config(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link Ec2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEc2Config(Consumer<Ec2Config.Builder> configurer) {
        return updateServiceConfig(ec2Config, configurer);
    }

    /**
     * ECR-specific settings such as registry ports and TLS configuration
     *
     * @return the ECR configuration
     */
    public EcrConfig getEcrConfig() {
        return ecrConfig.get();
    }

    /**
     * Configures ECR-specific settings such as registry ports and TLS configuration.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEcrConfig(c -> c
     *         .registryPortRange(5000, 100)
     *         .registryImage("registry:2"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EcrConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEcrConfig(Consumer<EcrConfig.Builder> configurer) {
        return updateServiceConfig(ecrConfig, configurer);
    }

    /**
     * ECS-specific settings such as mock mode and default task resources
     *
     * @return the ECS configuration
     */
    public EcsConfig getEcsConfig() {
        return ecsConfig.get();
    }

    /**
     * Configures ECS-specific settings such as mock mode and default task resources.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEcsConfig(c -> c
     *         .mock(true)
     *         .defaultMemoryMb(1024));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EcsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEcsConfig(Consumer<EcsConfig.Builder> configurer) {
        return updateServiceConfig(ecsConfig, configurer);
    }

    /**
     * ElastiCache-specific settings such as proxy ports and default image
     *
     * @return the ElastiCache configuration
     */
    public ElastiCacheConfig getElastiCacheConfig() {
        return elastiCacheConfig.get();
    }

    /**
     * Configures ElastiCache-specific settings such as proxy ports and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withElastiCacheConfig(c -> c
     *         .proxyPortRange(6379, 21)
     *         .defaultImage("valkey/valkey:8"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ElastiCacheConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withElastiCacheConfig(Consumer<ElastiCacheConfig.Builder> configurer) {
        return updateServiceConfig(elastiCacheConfig, configurer);
    }

    /**
     * EventBridge-specific settings
     *
     * @return the EventBridge configuration
     */
    public EventBridgeConfig getEventBridgeConfig() {
        return eventBridgeConfig.get();
    }

    /**
     * Configures eventbridge-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEventBridgeConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EventBridgeConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEventBridgeConfig(Consumer<EventBridgeConfig.Builder> configurer) {
        return updateServiceConfig(eventBridgeConfig, configurer);
    }

    /**
     * IAM-specific settings
     *
     * @return the IAM configuration
     */
    public IamConfig getIamConfig() {
        return iamConfig.get();
    }

    /**
     * Configures iam-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withIamConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IamConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withIamConfig(Consumer<IamConfig.Builder> configurer) {
        return updateServiceConfig(iamConfig, configurer);
    }

    /**
     * Kinesis-specific settings
     *
     * @return the Kinesis configuration
     */
    public KinesisConfig getKinesisConfig() {
        return kinesisConfig.get();
    }

    /**
     * Configures kinesis-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withKinesisConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KinesisConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withKinesisConfig(Consumer<KinesisConfig.Builder> configurer) {
        return updateServiceConfig(kinesisConfig, configurer);
    }

    /**
     * KMS-specific settings
     *
     * @return the KMS configuration
     */
    public KmsConfig getKmsConfig() {
        return kmsConfig.get();
    }

    /**
     * Configures kms-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withKmsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KmsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withKmsConfig(Consumer<KmsConfig.Builder> configurer) {
        return updateServiceConfig(kmsConfig, configurer);
    }

    /**
     * Lambda-specific settings such as the Runtime API port range
     *
     * @return the Lambda configuration
     */
    public LambdaConfig getLambdaConfig() {
        return lambdaConfig.get();
    }

    /**
     * Configures Lambda-specific settings such as the Runtime API port range.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withLambdaConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link LambdaConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withLambdaConfig(Consumer<LambdaConfig.Builder> configurer) {
        return updateServiceConfig(lambdaConfig, configurer);
    }

    /**
     * OpenSearch-specific settings such as mode, proxy ports and default image
     *
     * @return the OpenSearch configuration
     */
    public OpenSearchConfig getOpenSearchConfig() {
        return openSearchConfig.get();
    }

    /**
     * Configures OpenSearch-specific settings such as mode, proxy ports and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withOpenSearchConfig(c -> c
     *         .mock(true)
     *         .proxyPortRange(9400, 100));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link OpenSearchConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withOpenSearchConfig(Consumer<OpenSearchConfig.Builder> configurer) {
        return updateServiceConfig(openSearchConfig, configurer);
    }

    /**
     * RDS-specific settings such as proxy ports and default database images.
     *
     * @return the RDS configuration
     */
    public RdsConfig getRdsConfig() {
        return rdsConfig.get();
    }

    /**
     * Configures RDS-specific settings such as proxy ports and default database images.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRdsConfig(c -> c
     *         .proxyPortRange(7000, 7099)
     *         .defaultPostgresImage("postgres:16-alpine"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RdsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRdsConfig(Consumer<RdsConfig.Builder> configurer) {
        return updateServiceConfig(rdsConfig, configurer);
    }

    /**
     * S3-specific settings such as presigned URL expiry
     *
     * @return the S3 configuration
     */
    public S3Config getS3Config() {
        return s3Config.get();
    }

    /**
     * Configures s3-specific settings such as presigned url expiry.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withS3Config(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link S3Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withS3Config(Consumer<S3Config.Builder> configurer) {
        return updateServiceConfig(s3Config, configurer);
    }

    /**
     * Scheduler-specific settings
     *
     * @return the Scheduler configuration
     */
    public SchedulerConfig getSchedulerConfig() {
        return schedulerConfig.get();
    }

    /**
     * Configures scheduler-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSchedulerConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SchedulerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSchedulerConfig(Consumer<SchedulerConfig.Builder> configurer) {
        return updateServiceConfig(schedulerConfig, configurer);
    }

    /**
     * Secrets Manager-specific settings such as recovery window
     *
     * @return the Secrets Manager configuration
     */
    public SecretsManagerConfig getSecretsManagerConfig() {
        return secretsManagerConfig.get();
    }

    /**
     * Configures secrets manager-specific settings such as recovery window.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSecretsManagerConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SecretsManagerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSecretsManagerConfig(Consumer<SecretsManagerConfig.Builder> configurer) {
        return updateServiceConfig(secretsManagerConfig, configurer);
    }

    /**
     * SES-specific settings
     *
     * @return the SES configuration
     */
    public SesConfig getSesConfig() {
        return sesConfig.get();
    }

    /**
     * Configures ses-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSesConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SesConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSesConfig(Consumer<SesConfig.Builder> configurer) {
        return updateServiceConfig(sesConfig, configurer);
    }

    /**
     * SNS-specific settings
     *
     * @return the SNS configuration
     */
    public SnsConfig getSnsConfig() {
        return snsConfig.get();
    }

    /**
     * Configures sns-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSnsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SnsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSnsConfig(Consumer<SnsConfig.Builder> configurer) {
        return updateServiceConfig(snsConfig, configurer);
    }

    /**
     * SQS-specific settings such as visibility timeout and message size limits
     *
     * @return the SQS configuration
     */
    public SqsConfig getSqsConfig() {
        return sqsConfig.get();
    }

    /**
     * Configures sqs-specific settings such as visibility timeout and message size limits.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSqsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SqsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSqsConfig(Consumer<SqsConfig.Builder> configurer) {
        return updateServiceConfig(sqsConfig, configurer);
    }

    /**
     * SSM-specific settings such as parameter history limits
     *
     * @return the SSM configuration
     */
    public SsmConfig getSsmConfig() {
        return ssmConfig.get();
    }

    /**
     * Configures ssm-specific settings such as parameter history limits.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSsmConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SsmConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSsmConfig(Consumer<SsmConfig.Builder> configurer) {
        return updateServiceConfig(ssmConfig, configurer);
    }

    /**
     * Step Functions-specific settings
     *
     * @return the Step Functions configuration
     */
    public StepFunctionsConfig getStepFunctionsConfig() {
        return stepFunctionsConfig.get();
    }

    /**
     * Configures step functions-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withStepFunctionsConfig(c -> c...);
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link StepFunctionsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withStepFunctionsConfig(Consumer<StepFunctionsConfig.Builder> configurer) {
        return updateServiceConfig(stepFunctionsConfig, configurer);
    }

    /**
     * MSK-specific settings such as mock mode and default image.
     *
     * @return the MSK configuration
     */
    public MskConfig getMskConfig() {
        return mskConfig.get();
    }

    /**
     * Configures MSK-specific settings such as mock mode and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withMskConfig(c -> c
     *         .mock(true)
     *         .defaultImage("redpandadata/redpanda:v24")
     *         .kafkaHostPortRange(9300, 10));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MskConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withMskConfig(Consumer<MskConfig.Builder> configurer) {
        return updateServiceConfig(mskConfig, configurer);
    }

    /**
     * Firehose-specific settings.
     *
     * @return the Firehose configuration
     */
    public FirehoseConfig getFirehoseConfig() {
        return firehoseConfig.get();
    }

    /**
     * Configures firehose-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withFirehoseConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FirehoseConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withFirehoseConfig(Consumer<FirehoseConfig.Builder> configurer) {
        return updateServiceConfig(firehoseConfig, configurer);
    }

    /**
     * Athena-specific settings.
     *
     * @return the Athena configuration
     */
    public AthenaConfig getAthenaConfig() {
        return athenaConfig.get();
    }

    /**
     * Configures athena-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAthenaConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AthenaConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAthenaConfig(Consumer<AthenaConfig.Builder> configurer) {
        return updateServiceConfig(athenaConfig, configurer);
    }

    /**
     * Glue-specific settings.
     *
     * @return the Glue configuration
     */
    public GlueConfig getGlueConfig() {
        return glueConfig.get();
    }

    /**
     * Configures glue-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withGlueConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link GlueConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withGlueConfig(Consumer<GlueConfig.Builder> configurer) {
        return updateServiceConfig(glueConfig, configurer);
    }

    /**
     * Resource Groups Tagging-specific settings.
     *
     * @return the Resource Groups Tagging configuration
     */
    public ResourceGroupsTaggingConfig getResourceGroupsTaggingConfig() {
        return resourceGroupsTaggingConfig.get();
    }

    /**
     * Configures resource groups tagging-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withResourceGroupsTaggingConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ResourceGroupsTaggingConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withResourceGroupsTaggingConfig(Consumer<ResourceGroupsTaggingConfig.Builder> configurer) {
        return updateServiceConfig(resourceGroupsTaggingConfig, configurer);
    }

    /**
     * Bedrock AgentCore-specific settings.
     *
     * @return the Bedrock AgentCore configuration
     */
    public BedrockAgentCoreConfig getBedrockAgentCoreConfig() {
        return bedrockAgentCoreConfig.get();
    }

    /**
     * Configures Bedrock AgentCore-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBedrockAgentCoreConfig(c -> c.validateRuntimeExists(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BedrockAgentCoreConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBedrockAgentCoreConfig(Consumer<BedrockAgentCoreConfig.Builder> configurer) {
        return updateServiceConfig(bedrockAgentCoreConfig, configurer);
    }

    /**
     * Bedrock AgentCore Control-specific settings.
     *
     * @return the Bedrock AgentCore Control configuration
     */
    public BedrockAgentCoreControlConfig getBedrockAgentCoreControlConfig() {
        return bedrockAgentCoreControlConfig.get();
    }

    /**
     * Configures Bedrock AgentCore Control-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBedrockAgentCoreControlConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BedrockAgentCoreControlConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBedrockAgentCoreControlConfig(Consumer<BedrockAgentCoreControlConfig.Builder> configurer) {
        return updateServiceConfig(bedrockAgentCoreControlConfig, configurer);
    }

    /**
     * Bedrock Runtime-specific settings.
     *
     * @return the Bedrock Runtime configuration
     */
    public BedrockRuntimeConfig getBedrockRuntimeConfig() {
        return bedrockRuntimeConfig.get();
    }

    /**
     * Configures bedrock runtime-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBedrockRuntimeConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BedrockRuntimeConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBedrockRuntimeConfig(Consumer<BedrockRuntimeConfig.Builder> configurer) {
        return updateServiceConfig(bedrockRuntimeConfig, configurer);
    }

    /**
     * Pipes-specific settings.
     *
     * @return the Pipes configuration
     */
    public PipesConfig getPipesConfig() {
        return pipesConfig.get();
    }

    /**
     * Configures pipes-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withPipesConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PipesConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withPipesConfig(Consumer<PipesConfig.Builder> configurer) {
        return updateServiceConfig(pipesConfig, configurer);
    }

    /**
     * EKS-specific settings such as mock mode, provider, API server ports and default image.
     *
     * @return the EKS configuration
     */
    public EksConfig getEksConfig() {
        return eksConfig.get();
    }

    /**
     * Configures EKS-specific settings such as mock mode, provider, API server ports and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEksConfig(c -> c
     *         .mock(true)
     *         .apiServerPortRange(6500, 100));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EksConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEksConfig(Consumer<EksConfig.Builder> configurer) {
        return updateServiceConfig(eksConfig, configurer);
    }

    /**
     * CodeBuild-specific settings
     *
     * @return the CodeBuild configuration
     */
    public CodeBuildConfig getCodeBuildConfig() {
        return codeBuildConfig.get();
    }

    /**
     * Configures CodeBuild-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCodeBuildConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CodeBuildConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCodeBuildConfig(Consumer<CodeBuildConfig.Builder> configurer) {
        return updateServiceConfig(codeBuildConfig, configurer);
    }

    /**
     * CodeDeploy-specific settings
     *
     * @return the CodeDeploy configuration
     */
    public CodeDeployConfig getCodeDeployConfig() {
        return codeDeployConfig.get();
    }

    /**
     * Configures CodeDeploy-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCodeDeployConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CodeDeployConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCodeDeployConfig(Consumer<CodeDeployConfig.Builder> configurer) {
        return updateServiceConfig(codeDeployConfig, configurer);
    }

    /**
     * ELBv2-specific settings
     *
     * @return the ELBv2 configuration
     */
    public ElbV2Config getElbV2Config() {
        return elbV2Config.get();
    }

    /**
     * Configures ELBv2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withElbV2Config(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ElbV2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withElbV2Config(Consumer<ElbV2Config.Builder> configurer) {
        return updateServiceConfig(elbV2Config, configurer);
    }

    /**
     * Backup-specific settings such as job completion delay.
     *
     * @return the Backup configuration
     */
    public BackupConfig getBackupConfig() {
        return backupConfig.get();
    }

    /**
     * Configures Backup-specific settings such as job completion delay.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBackupConfig(c -> c.jobCompletionDelaySeconds(5));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BackupConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBackupConfig(Consumer<BackupConfig.Builder> configurer) {
        return updateServiceConfig(backupConfig, configurer);
    }

    /**
     * Transfer Family-specific settings.
     *
     * @return the Transfer Family configuration
     */
    public TransferFamilyConfig getTransferFamilyConfig() {
        return transferFamilyConfig.get();
    }

    /**
     * Configures Transfer Family-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTransferConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TransferFamilyConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTransferFamilyConfig(Consumer<TransferFamilyConfig.Builder> configurer) {
        return updateServiceConfig(transferFamilyConfig, configurer);
    }

    /**
     * Route 53-specific settings such as default nameservers.
     *
     * @return the Route 53 configuration
     */
    public Route53Config getRoute53Config() {
        return route53Config.get();
    }

    /**
     * Configures Route 53-specific settings such as default nameservers.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRoute53Config(c -> c.defaultNameserver1("ns1.example.com"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link Route53Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRoute53Config(Consumer<Route53Config.Builder> configurer) {
        return updateServiceConfig(route53Config, configurer);
    }

    /**
     * Textract-specific settings.
     *
     * @return the Textract configuration
     */
    public TextractConfig getTextractConfig() {
        return textractConfig.get();
    }

    /**
     * Configures Textract-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTextractConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TextractConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTextractConfig(Consumer<TextractConfig.Builder> configurer) {
        return updateServiceConfig(textractConfig, configurer);
    }

    /**
     * Pricing-specific settings such as an optional snapshot path override.
     *
     * @return the Pricing configuration
     */
    public PricingConfig getPricingConfig() {
        return pricingConfig.get();
    }

    /**
     * Configures Pricing-specific settings such as an optional snapshot path override.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withPricingConfig(c -> c.snapshotPath("/data/pricing"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PricingConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withPricingConfig(Consumer<PricingConfig.Builder> configurer) {
        return updateServiceConfig(pricingConfig, configurer);
    }

    /**
     * Neptune-specific settings such as proxy ports and default image.
     *
     * @return the Neptune configuration
     */
    public NeptuneConfig getNeptuneConfig() {
        return neptuneConfig.get();
    }

    /**
     * Configures Neptune-specific settings such as proxy ports and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withNeptuneConfig(c -> c
     *         .proxyPortRange(8182, 8282)
     *         .defaultImage("tinkerpop/gremlin-server:3.7.3"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link NeptuneConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withNeptuneConfig(Consumer<NeptuneConfig.Builder> configurer) {
        return updateServiceConfig(neptuneConfig, configurer);
    }

    /**
     * Cost Explorer-specific settings such as the synthetic monthly credit.
     *
     * @return the Cost Explorer configuration
     */
    public CostExplorerConfig getCostExplorerConfig() {
        return costExplorerConfig.get();
    }

    /**
     * Configures Cost Explorer-specific settings such as the synthetic monthly credit.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCostExplorerConfig(c -> c.creditUsdMonthly(100.0));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CostExplorerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCostExplorerConfig(Consumer<CostExplorerConfig.Builder> configurer) {
        return updateServiceConfig(costExplorerConfig, configurer);
    }

    /**
     * CUR (Cost and Usage Reports)-specific settings such as emit mode and staging bucket.
     *
     * @return the CUR configuration
     */
    public CurConfig getCurConfig() {
        return curConfig.get();
    }

    /**
     * Configures CUR (Cost and Usage Reports)-specific settings such as emit mode and staging bucket.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCurConfig(c -> c.emitMode("daily").stagingBucket("my-bucket"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CurConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCurConfig(Consumer<CurConfig.Builder> configurer) {
        return updateServiceConfig(curConfig, configurer);
    }

    /**
     * BCM Data Exports-specific settings such as emit mode.
     *
     * @return the BCM Data Exports configuration
     */
    public BcmDataExportsConfig getBcmDataExportsConfig() {
        return bcmDataExportsConfig.get();
    }

    /**
     * Configures BCM Data Exports-specific settings such as emit mode.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBcmDataExportsConfig(c -> c.emitMode("daily"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BcmDataExportsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBcmDataExportsConfig(Consumer<BcmDataExportsConfig.Builder> configurer) {
        return updateServiceConfig(bcmDataExportsConfig, configurer);
    }

    /**
     * CloudTrail-specific settings.
     *
     * @return the CloudTrail configuration
     */
    public CloudTrailConfig getCloudTrailConfig() {
        return cloudTrailConfig.get();
    }

    /**
     * Configures CloudTrail-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudTrailConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudTrailConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudTrailConfig(Consumer<CloudTrailConfig.Builder> configurer) {
        return updateServiceConfig(cloudTrailConfig, configurer);
    }

    /**
     * Batch-specific settings such as runner mode and Docker network.
     *
     * @return the Batch configuration
     */
    public BatchConfig getBatchConfig() {
        return batchConfig.get();
    }

    /**
     * Configures Batch-specific settings such as runner mode and Docker network.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBatchConfig(c -> c.runnerMode("deferred"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BatchConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBatchConfig(Consumer<BatchConfig.Builder> configurer) {
        return updateServiceConfig(batchConfig, configurer);
    }

    /**
     * RDS Data API-specific settings such as transaction TTL.
     *
     * @return the RDS Data configuration
     */
    public RdsDataConfig getRdsDataConfig() {
        return rdsDataConfig.get();
    }

    /**
     * Configures RDS Data API-specific settings such as transaction TTL.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRdsDataConfig(c -> c.transactionTtlSeconds(300));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RdsDataConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRdsDataConfig(Consumer<RdsDataConfig.Builder> configurer) {
        return updateServiceConfig(rdsDataConfig, configurer);
    }

    /**
     * DocumentDB-specific settings such as mock mode, default image and Docker network.
     *
     * @return the DocumentDB configuration
     */
    public DocumentDbConfig getDocumentDbConfig() {
        return documentDbConfig.get();
    }

    /**
     * Configures DocumentDB-specific settings such as mock mode, default image and Docker network.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDocumentDbConfig(c -> c
     *         .mock(true)
     *         .defaultImage("mongo:8.0"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DocumentDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDocumentDbConfig(Consumer<DocumentDbConfig.Builder> configurer) {
        return updateServiceConfig(documentDbConfig, configurer);
    }

    /**
     * EMR-specific settings such as default release label and cluster startup delay.
     *
     * @return the EMR configuration
     */
    public EmrConfig getEmrConfig() {
        return emrConfig.get();
    }

    /**
     * Configures EMR-specific settings such as default release label and cluster startup delay.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEmrConfig(c -> c.defaultReleaseLabel("emr-7.8.0"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EmrConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEmrConfig(Consumer<EmrConfig.Builder> configurer) {
        return updateServiceConfig(emrConfig, configurer);
    }

    /**
     * WAF V2-specific settings.
     *
     * @return the WAF V2 configuration
     */
    public WafV2Config getWafV2Config() {
        return wafV2Config.get();
    }

    /**
     * Configures WAF V2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withWafV2Config(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link WafV2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withWafV2Config(Consumer<WafV2Config.Builder> configurer) {
        return updateServiceConfig(wafV2Config, configurer);
    }

    /**
     * IoT Core-specific settings such as the embedded MQTT broker.
     *
     * @return the IoT Core configuration
     */
    public IotConfig getIotConfig() {
        return iotConfig.get();
    }

    /**
     * Configures IoT Core-specific settings such as the embedded MQTT broker.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withIotConfig(c -> c
     *         .mqttAutoStart(true)
     *         .mqttPort(1883));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IotConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withIotConfig(Consumer<IotConfig.Builder> configurer) {
        return updateServiceConfig(iotConfig, configurer);
    }

    /**
     * IoT Data Plane-specific settings.
     *
     * @return the IoT Data Plane configuration
     */
    public IotDataConfig getIotDataConfig() {
        return iotDataConfig.get();
    }

    /**
     * Configures IoT Data Plane-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withIotDataConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IotDataConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withIotDataConfig(Consumer<IotDataConfig.Builder> configurer) {
        return updateServiceConfig(iotDataConfig, configurer);
    }

    /**
     * Lightsail-specific settings.
     *
     * @return the Lightsail configuration
     */
    public LightsailConfig getLightsailConfig() {
        return lightsailConfig.get();
    }

    /**
     * Configures Lightsail-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withLightsailConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link LightsailConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withLightsailConfig(Consumer<LightsailConfig.Builder> configurer) {
        return updateServiceConfig(lightsailConfig, configurer);
    }

    /**
     * Cloud Control API-specific settings.
     *
     * @return the Cloud Control API configuration
     */
    public CloudControlConfig getCloudControlConfig() {
        return cloudControlConfig.get();
    }

    /**
     * Configures Cloud Control API-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudControlConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudControlConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudControlConfig(Consumer<CloudControlConfig.Builder> configurer) {
        return updateServiceConfig(cloudControlConfig, configurer);
    }

    /**
     * S3 Vectors-specific settings.
     *
     * @return the S3 Vectors configuration
     */
    public S3VectorsConfig getS3VectorsConfig() {
        return s3VectorsConfig.get();
    }

    /**
     * Configures S3 Vectors-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withS3VectorsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link S3VectorsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withS3VectorsConfig(Consumer<S3VectorsConfig.Builder> configurer) {
        return updateServiceConfig(s3VectorsConfig, configurer);
    }

    /**
     * Elastic Beanstalk-specific settings.
     *
     * @return the Elastic Beanstalk configuration
     */
    public ElasticBeanstalkConfig getElasticBeanstalkConfig() {
        return elasticBeanstalkConfig.get();
    }

    /**
     * Configures Elastic Beanstalk-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withElasticBeanstalkConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ElasticBeanstalkConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withElasticBeanstalkConfig(Consumer<ElasticBeanstalkConfig.Builder> configurer) {
        return updateServiceConfig(elasticBeanstalkConfig, configurer);
    }

    /**
     * CodePipeline-specific settings.
     *
     * @return the CodePipeline configuration
     */
    public CodePipelineConfig getCodePipelineConfig() {
        return codePipelineConfig.get();
    }

    /**
     * Configures CodePipeline-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCodePipelineConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CodePipelineConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCodePipelineConfig(Consumer<CodePipelineConfig.Builder> configurer) {
        return updateServiceConfig(codePipelineConfig, configurer);
    }

    /**
     * Amazon MQ-specific settings such as mock mode and default image.
     *
     * @return the Amazon MQ configuration
     */
    public AmazonMqConfig getAmazonMqConfig() {
        return amazonMqConfig.get();
    }

    /**
     * Configures Amazon MQ-specific settings such as mock mode and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAmazonMqConfig(c -> c
     *         .mock(true)
     *         .defaultImage("rabbitmq:4-management"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AmazonMqConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAmazonMqConfig(Consumer<AmazonMqConfig.Builder> configurer) {
        return updateServiceConfig(amazonMqConfig, configurer);
    }

    /**
     * MemoryDB-specific settings such as mock mode, proxy port range and default image.
     *
     * @return the MemoryDB configuration
     */
    public MemoryDbConfig getMemoryDbConfig() {
        return memoryDbConfig.get();
    }

    /**
     * Configures MemoryDB-specific settings such as mock mode, proxy port range and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withMemoryDbConfig(c -> c
     *         .mock(true)
     *         .proxyPortRange(6400, 20)
     *         .defaultImage("valkey/valkey:9"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MemoryDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withMemoryDbConfig(Consumer<MemoryDbConfig.Builder> configurer) {
        return updateServiceConfig(memoryDbConfig, configurer);
    }

    /**
     * CloudWatch RUM (Real User Monitoring)-specific settings.
     *
     * @return the RUM configuration
     */
    public RumConfig getRumConfig() {
        return rumConfig.get();
    }

    /**
     * Configures CloudWatch RUM (Real User Monitoring)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRumConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RumConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRumConfig(Consumer<RumConfig.Builder> configurer) {
        return updateServiceConfig(rumConfig, configurer);
    }

    /**
     * S3 Tables-specific settings.
     *
     * @return the S3 Tables configuration
     */
    public S3TablesConfig getS3TablesConfig() {
        return s3TablesConfig.get();
    }

    /**
     * Configures S3 Tables-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withS3TablesConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link S3TablesConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withS3TablesConfig(Consumer<S3TablesConfig.Builder> configurer) {
        return updateServiceConfig(s3TablesConfig, configurer);
    }

    /**
     * Application Auto Scaling-specific settings.
     *
     * @return the Application Auto Scaling configuration
     */
    public ApplicationAutoScalingConfig getApplicationAutoScalingConfig() {
        return applicationAutoScalingConfig.get();
    }

    /**
     * Configures Application Auto Scaling-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withApplicationAutoScalingConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ApplicationAutoScalingConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withApplicationAutoScalingConfig(Consumer<ApplicationAutoScalingConfig.Builder> configurer) {
        return updateServiceConfig(applicationAutoScalingConfig, configurer);
    }

    /**
     * SWF (Simple Workflow Service)-specific settings such as the timeout sweep.
     *
     * @return the SWF configuration
     */
    public SwfConfig getSwfConfig() {
        return swfConfig.get();
    }

    /**
     * Configures SWF (Simple Workflow Service)-specific settings such as the timeout sweep.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSwfConfig(c -> c
     *         .timeoutSweepEnabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SwfConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSwfConfig(Consumer<SwfConfig.Builder> configurer) {
        return updateServiceConfig(swfConfig, configurer);
    }

    /**
     * Kinesis Analytics (Managed Service for Apache Flink)-specific settings such as mock mode
     * and default image.
     *
     * @return the Kinesis Analytics configuration
     */
    public KinesisAnalyticsConfig getKinesisAnalyticsConfig() {
        return kinesisAnalyticsConfig.get();
    }

    /**
     * Configures Kinesis Analytics (Managed Service for Apache Flink)-specific settings such as
     * mock mode and default image.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withKinesisAnalyticsConfig(c -> c
     *         .mock(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KinesisAnalyticsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withKinesisAnalyticsConfig(Consumer<KinesisAnalyticsConfig.Builder> configurer) {
        return updateServiceConfig(kinesisAnalyticsConfig, configurer);
    }

    /**
     * MWAA (Managed Workflows for Apache Airflow)-specific settings such as mock mode, proxy
     * port range and supported Airflow versions.
     *
     * @return the MWAA configuration
     */
    public MwaaConfig getMwaaConfig() {
        return mwaaConfig.get();
    }

    /**
     * Configures MWAA (Managed Workflows for Apache Airflow)-specific settings such as mock
     * mode, proxy port range and supported Airflow versions.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withMwaaConfig(c -> c
     *         .mock(true)
     *         .proxyPortRange(8700, 50)
     *         .defaultVersion("2.10.5"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MwaaConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withMwaaConfig(Consumer<MwaaConfig.Builder> configurer) {
        return updateServiceConfig(mwaaConfig, configurer);
    }

    /**
     * GuardDuty-specific settings.
     *
     * @return the GuardDuty configuration
     */
    public GuardDutyConfig getGuardDutyConfig() {
        return guardDutyConfig.get();
    }

    /**
     * Configures GuardDuty-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withGuardDutyConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link GuardDutyConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withGuardDutyConfig(Consumer<GuardDutyConfig.Builder> configurer) {
        return updateServiceConfig(guardDutyConfig, configurer);
    }

    /**
     * CloudHSM v2-specific settings.
     *
     * @return the CloudHSM v2 configuration
     */
    public CloudHsmV2Config getCloudHsmV2Config() {
        return cloudHsmV2Config.get();
    }

    /**
     * Configures CloudHSM v2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCloudHsmV2Config(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudHsmV2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCloudHsmV2Config(Consumer<CloudHsmV2Config.Builder> configurer) {
        return updateServiceConfig(cloudHsmV2Config, configurer);
    }

    /**
     * EMR Serverless-specific settings.
     *
     * @return the EMR Serverless configuration
     */
    public EmrServerlessConfig getEmrServerlessConfig() {
        return emrServerlessConfig.get();
    }

    /**
     * Configures EMR Serverless-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEmrServerlessConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link EmrServerlessConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEmrServerlessConfig(Consumer<EmrServerlessConfig.Builder> configurer) {
        return updateServiceConfig(emrServerlessConfig, configurer);
    }

    /**
     * FIS (Fault Injection Simulator)-specific settings.
     *
     * @return the FIS configuration
     */
    public FisConfig getFisConfig() {
        return fisConfig.get();
    }

    /**
     * Configures FIS (Fault Injection Simulator)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withFisConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FisConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withFisConfig(Consumer<FisConfig.Builder> configurer) {
        return updateServiceConfig(fisConfig, configurer);
    }


    /**
     * Connect-specific settings.
     *
     * @return the Connect configuration
     */
    public ConnectConfig getConnectConfig() {
        return connectConfig.get();
    }

    /**
     * Configures Connect-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withConnectConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ConnectConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withConnectConfig(Consumer<ConnectConfig.Builder> configurer) {
        return updateServiceConfig(connectConfig, configurer);
    }


    /**
     * SSO Admin-specific settings.
     *
     * @return the SSO Admin configuration
     */
    public SsoAdminConfig getSsoAdminConfig() {
        return ssoAdminConfig.get();
    }

    /**
     * Configures SSO Admin-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSsoAdminConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SsoAdminConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSsoAdminConfig(Consumer<SsoAdminConfig.Builder> configurer) {
        return updateServiceConfig(ssoAdminConfig, configurer);
    }


    /**
     * APS (Prometheus)-specific settings.
     *
     * @return the APS (Prometheus) configuration
     */
    public ApsConfig getApsConfig() {
        return apsConfig.get();
    }

    /**
     * Configures APS (Prometheus)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withApsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link ApsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withApsConfig(Consumer<ApsConfig.Builder> configurer) {
        return updateServiceConfig(apsConfig, configurer);
    }


    /**
     * CodeGuru Reviewer-specific settings.
     *
     * @return the CodeGuru Reviewer configuration
     */
    public CodeGuruReviewerConfig getCodeGuruReviewerConfig() {
        return codeGuruReviewerConfig.get();
    }

    /**
     * Configures CodeGuru Reviewer-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCodeGuruReviewerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CodeGuruReviewerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCodeGuruReviewerConfig(Consumer<CodeGuruReviewerConfig.Builder> configurer) {
        return updateServiceConfig(codeGuruReviewerConfig, configurer);
    }


    /**
     * Control Tower-specific settings.
     *
     * @return the Control Tower configuration
     */
    public ControlTowerConfig getControlTowerConfig() {
        return controlTowerConfig.get();
    }

    /**
     * Configures Control Tower-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withControlTowerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ControlTowerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withControlTowerConfig(Consumer<ControlTowerConfig.Builder> configurer) {
        return updateServiceConfig(controlTowerConfig, configurer);
    }


    /**
     * Route 53 Resolver-specific settings.
     *
     * @return the Route 53 Resolver configuration
     */
    public Route53ResolverConfig getRoute53ResolverConfig() {
        return route53ResolverConfig.get();
    }

    /**
     * Configures Route 53 Resolver-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRoute53ResolverConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link Route53ResolverConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRoute53ResolverConfig(Consumer<Route53ResolverConfig.Builder> configurer) {
        return updateServiceConfig(route53ResolverConfig, configurer);
    }


    /**
     * Network Firewall-specific settings.
     *
     * @return the Network Firewall configuration
     */
    public NetworkFirewallConfig getNetworkFirewallConfig() {
        return networkFirewallConfig.get();
    }

    /**
     * Configures Network Firewall-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withNetworkFirewallConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link NetworkFirewallConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withNetworkFirewallConfig(Consumer<NetworkFirewallConfig.Builder> configurer) {
        return updateServiceConfig(networkFirewallConfig, configurer);
    }


    /**
     * Service Catalog-specific settings.
     *
     * @return the Service Catalog configuration
     */
    public ServiceCatalogConfig getServiceCatalogConfig() {
        return serviceCatalogConfig.get();
    }

    /**
     * Configures Service Catalog-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withServiceCatalogConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ServiceCatalogConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withServiceCatalogConfig(Consumer<ServiceCatalogConfig.Builder> configurer) {
        return updateServiceConfig(serviceCatalogConfig, configurer);
    }


    /**
     * Service Quotas-specific settings.
     *
     * @return the Service Quotas configuration
     */
    public ServiceQuotasConfig getServiceQuotasConfig() {
        return serviceQuotasConfig.get();
    }

    /**
     * Configures Service Quotas-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withServiceQuotasConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ServiceQuotasConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withServiceQuotasConfig(Consumer<ServiceQuotasConfig.Builder> configurer) {
        return updateServiceConfig(serviceQuotasConfig, configurer);
    }


    /**
     * RAM-specific settings.
     *
     * @return the RAM configuration
     */
    public RamConfig getRamConfig() {
        return ramConfig.get();
    }

    /**
     * Configures RAM-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRamConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RamConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRamConfig(Consumer<RamConfig.Builder> configurer) {
        return updateServiceConfig(ramConfig, configurer);
    }


    /**
     * Lake Formation-specific settings.
     *
     * @return the Lake Formation configuration
     */
    public LakeFormationConfig getLakeFormationConfig() {
        return lakeFormationConfig.get();
    }

    /**
     * Configures Lake Formation-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withLakeFormationConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link LakeFormationConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withLakeFormationConfig(Consumer<LakeFormationConfig.Builder> configurer) {
        return updateServiceConfig(lakeFormationConfig, configurer);
    }


    /**
     * EFS-specific settings.
     *
     * @return the EFS configuration
     */
    public EfsConfig getEfsConfig() {
        return efsConfig.get();
    }

    /**
     * Configures EFS-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withEfsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link EfsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withEfsConfig(Consumer<EfsConfig.Builder> configurer) {
        return updateServiceConfig(efsConfig, configurer);
    }


    /**
     * Resource Explorer v2-specific settings.
     *
     * @return the Resource Explorer v2 configuration
     */
    public ResourceExplorer2Config getResourceExplorer2Config() {
        return resourceExplorer2Config.get();
    }

    /**
     * Configures Resource Explorer v2-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withResourceExplorer2Config(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ResourceExplorer2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withResourceExplorer2Config(Consumer<ResourceExplorer2Config.Builder> configurer) {
        return updateServiceConfig(resourceExplorer2Config, configurer);
    }


    /**
     * Redshift-specific settings.
     *
     * @return the Redshift configuration
     */
    public RedshiftConfig getRedshiftConfig() {
        return redshiftConfig.get();
    }

    /**
     * Configures Redshift-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRedshiftConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RedshiftConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRedshiftConfig(Consumer<RedshiftConfig.Builder> configurer) {
        return updateServiceConfig(redshiftConfig, configurer);
    }


    /**
     * Organizations-specific settings.
     *
     * @return the Organizations configuration
     */
    public OrganizationsConfig getOrganizationsConfig() {
        return organizationsConfig.get();
    }

    /**
     * Configures Organizations-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withOrganizationsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives an {@link OrganizationsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withOrganizationsConfig(Consumer<OrganizationsConfig.Builder> configurer) {
        return updateServiceConfig(organizationsConfig, configurer);
    }


    /**
     * Comprehend-specific settings.
     *
     * @return the Comprehend configuration
     */
    public ComprehendConfig getComprehendConfig() {
        return comprehendConfig.get();
    }

    /**
     * Configures Comprehend-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withComprehendConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ComprehendConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withComprehendConfig(Consumer<ComprehendConfig.Builder> configurer) {
        return updateServiceConfig(comprehendConfig, configurer);
    }


    /**
     * Rekognition-specific settings.
     *
     * @return the Rekognition configuration
     */
    public RekognitionConfig getRekognitionConfig() {
        return rekognitionConfig.get();
    }

    /**
     * Configures Rekognition-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRekognitionConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RekognitionConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRekognitionConfig(Consumer<RekognitionConfig.Builder> configurer) {
        return updateServiceConfig(rekognitionConfig, configurer);
    }


    /**
     * Transcribe-specific settings.
     *
     * @return the Transcribe configuration
     */
    public TranscribeConfig getTranscribeConfig() {
        return transcribeConfig.get();
    }

    /**
     * Configures Transcribe-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTranscribeConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TranscribeConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTranscribeConfig(Consumer<TranscribeConfig.Builder> configurer) {
        return updateServiceConfig(transcribeConfig, configurer);
    }

    /**
     * Redshift Data API-specific settings.
     *
     * @return the Redshift Data API configuration
     */
    public RedshiftDataConfig getRedshiftDataConfig() {
        return redshiftDataConfig.get();
    }

    /**
     * Configures Redshift Data API-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRedshiftDataConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RedshiftDataConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRedshiftDataConfig(Consumer<RedshiftDataConfig.Builder> configurer) {
        return updateServiceConfig(redshiftDataConfig, configurer);
    }

    /**
     * Redshift Serverless-specific settings.
     *
     * @return the Redshift Serverless configuration
     */
    public RedshiftServerlessConfig getRedshiftServerlessConfig() {
        return redshiftServerlessConfig.get();
    }

    /**
     * Configures Redshift Serverless-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withRedshiftServerlessConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RedshiftServerlessConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withRedshiftServerlessConfig(Consumer<RedshiftServerlessConfig.Builder> configurer) {
        return updateServiceConfig(redshiftServerlessConfig, configurer);
    }

    /**
     * Bedrock-specific settings.
     *
     * @return the Bedrock configuration
     */
    public BedrockConfig getBedrockConfig() {
        return bedrockConfig.get();
    }

    /**
     * Configures Bedrock-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBedrockConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BedrockConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBedrockConfig(Consumer<BedrockConfig.Builder> configurer) {
        return updateServiceConfig(bedrockConfig, configurer);
    }

    /**
     * Translate-specific settings.
     *
     * @return the Translate configuration
     */
    public TranslateConfig getTranslateConfig() {
        return translateConfig.get();
    }

    /**
     * Configures Translate-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTranslateConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TranslateConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTranslateConfig(Consumer<TranslateConfig.Builder> configurer) {
        return updateServiceConfig(translateConfig, configurer);
    }

    /**
     * OAM (CloudWatch Observability Access Manager)-specific settings.
     *
     * @return the OAM (CloudWatch Observability Access Manager) configuration
     */
    public OamConfig getOamConfig() {
        return oamConfig.get();
    }

    /**
     * Configures OAM (CloudWatch Observability Access Manager)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withOamConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link OamConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withOamConfig(Consumer<OamConfig.Builder> configurer) {
        return updateServiceConfig(oamConfig, configurer);
    }

    /**
     * BCM Pricing Calculator-specific settings.
     *
     * @return the BCM Pricing Calculator configuration
     */
    public BcmPricingCalculatorConfig getBcmPricingCalculatorConfig() {
        return bcmPricingCalculatorConfig.get();
    }

    /**
     * Configures BCM Pricing Calculator-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBcmPricingCalculatorConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BcmPricingCalculatorConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBcmPricingCalculatorConfig(Consumer<BcmPricingCalculatorConfig.Builder> configurer) {
        return updateServiceConfig(bcmPricingCalculatorConfig, configurer);
    }

    /**
     * Timestream for InfluxDB-specific settings.
     *
     * @return the Timestream for InfluxDB configuration
     */
    public TimestreamInfluxDbConfig getTimestreamInfluxDbConfig() {
        return timestreamInfluxDbConfig.get();
    }

    /**
     * Configures Timestream for InfluxDB-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withTimestreamInfluxDbConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TimestreamInfluxDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withTimestreamInfluxDbConfig(Consumer<TimestreamInfluxDbConfig.Builder> configurer) {
        return updateServiceConfig(timestreamInfluxDbConfig, configurer);
    }

    /**
     * SageMaker-specific settings.
     *
     * @return the SageMaker configuration
     */
    public SageMakerConfig getSageMakerConfig() {
        return sageMakerConfig.get();
    }

    /**
     * Configures SageMaker-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSageMakerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SageMakerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSageMakerConfig(Consumer<SageMakerConfig.Builder> configurer) {
        return updateServiceConfig(sageMakerConfig, configurer);
    }

    /**
     * SSO OIDC (IAM Identity Center OIDC)-specific settings.
     *
     * @return the SSO OIDC (IAM Identity Center OIDC) configuration
     */
    public SsoOidcConfig getSsoOidcConfig() {
        return ssoOidcConfig.get();
    }

    /**
     * Configures SSO OIDC (IAM Identity Center OIDC)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSsoOidcConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SsoOidcConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSsoOidcConfig(Consumer<SsoOidcConfig.Builder> configurer) {
        return updateServiceConfig(ssoOidcConfig, configurer);
    }

    /**
     * Macie-specific settings.
     *
     * @return the Macie configuration
     */
    public Macie2Config getMacie2Config() {
        return macie2Config.get();
    }

    /**
     * Configures Macie-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withMacie2Config(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link Macie2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withMacie2Config(Consumer<Macie2Config.Builder> configurer) {
        return updateServiceConfig(macie2Config, configurer);
    }

    /**
     * Account Management-specific settings.
     *
     * @return the Account Management configuration
     */
    public AccountConfig getAccountConfig() {
        return accountConfig.get();
    }

    /**
     * Configures Account Management-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAccountConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AccountConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAccountConfig(Consumer<AccountConfig.Builder> configurer) {
        return updateServiceConfig(accountConfig, configurer);
    }

    /**
     * IAM Access Analyzer-specific settings.
     *
     * @return the IAM Access Analyzer configuration
     */
    public AccessAnalyzerConfig getAccessAnalyzerConfig() {
        return accessAnalyzerConfig.get();
    }

    /**
     * Configures IAM Access Analyzer-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAccessAnalyzerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AccessAnalyzerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAccessAnalyzerConfig(Consumer<AccessAnalyzerConfig.Builder> configurer) {
        return updateServiceConfig(accessAnalyzerConfig, configurer);
    }

    /**
     * Identity Store-specific settings.
     *
     * @return the Identity Store configuration
     */
    public IdentityStoreConfig getIdentityStoreConfig() {
        return identityStoreConfig.get();
    }

    /**
     * Configures Identity Store-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withIdentityStoreConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IdentityStoreConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withIdentityStoreConfig(Consumer<IdentityStoreConfig.Builder> configurer) {
        return updateServiceConfig(identityStoreConfig, configurer);
    }

    /**
     * Budgets-specific settings.
     *
     * @return the Budgets configuration
     */
    public BudgetsConfig getBudgetsConfig() {
        return budgetsConfig.get();
    }

    /**
     * Configures Budgets-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withBudgetsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BudgetsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withBudgetsConfig(Consumer<BudgetsConfig.Builder> configurer) {
        return updateServiceConfig(budgetsConfig, configurer);
    }

    /**
     * Inspector-specific settings.
     *
     * @return the Inspector configuration
     */
    public Inspector2Config getInspector2Config() {
        return inspector2Config.get();
    }

    /**
     * Configures Inspector-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withInspector2Config(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link Inspector2Config.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withInspector2Config(Consumer<Inspector2Config.Builder> configurer) {
        return updateServiceConfig(inspector2Config, configurer);
    }

    /**
     * Security Hub-specific settings.
     *
     * @return the Security Hub configuration
     */
    public SecurityHubConfig getSecurityHubConfig() {
        return securityHubConfig.get();
    }

    /**
     * Configures Security Hub-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withSecurityHubConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SecurityHubConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withSecurityHubConfig(Consumer<SecurityHubConfig.Builder> configurer) {
        return updateServiceConfig(securityHubConfig, configurer);
    }

    /**
     * Detective-specific settings.
     *
     * @return the Detective configuration
     */
    public DetectiveConfig getDetectiveConfig() {
        return detectiveConfig.get();
    }

    /**
     * Configures Detective-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDetectiveConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DetectiveConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDetectiveConfig(Consumer<DetectiveConfig.Builder> configurer) {
        return updateServiceConfig(detectiveConfig, configurer);
    }

    /**
     * Verified Permissions-specific settings.
     *
     * @return the Verified Permissions configuration
     */
    public VerifiedPermissionsConfig getVerifiedPermissionsConfig() {
        return verifiedPermissionsConfig.get();
    }

    /**
     * Configures Verified Permissions-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withVerifiedPermissionsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link VerifiedPermissionsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withVerifiedPermissionsConfig(Consumer<VerifiedPermissionsConfig.Builder> configurer) {
        return updateServiceConfig(verifiedPermissionsConfig, configurer);
    }

    /**
     * Control Catalog-specific settings.
     *
     * @return the Control Catalog configuration
     */
    public ControlCatalogConfig getControlCatalogConfig() {
        return controlCatalogConfig.get();
    }

    /**
     * Configures Control Catalog-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withControlCatalogConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ControlCatalogConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withControlCatalogConfig(Consumer<ControlCatalogConfig.Builder> configurer) {
        return updateServiceConfig(controlCatalogConfig, configurer);
    }

    /**
     * AppIntegrations-specific settings.
     *
     * @return the AppIntegrations configuration
     */
    public AppIntegrationsConfig getAppIntegrationsConfig() {
        return appIntegrationsConfig.get();
    }

    /**
     * Configures AppIntegrations-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withAppIntegrationsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AppIntegrationsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withAppIntegrationsConfig(Consumer<AppIntegrationsConfig.Builder> configurer) {
        return updateServiceConfig(appIntegrationsConfig, configurer);
    }

    /**
     * DLM (Data Lifecycle Manager)-specific settings.
     *
     * @return the DLM (Data Lifecycle Manager) configuration
     */
    public DlmConfig getDlmConfig() {
        return dlmConfig.get();
    }

    /**
     * Configures DLM (Data Lifecycle Manager)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDlmConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DlmConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDlmConfig(Consumer<DlmConfig.Builder> configurer) {
        return updateServiceConfig(dlmConfig, configurer);
    }

    /**
     * Cognito Identity-specific settings.
     *
     * @return the Cognito Identity configuration
     */
    public CognitoIdentityConfig getCognitoIdentityConfig() {
        return cognitoIdentityConfig.get();
    }

    /**
     * Configures Cognito Identity-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCognitoIdentityConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CognitoIdentityConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCognitoIdentityConfig(Consumer<CognitoIdentityConfig.Builder> configurer) {
        return updateServiceConfig(cognitoIdentityConfig, configurer);
    }

    /**
     * Global Accelerator-specific settings.
     *
     * @return the Global Accelerator configuration
     */
    public GlobalAcceleratorConfig getGlobalAcceleratorConfig() {
        return globalAcceleratorConfig.get();
    }

    /**
     * Configures Global Accelerator-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withGlobalAcceleratorConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link GlobalAcceleratorConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withGlobalAcceleratorConfig(Consumer<GlobalAcceleratorConfig.Builder> configurer) {
        return updateServiceConfig(globalAcceleratorConfig, configurer);
    }

    /**
     * DataSync-specific settings.
     *
     * @return the DataSync configuration
     */
    public DataSyncConfig getDataSyncConfig() {
        return dataSyncConfig.get();
    }

    /**
     * Configures DataSync-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDataSyncConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DataSyncConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDataSyncConfig(Consumer<DataSyncConfig.Builder> configurer) {
        return updateServiceConfig(dataSyncConfig, configurer);
    }

    /**
     * CodeArtifact-specific settings.
     *
     * @return the CodeArtifact configuration
     */
    public CodeArtifactConfig getCodeArtifactConfig() {
        return codeArtifactConfig.get();
    }

    /**
     * Configures CodeArtifact-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withCodeArtifactConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CodeArtifactConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withCodeArtifactConfig(Consumer<CodeArtifactConfig.Builder> configurer) {
        return updateServiceConfig(codeArtifactConfig, configurer);
    }

    /**
     * AWS Marketplace-specific settings.
     *
     * @return the AWS Marketplace configuration
     */
    public MarketplaceConfig getMarketplaceConfig() {
        return marketplaceConfig.get();
    }

    /**
     * Configures AWS Marketplace-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withMarketplaceConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MarketplaceConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withMarketplaceConfig(Consumer<MarketplaceConfig.Builder> configurer) {
        return updateServiceConfig(marketplaceConfig, configurer);
    }

    /**
     * DMS (Database Migration Service)-specific settings.
     *
     * @return the DMS (Database Migration Service) configuration
     */
    public DmsConfig getDmsConfig() {
        return dmsConfig.get();
    }

    /**
     * Configures DMS (Database Migration Service)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withDmsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DmsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withDmsConfig(Consumer<DmsConfig.Builder> configurer) {
        return updateServiceConfig(dmsConfig, configurer);
    }

    /**
     * Classic ELB (Elastic Load Balancing, API version 2012-06-01)-specific settings.
     *
     * @return the Classic ELB configuration
     */
    public ElbConfig getElbConfig() {
        return elbConfig.get();
    }

    /**
     * Configures Classic ELB (Elastic Load Balancing, API version 2012-06-01)-specific settings.
     *
     * <pre>{@code
     * new FlociContainer()
     *     .withElbConfig(c -> c.mock(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ElbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociContainer withElbConfig(Consumer<ElbConfig.Builder> configurer) {
        return updateServiceConfig(elbConfig, configurer);
    }

    private void preparePersistentStorageForCleanup() {
        if (storageConfig.getHostPersistentPath().isEmpty() || !isRunning()) {
            return;
        }

        // Child containers can create root-owned files in Floci's bind-mounted data directory.
        try {
            ExecResult result = execInContainer("chmod", "-R", "a+rwX", "/app/data");
            if (result.getExitCode() != 0) {
                logger.warn("Failed to make Floci persistent storage deletable: {}", result.getStderr());
            }
        } catch (IOException e) {
            logger.warn("Failed to make Floci persistent storage deletable", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while making Floci persistent storage deletable", e);
        }
    }

    private void deletePersistentStorage() {
        storageConfig.getHostPersistentPath().ifPresent(path -> {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                        deletePath(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException failure) {
                        logger.warn("Failed to visit Floci persistent storage path {}", file, failure);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path directory, IOException failure) {
                        if (failure != null) {
                            logger.warn("Failed to visit Floci persistent storage directory {}", directory, failure);
                        }
                        deletePath(directory);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.warn("Failed to delete Floci persistent storage at {}", path, e);
            }
        });
    }

    private static void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.warn("Failed to delete Floci persistent storage path {}", path, e);
        }
    }

    @Override
    protected void applyGlobalEnvVars() {
        tlsConfig.applyEnvVarsToContainer(this);
        storageConfig.applyEnvVarsToContainer(this);
        duckDbConfig.applyEnvVarsToContainer(this);
        securityConfig.applyEnvVarsToContainer(this);
        protocolsConfig.applyEnvVarsToContainer(this);
        authConfig.applyEnvVarsToContainer(this);
        initHooksConfig.applyEnvVarsToContainer(this);
        partitionsConfig.applyEnvVarsToContainer(this);
        networkConfig.applyEnvVarsToContainer(this);
    }
}
