package io.floci.testcontainers.az;

import io.floci.testcontainers.az.config.AuthConfig;
import io.floci.testcontainers.az.config.DirectoryConfig;
import io.floci.testcontainers.az.config.TlsConfig;
import io.floci.testcontainers.az.config.services.*;
import io.floci.testcontainers.core.AbstractFlociContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Testcontainers module for <a href="https://github.com/floci-io/floci-az">Floci Azure</a> — a
 * free, open-source local Azure emulator.
 *
 * <p>Starts a Floci Azure container that exposes all emulated Azure services on a single HTTP
 * endpoint. Use {@link #getEndpoint()} to obtain the base URL, or one of the service-specific
 * helpers such as {@link #getBlobEndpoint()} and {@link #getStorageConnectionString()} to configure
 * Azure SDK clients.
 *
 * <p>Container-based services (Azure Functions, AKS, Azure Database for PostgreSQL and others) require
 * access to the Docker daemon. This module automatically mounts the Docker socket only when at least
 * one currently enabled service actually needs it. Use {@link #withDockerSocket(boolean)} to override
 * this auto-detection.
 *
 * <pre>{@code
 * try (FlociAzContainer floci = new FlociAzContainer()) {
 *     floci.start();
 *     BlobServiceClient client = new BlobServiceClientBuilder()
 *         .connectionString(floci.getStorageConnectionString())
 *         .buildClient();
 * }
 * }</pre>
 */
public class FlociAzContainer extends AbstractFlociContainer<FlociAzContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("floci/floci-az");
    private static final String DEFAULT_TAG = "latest";

    /**
     * Port Floci Azure serves all emulated services on.
     */
    public static final int PORT = 4577;

    private static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_AZ__LEVEL";
    private static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_AZ_SERVICES_DOCKER_NETWORK";
    private static final String HEALTH_PATH = "/_floci/health";
    private static final String TLS_CERT_PATH = "/_floci/tls-cert";

    private static final String DEFAULT_ACCOUNT_NAME = "devstoreaccount1";
    private static final String DEFAULT_ACCOUNT_KEY =
            "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

    private TlsConfig tlsConfig = TlsConfig.builder().build();
    private AuthConfig authConfig = AuthConfig.builder().build();
    private DirectoryConfig directoryConfig = DirectoryConfig.builder().build();

    // Service configs
    private final ServiceConfigRef<BlobConfig> blobConfig = registerServiceConfig(BlobConfig.builder().build());
    private final ServiceConfigRef<QueueConfig> queueConfig = registerServiceConfig(QueueConfig.builder().build());
    private final ServiceConfigRef<TableConfig> tableConfig = registerServiceConfig(TableConfig.builder().build());
    private final ServiceConfigRef<FunctionsConfig> functionsConfig = registerServiceConfig(FunctionsConfig.builder().build());
    private final ServiceConfigRef<AppConfigConfig> appConfigConfig = registerServiceConfig(AppConfigConfig.builder().build());
    private final ServiceConfigRef<SignalRConfig> signalRConfig = registerServiceConfig(SignalRConfig.builder().build());
    private final ServiceConfigRef<CosmosConfig> cosmosConfig = registerServiceConfig(CosmosConfig.builder().build());
    private final ServiceConfigRef<KeyVaultConfig> keyVaultConfig = registerServiceConfig(KeyVaultConfig.builder().build());
    private final ServiceConfigRef<EventHubConfig> eventHubConfig = registerServiceConfig(EventHubConfig.builder().build());
    private final ServiceConfigRef<SqlConfig> sqlConfig = registerServiceConfig(SqlConfig.builder().build());
    private final ServiceConfigRef<PostgresConfig> postgresConfig = registerServiceConfig(PostgresConfig.builder().build());
    private final ServiceConfigRef<MySqlConfig> mySqlConfig = registerServiceConfig(MySqlConfig.builder().build());
    private final ServiceConfigRef<MariaDbConfig> mariaDbConfig = registerServiceConfig(MariaDbConfig.builder().build());
    private final ServiceConfigRef<ServiceBusConfig> serviceBusConfig = registerServiceConfig(ServiceBusConfig.builder().build());
    private final ServiceConfigRef<AksConfig> aksConfig = registerServiceConfig(AksConfig.builder().build());
    private final ServiceConfigRef<AciConfig> aciConfig = registerServiceConfig(AciConfig.builder().build());
    private final ServiceConfigRef<VmConfig> vmConfig = registerServiceConfig(VmConfig.builder().build());
    private final ServiceConfigRef<ApimConfig> apimConfig = registerServiceConfig(ApimConfig.builder().build());
    private final ServiceConfigRef<RedisConfig> redisConfig = registerServiceConfig(RedisConfig.builder().build());
    private final ServiceConfigRef<AcrConfig> acrConfig = registerServiceConfig(AcrConfig.builder().build());
    private final ServiceConfigRef<MonitorConfig> monitorConfig = registerServiceConfig(MonitorConfig.builder().build());
    private final ServiceConfigRef<EntraConfig> entraConfig = registerServiceConfig(EntraConfig.builder().build());
    private final ServiceConfigRef<GraphConfig> graphConfig = registerServiceConfig(GraphConfig.builder().build());
    private final ServiceConfigRef<ArmConfig> armConfig = registerServiceConfig(ArmConfig.builder().build());
    private final ServiceConfigRef<NetworkConfig> networkConfig = registerServiceConfig(NetworkConfig.builder().build());
    private final ServiceConfigRef<EventGridConfig> eventGridConfig = registerServiceConfig(EventGridConfig.builder().build());
    private final ServiceConfigRef<ManagedIdentityConfig> managedIdentityConfig = registerServiceConfig(ManagedIdentityConfig.builder().build());
    private final ServiceConfigRef<ContainerAppsConfig> containerAppsConfig = registerServiceConfig(ContainerAppsConfig.builder().build());
    private final ServiceConfigRef<PolicyConfig> policyConfig = registerServiceConfig(PolicyConfig.builder().build());
    private final ServiceConfigRef<EmailConfig> emailConfig = registerServiceConfig(EmailConfig.builder().build());

    /**
     * Creates a new Floci Azure container with the default image ({@code floci/floci-az:latest}).
     */
    public FlociAzContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Creates a new Floci Azure container with the specified image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-az})
     */
    public FlociAzContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Creates a new Floci Azure container with the specified Docker image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-az})
     */
    public FlociAzContainer(DockerImageName dockerImageName) {
        super(dockerImageName, DEFAULT_IMAGE_NAME, PORT, LOG_LEVEL_ENV_VAR, DOCKER_NETWORK_ENV_VAR);

        waitingFor(Wait.forHttp(HEALTH_PATH)
                .forPort(PORT)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofSeconds(60)));

        applyAllConfigs();
    }

    @Override
    protected void applyGlobalEnvVars() {
        tlsConfig.applyEnvVarsToContainer(this);
        authConfig.applyEnvVarsToContainer(this);
        directoryConfig.applyEnvVarsToContainer(this);
    }

    /**
     * Returns the id of the default subscription, as configured via
     * {@link ArmConfig.Builder#defaultSubscriptionId(String)}. It is the subscription {@code GET /subscriptions}
     * lists; other subscription ids are accepted as well.
     *
     * @return the default subscription id
     */
    public String getSubscriptionId() {
        return getArmConfig().getDefaultSubscriptionId();
    }

    /**
     * Returns the id of the default Microsoft Entra ID tenant, as configured via
     * {@link EntraConfig.Builder#defaultTenantId(String)}. Tokens requested for the {@code common},
     * {@code organizations} and {@code consumers} tenants are issued for this tenant.
     *
     * @return the default tenant id
     */
    public String getTenantId() {
        return getEntraConfig().getDefaultTenantId();
    }

    /**
     * Returns the HTTPS endpoint URL for connecting to Floci Azure (e.g. {@code https://localhost:32781}).
     * Floci Azure serves HTTPS on the same port as plain HTTP, but only while TLS is enabled via
     * {@link #withTlsConfig(Consumer)}. Clients have to trust the certificate returned by
     * {@link #getTlsCertificate()}.
     *
     * @return the HTTPS endpoint URL
     */
    public String getHttpsEndpoint() {
        return String.format("https://%s:%d", getHost(), getMappedPort(PORT));
    }

    /**
     * Fetches the PEM-encoded certificate Floci Azure currently serves HTTPS with (either the
     * auto-generated one or the one configured via {@link TlsConfig.Builder#certPath(String)}).
     * Import it into the trust store of HTTPS clients such as the Cosmos DB or Key Vault SDKs.
     *
     * @return the PEM-encoded TLS certificate
     * @throws IllegalStateException if TLS is not enabled or the certificate is not available
     */
    public String getTlsCertificate() {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(getEndpoint() + TLS_CERT_PATH)).build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("TLS certificate not available (HTTP " + response.statusCode()
                        + "): " + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to fetch the TLS certificate of Floci Azure", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching the TLS certificate of Floci Azure", e);
        }
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
     * Configures TLS/HTTPS for the Floci Azure server. When enabled, HTTP and HTTPS are served on the same
     * port; use {@link #getHttpsEndpoint()} and trust {@link #getTlsCertificate()} in HTTPS clients.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withTlsConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TlsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withTlsConfig(Consumer<TlsConfig.Builder> configurer) {
        TlsConfig.Builder builder = tlsConfig.toBuilder();
        configurer.accept(builder);
        this.tlsConfig = builder.build();
        tlsConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the authentication configuration.
     *
     * @return the authentication configuration
     */
    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    /**
     * Configures authentication of the Floci Azure server, e.g. the keys of additional storage accounts used
     * to validate shared-key signed SAS tokens:
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withAuthConfig(c -> c.storageAccountKey("myaccount", "bXktYmFzZTY0LWtleQ=="));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AuthConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withAuthConfig(Consumer<AuthConfig.Builder> configurer) {
        AuthConfig.Builder builder = authConfig.toBuilder();
        configurer.accept(builder);
        this.authConfig = builder.build();
        authConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the tenant and subscription directory configuration.
     *
     * @return the directory configuration
     */
    public DirectoryConfig getDirectoryConfig() {
        return directoryConfig;
    }

    /**
     * Configures the tenant and subscription directory of the Floci Azure server, i.e. the default cloud
     * and the tenants and subscriptions it holds besides the default ones:
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withDirectoryConfig(c -> c
     *         .tenant("11111111-0000-0000-0000-000000000000", t -> t.displayName("Contoso"))
     *         .subscription("22222222-0000-0000-0000-000000000000",
     *             s -> s.tenantId("11111111-0000-0000-0000-000000000000")));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DirectoryConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withDirectoryConfig(Consumer<DirectoryConfig.Builder> configurer) {
        DirectoryConfig.Builder builder = directoryConfig.toBuilder();
        configurer.accept(builder);
        this.directoryConfig = builder.build();
        directoryConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the name of the default storage account ({@value DEFAULT_ACCOUNT_NAME}). Floci Azure serves
     * the data planes of all storage services under paths prefixed with the account name.
     *
     * @return the storage account name
     */
    public String getAccountName() {
        return DEFAULT_ACCOUNT_NAME;
    }

    /**
     * Returns the key of the default storage account: the well-known development storage key, unless a
     * different key was configured for {@value DEFAULT_ACCOUNT_NAME} via
     * {@link AuthConfig.Builder#storageAccountKey(String, String)}.
     *
     * @return the base64-encoded storage account key
     */
    public String getAccountKey() {
        return authConfig.getStorageAccountKeys().getOrDefault(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_KEY);
    }

    /**
     * Returns the Blob Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1}).
     *
     * @return the Blob Storage endpoint
     */
    public String getBlobEndpoint() {
        return getEndpoint() + "/" + getAccountName();
    }

    /**
     * Returns the Queue Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1-queue}).
     *
     * @return the Queue Storage endpoint
     */
    public String getQueueEndpoint() {
        return getEndpoint() + "/" + getAccountName() + "-queue";
    }

    /**
     * Returns the Table Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1-table}).
     *
     * @return the Table Storage endpoint
     */
    public String getTableEndpoint() {
        return getEndpoint() + "/" + getAccountName() + "-table";
    }

    /**
     * Returns a storage connection string for the default storage account, with the Blob, Queue and
     * Table endpoints pointing at this container.
     *
     * @return the storage connection string
     */
    public String getStorageConnectionString() {
        return "DefaultEndpointsProtocol=http"
                + ";AccountName=" + getAccountName()
                + ";AccountKey=" + getAccountKey()
                + ";BlobEndpoint=" + getBlobEndpoint()
                + ";QueueEndpoint=" + getQueueEndpoint()
                + ";TableEndpoint=" + getTableEndpoint()
                + ";";
    }

    /**
     * Returns the Azure Blob Storage configuration.
     *
     * @return the Blob Storage configuration
     */
    public BlobConfig getBlobConfig() {
        return blobConfig.get();
    }

    /**
     * Configures Azure Blob Storage.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withBlobConfig(c -> c.hierarchicalNamespaceAccounts(List.of("datalake1")));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link BlobConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withBlobConfig(Consumer<BlobConfig.Builder> configurer) {
        return updateServiceConfig(blobConfig, configurer);
    }
    /**
     * Returns the Azure Queue Storage configuration.
     *
     * @return the Queue Storage configuration
     */
    public QueueConfig getQueueConfig() {
        return queueConfig.get();
    }

    /**
     * Configures Azure Queue Storage.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withQueueConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link QueueConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withQueueConfig(Consumer<QueueConfig.Builder> configurer) {
        return updateServiceConfig(queueConfig, configurer);
    }

    /**
     * Returns the Azure Table Storage configuration.
     *
     * @return the Table Storage configuration
     */
    public TableConfig getTableConfig() {
        return tableConfig.get();
    }

    /**
     * Configures Azure Table Storage.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withTableConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TableConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withTableConfig(Consumer<TableConfig.Builder> configurer) {
        return updateServiceConfig(tableConfig, configurer);
    }

    /**
     * Returns the Azure Functions configuration.
     *
     * @return the Functions configuration
     */
    public FunctionsConfig getFunctionsConfig() {
        return functionsConfig.get();
    }

    /**
     * Configures Azure Functions, which runs functions in sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withFunctionsConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FunctionsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withFunctionsConfig(Consumer<FunctionsConfig.Builder> configurer) {
        return updateServiceConfig(functionsConfig, configurer);
    }
    /**
     * Returns the Azure App Configuration configuration.
     *
     * @return the App Configuration configuration
     */
    public AppConfigConfig getAppConfigConfig() {
        return appConfigConfig.get();
    }

    /**
     * Configures Azure App Configuration.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withAppConfigConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AppConfigConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withAppConfigConfig(Consumer<AppConfigConfig.Builder> configurer) {
        return updateServiceConfig(appConfigConfig, configurer);
    }

    /**
     * Returns the Azure SignalR Service configuration.
     *
     * @return the SignalR configuration
     */
    public SignalRConfig getSignalRConfig() {
        return signalRConfig.get();
    }

    /**
     * Configures Azure SignalR Service.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withSignalRConfig(c -> c.accessKey("bXktc2lnbmFsci1rZXk="));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SignalRConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withSignalRConfig(Consumer<SignalRConfig.Builder> configurer) {
        return updateServiceConfig(signalRConfig, configurer);
    }
    /**
     * Returns the Azure Cosmos DB configuration.
     *
     * @return the Cosmos DB configuration
     */
    public CosmosConfig getCosmosConfig() {
        return cosmosConfig.get();
    }

    /**
     * Configures Azure Cosmos DB, whose Docker-backed API engines (MongoDB, PostgreSQL, Cassandra, Gremlin)
     * require the Docker socket.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withCosmosConfig(c -> c.mongodb(api -> api.enabled(true)));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CosmosConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withCosmosConfig(Consumer<CosmosConfig.Builder> configurer) {
        return updateServiceConfig(cosmosConfig, configurer);
    }
    /**
     * Returns the Azure Key Vault configuration.
     *
     * @return the Key Vault configuration
     */
    public KeyVaultConfig getKeyVaultConfig() {
        return keyVaultConfig.get();
    }

    /**
     * Configures Azure Key Vault.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withKeyVaultConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KeyVaultConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withKeyVaultConfig(Consumer<KeyVaultConfig.Builder> configurer) {
        return updateServiceConfig(keyVaultConfig, configurer);
    }
    /**
     * Returns the Azure Event Hubs configuration.
     *
     * @return the Event Hubs configuration
     */
    public EventHubConfig getEventHubConfig() {
        return eventHubConfig.get();
    }

    /**
     * Configures Azure Event Hubs, which runs its brokers in sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withEventHubConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EventHubConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withEventHubConfig(Consumer<EventHubConfig.Builder> configurer) {
        return updateServiceConfig(eventHubConfig, configurer);
    }
    /**
     * Returns the Azure SQL Database configuration.
     *
     * @return the Azure SQL configuration
     */
    public SqlConfig getSqlConfig() {
        return sqlConfig.get();
    }

    /**
     * Configures Azure SQL Database, whose managed data plane runs SQL Server in sibling containers and therefore
     * requires the Docker socket.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withSqlConfig(c -> c.acceptEula("Y"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SqlConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withSqlConfig(Consumer<SqlConfig.Builder> configurer) {
        return updateServiceConfig(sqlConfig, configurer);
    }
    /**
     * Returns the Azure Database for PostgreSQL configuration.
     *
     * @return the PostgreSQL configuration
     */
    public PostgresConfig getPostgresConfig() {
        return postgresConfig.get();
    }

    /**
     * Configures Azure Database for PostgreSQL, which spawns sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withPostgresConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PostgresConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withPostgresConfig(Consumer<PostgresConfig.Builder> configurer) {
        return updateServiceConfig(postgresConfig, configurer);
    }
    /**
     * Returns the Azure Database for MySQL configuration.
     *
     * @return the MySQL configuration
     */
    public MySqlConfig getMySqlConfig() {
        return mySqlConfig.get();
    }

    /**
     * Configures Azure Database for MySQL, which spawns sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withMySqlConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MySqlConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withMySqlConfig(Consumer<MySqlConfig.Builder> configurer) {
        return updateServiceConfig(mySqlConfig, configurer);
    }
    /**
     * Returns the Azure Database for MariaDB configuration.
     *
     * @return the MariaDB configuration
     */
    public MariaDbConfig getMariaDbConfig() {
        return mariaDbConfig.get();
    }

    /**
     * Configures Azure Database for MariaDB, which spawns sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withMariaDbConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MariaDbConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withMariaDbConfig(Consumer<MariaDbConfig.Builder> configurer) {
        return updateServiceConfig(mariaDbConfig, configurer);
    }
    /**
     * Returns the Azure Service Bus configuration.
     *
     * @return the Service Bus configuration
     */
    public ServiceBusConfig getServiceBusConfig() {
        return serviceBusConfig.get();
    }

    /**
     * Configures Azure Service Bus, which requires the Docker socket unless mocked (the default).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withServiceBusConfig(c -> c.mocked(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ServiceBusConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withServiceBusConfig(Consumer<ServiceBusConfig.Builder> configurer) {
        return updateServiceConfig(serviceBusConfig, configurer);
    }
    /**
     * Returns the Azure Kubernetes Service (AKS) configuration.
     *
     * @return the AKS configuration
     */
    public AksConfig getAksConfig() {
        return aksConfig.get();
    }

    /**
     * Configures Azure Kubernetes Service (AKS), which runs clusters in sibling containers and therefore requires
     * the Docker socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withAksConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AksConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withAksConfig(Consumer<AksConfig.Builder> configurer) {
        return updateServiceConfig(aksConfig, configurer);
    }
    /**
     * Returns the Azure Container Instances configuration.
     *
     * @return the Container Instances configuration
     */
    public AciConfig getAciConfig() {
        return aciConfig.get();
    }

    /**
     * Configures Azure Container Instances, which requires the Docker socket unless mocked (the default).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withAciConfig(c -> c.mocked(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AciConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withAciConfig(Consumer<AciConfig.Builder> configurer) {
        return updateServiceConfig(aciConfig, configurer);
    }
    /**
     * Returns the Azure Virtual Machines configuration.
     *
     * @return the Virtual Machines configuration
     */
    public VmConfig getVmConfig() {
        return vmConfig.get();
    }

    /**
     * Configures Azure Virtual Machines, which requires the Docker socket unless mocked (the default).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withVmConfig(c -> c.mocked(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link VmConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withVmConfig(Consumer<VmConfig.Builder> configurer) {
        return updateServiceConfig(vmConfig, configurer);
    }
    /**
     * Returns the Azure API Management configuration.
     *
     * @return the API Management configuration
     */
    public ApimConfig getApimConfig() {
        return apimConfig.get();
    }

    /**
     * Configures Azure API Management.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withApimConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ApimConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withApimConfig(Consumer<ApimConfig.Builder> configurer) {
        return updateServiceConfig(apimConfig, configurer);
    }
    /**
     * Returns the Azure Cache for Redis configuration.
     *
     * @return the Redis configuration
     */
    public RedisConfig getRedisConfig() {
        return redisConfig.get();
    }

    /**
     * Configures Azure Cache for Redis, which runs caches in sibling containers and therefore requires the Docker
     * socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withRedisConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link RedisConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withRedisConfig(Consumer<RedisConfig.Builder> configurer) {
        return updateServiceConfig(redisConfig, configurer);
    }
    /**
     * Returns the Azure Container Registry configuration.
     *
     * @return the Container Registry configuration
     */
    public AcrConfig getAcrConfig() {
        return acrConfig.get();
    }

    /**
     * Configures Azure Container Registry, which runs registries in a sibling container and therefore requires the
     * Docker socket unless mocked.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withAcrConfig(c -> c.mocked(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AcrConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withAcrConfig(Consumer<AcrConfig.Builder> configurer) {
        return updateServiceConfig(acrConfig, configurer);
    }
    /**
     * Returns the Azure Monitor / Log Analytics configuration.
     *
     * @return the Monitor configuration
     */
    public MonitorConfig getMonitorConfig() {
        return monitorConfig.get();
    }

    /**
     * Configures Azure Monitor / Log Analytics.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withMonitorConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MonitorConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withMonitorConfig(Consumer<MonitorConfig.Builder> configurer) {
        return updateServiceConfig(monitorConfig, configurer);
    }
    /**
     * Returns the Microsoft Entra ID configuration.
     *
     * @return the Entra ID configuration
     */
    public EntraConfig getEntraConfig() {
        return entraConfig.get();
    }

    /**
     * Configures Microsoft Entra ID.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withEntraConfig(c -> c.validateTokens(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EntraConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withEntraConfig(Consumer<EntraConfig.Builder> configurer) {
        return updateServiceConfig(entraConfig, configurer);
    }
    /**
     * Returns the Microsoft Graph configuration.
     *
     * @return the Graph configuration
     */
    public GraphConfig getGraphConfig() {
        return graphConfig.get();
    }

    /**
     * Configures Microsoft Graph.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withGraphConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link GraphConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withGraphConfig(Consumer<GraphConfig.Builder> configurer) {
        return updateServiceConfig(graphConfig, configurer);
    }
    /**
     * Returns the Azure Resource Manager (ARM) configuration.
     *
     * @return the ARM configuration
     */
    public ArmConfig getArmConfig() {
        return armConfig.get();
    }

    /**
     * Configures Azure Resource Manager (ARM).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withArmConfig(c -> c.defaultSubscriptionId("22222222-2222-2222-2222-222222222222"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ArmConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withArmConfig(Consumer<ArmConfig.Builder> configurer) {
        return updateServiceConfig(armConfig, configurer);
    }
    /**
     * Returns the Azure Virtual Network configuration.
     *
     * @return the Virtual Network configuration
     */
    public NetworkConfig getNetworkConfig() {
        return networkConfig.get();
    }

    /**
     * Configures Azure Virtual Network.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withNetworkConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link NetworkConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withNetworkConfig(Consumer<NetworkConfig.Builder> configurer) {
        return updateServiceConfig(networkConfig, configurer);
    }
    /**
     * Returns the Azure Event Grid configuration.
     *
     * @return the Event Grid configuration
     */
    public EventGridConfig getEventGridConfig() {
        return eventGridConfig.get();
    }

    /**
     * Configures Azure Event Grid.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withEventGridConfig(c -> c.maxDeliveryAttempts(5));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EventGridConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withEventGridConfig(Consumer<EventGridConfig.Builder> configurer) {
        return updateServiceConfig(eventGridConfig, configurer);
    }
    /**
     * Returns the Azure Managed Identity configuration.
     *
     * @return the Managed Identity configuration
     */
    public ManagedIdentityConfig getManagedIdentityConfig() {
        return managedIdentityConfig.get();
    }

    /**
     * Configures Azure Managed Identity.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withManagedIdentityConfig(c -> c.systemAssignedScope("subscriptions/sub/resourceGroups/rg"));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ManagedIdentityConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withManagedIdentityConfig(Consumer<ManagedIdentityConfig.Builder> configurer) {
        return updateServiceConfig(managedIdentityConfig, configurer);
    }
    /**
     * Returns the Azure Container Apps configuration.
     *
     * @return the Container Apps configuration
     */
    public ContainerAppsConfig getContainerAppsConfig() {
        return containerAppsConfig.get();
    }

    /**
     * Configures Azure Container Apps, which requires the Docker socket unless mocked (the default).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withContainerAppsConfig(c -> c.mocked(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ContainerAppsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withContainerAppsConfig(Consumer<ContainerAppsConfig.Builder> configurer) {
        return updateServiceConfig(containerAppsConfig, configurer);
    }
    /**
     * Returns the Azure Policy configuration.
     *
     * @return the Policy configuration
     */
    public PolicyConfig getPolicyConfig() {
        return policyConfig.get();
    }

    /**
     * Configures the Azure Policy control plane (definitions, set definitions, assignments and exemptions).
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withPolicyConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PolicyConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withPolicyConfig(Consumer<PolicyConfig.Builder> configurer) {
        return updateServiceConfig(policyConfig, configurer);
    }
    /**
     * Returns the Azure Communication Services Email configuration.
     *
     * @return the Email configuration
     */
    public EmailConfig getEmailConfig() {
        return emailConfig.get();
    }

    /**
     * Configures Azure Communication Services Email.
     *
     * <pre>{@code
     * new FlociAzContainer()
     *     .withEmailConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EmailConfig.Builder} to modify
     * @return this container instance
     */
    public FlociAzContainer withEmailConfig(Consumer<EmailConfig.Builder> configurer) {
        return updateServiceConfig(emailConfig, configurer);
    }
}
