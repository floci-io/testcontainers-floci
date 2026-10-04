package io.floci.testcontainers.gcp;

import io.floci.testcontainers.core.AbstractFlociContainer;
import io.floci.testcontainers.gcp.config.TlsConfig;
import io.floci.testcontainers.gcp.config.services.*;
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
 * Testcontainers module for <a href="https://github.com/floci-io/floci-gcp">Floci GCP</a> — a
 * free, open-source local Google Cloud emulator.
 *
 * <p>Starts a Floci GCP container that serves all emulated Google Cloud services, both gRPC and REST, on a
 * single port. Use {@link #getEndpoint()} for REST/HTTP clients (e.g. as host of the Cloud Storage client), or
 * {@link #getEmulatorHost()} for clients configured via a {@code host:port} pair, such as the
 * {@code PUBSUB_EMULATOR_HOST}-style settings or plaintext gRPC channels.
 *
 * <p>Container-based services (Cloud SQL, Cloud Run, GKE, Managed Kafka and the DuckDB-backed BigQuery query
 * engine) require access to the Docker daemon. This module automatically mounts the Docker socket only when at
 * least one currently enabled service actually needs it. Use {@link #withDockerSocket(boolean)} to override this
 * auto-detection.
 *
 * <pre>{@code
 * try (FlociGcpContainer floci = new FlociGcpContainer()) {
 *     floci.start();
 *     Storage storage = StorageOptions.newBuilder()
 *         .setHost(floci.getEndpoint())
 *         .setProjectId(floci.getProjectId())
 *         .setCredentials(NoCredentials.getInstance())
 *         .build()
 *         .getService();
 * }
 * }</pre>
 */
public class FlociGcpContainer extends AbstractFlociContainer<FlociGcpContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("floci/floci-gcp");
    private static final String DEFAULT_TAG = "latest";

    /**
     * Port Floci GCP serves all emulated services on (gRPC and REST).
     */
    public static final int PORT = 4588;

    private static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_GCP__LEVEL";
    private static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_GCP_SERVICES_DOCKER_NETWORK";
    private static final String PROJECT_ID_ENV_VAR = "FLOCI_GCP_DEFAULT_PROJECT_ID";
    private static final String HEALTH_PATH = "/_floci-gcp/health";
    private static final String TLS_CERT_PATH = "/_floci-gcp/tls-cert";

    private static final String DEFAULT_PROJECT_ID = "floci-local";

    private TlsConfig tlsConfig = TlsConfig.builder().build();

    // Service configs
    private final ServiceConfigRef<ComputeConfig> computeConfig = registerServiceConfig(ComputeConfig.builder().build());
    private final ServiceConfigRef<PubSubConfig> pubSubConfig = registerServiceConfig(PubSubConfig.builder().build());
    private final ServiceConfigRef<FirestoreConfig> firestoreConfig = registerServiceConfig(FirestoreConfig.builder().build());
    private final ServiceConfigRef<DatastoreConfig> datastoreConfig = registerServiceConfig(DatastoreConfig.builder().build());
    private final ServiceConfigRef<IamCredentialsConfig> iamCredentialsConfig = registerServiceConfig(IamCredentialsConfig.builder().build());
    private final ServiceConfigRef<StsConfig> stsConfig = registerServiceConfig(StsConfig.builder().build());
    private final ServiceConfigRef<SecretManagerConfig> secretManagerConfig = registerServiceConfig(SecretManagerConfig.builder().build());
    private final ServiceConfigRef<LoggingConfig> loggingConfig = registerServiceConfig(LoggingConfig.builder().build());
    private final ServiceConfigRef<KmsConfig> kmsConfig = registerServiceConfig(KmsConfig.builder().build());
    private final ServiceConfigRef<CloudTasksConfig> cloudTasksConfig = registerServiceConfig(CloudTasksConfig.builder().build());
    private final ServiceConfigRef<MonitoringConfig> monitoringConfig = registerServiceConfig(MonitoringConfig.builder().build());
    private final ServiceConfigRef<EventarcConfig> eventarcConfig = registerServiceConfig(EventarcConfig.builder().build());
    private final ServiceConfigRef<ServiceUsageConfig> serviceUsageConfig = registerServiceConfig(ServiceUsageConfig.builder().build());
    private final ServiceConfigRef<ResourceManagerConfig> resourceManagerConfig = registerServiceConfig(ResourceManagerConfig.builder().build());
    private final ServiceConfigRef<FirebaseAuthConfig> firebaseAuthConfig = registerServiceConfig(FirebaseAuthConfig.builder().build());

    /**
     * Creates a new Floci GCP container with the default image ({@code floci/floci-gcp:latest}).
     */
    public FlociGcpContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Creates a new Floci GCP container with the specified image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-gcp})
     */
    public FlociGcpContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Creates a new Floci GCP container with the specified Docker image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-gcp})
     */
    public FlociGcpContainer(DockerImageName dockerImageName) {
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
    }

    /**
     * Returns the {@code host:port} pair of the Floci GCP container (e.g. {@code localhost:32781}), without a
     * scheme. Use it for clients that are configured via an emulator host, such as the
     * {@code PUBSUB_EMULATOR_HOST}, {@code FIRESTORE_EMULATOR_HOST} or {@code DATASTORE_EMULATOR_HOST} settings,
     * or as the target of a plaintext gRPC channel.
     *
     * @return the emulator host and port
     */
    public String getEmulatorHost() {
        return getHost() + ":" + getMappedPort(PORT);
    }

    /**
     * Returns the default project id of Floci GCP ({@value DEFAULT_PROJECT_ID}, unless changed via
     * {@link #withProjectId(String)}). Floci GCP uses it wherever a request does not name a project explicitly;
     * requests for other projects are accepted as well.
     *
     * @return the default project id
     */
    public String getProjectId() {
        return getEnvMap().getOrDefault(PROJECT_ID_ENV_VAR, DEFAULT_PROJECT_ID);
    }

    /**
     * Sets the default project id of Floci GCP (default {@value DEFAULT_PROJECT_ID}).
     *
     * @param projectId the default project id
     * @return this container instance
     */
    public FlociGcpContainer withProjectId(String projectId) {
        return withEnv(PROJECT_ID_ENV_VAR, projectId);
    }

    /**
     * Returns the HTTPS endpoint URL for connecting to Floci GCP (e.g. {@code https://localhost:32781}).
     * Floci GCP serves HTTPS on the same port as plain HTTP, but only while TLS is enabled via
     * {@link #withTlsConfig(Consumer)}. Clients have to trust the certificate returned by
     * {@link #getTlsCertificate()}.
     *
     * @return the HTTPS endpoint URL
     */
    public String getHttpsEndpoint() {
        return String.format("https://%s:%d", getHost(), getMappedPort(PORT));
    }

    /**
     * Fetches the PEM-encoded certificate Floci GCP currently serves HTTPS with (either the
     * auto-generated one or the one configured via {@link TlsConfig.Builder#certPath(String)}).
     * Import it into the trust store of HTTPS or TLS-secured gRPC clients.
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
            throw new UncheckedIOException("Failed to fetch the TLS certificate of Floci GCP", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching the TLS certificate of Floci GCP", e);
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
     * Configures TLS/HTTPS for the Floci GCP server. When enabled, HTTP and HTTPS are served on the same
     * port; use {@link #getHttpsEndpoint()} and trust {@link #getTlsCertificate()} in HTTPS clients.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withTlsConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TlsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withTlsConfig(Consumer<TlsConfig.Builder> configurer) {
        TlsConfig.Builder builder = tlsConfig.toBuilder();
        configurer.accept(builder);
        this.tlsConfig = builder.build();
        tlsConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the Compute Engine configuration.
     *
     * @return the Compute Engine configuration
     */
    public ComputeConfig getComputeConfig() {
        return computeConfig.get();
    }

    /**
     * Configures Compute Engine.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withComputeConfig(c -> c.regions(List.of("europe-west3")));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ComputeConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withComputeConfig(Consumer<ComputeConfig.Builder> configurer) {
        return updateServiceConfig(computeConfig, configurer);
    }

    /**
     * Returns the Pub/Sub configuration.
     *
     * @return the Pub/Sub configuration
     */
    public PubSubConfig getPubSubConfig() {
        return pubSubConfig.get();
    }

    /**
     * Configures Pub/Sub.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withPubSubConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link PubSubConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withPubSubConfig(Consumer<PubSubConfig.Builder> configurer) {
        return updateServiceConfig(pubSubConfig, configurer);
    }

    /**
     * Returns the Firestore configuration.
     *
     * @return the Firestore configuration
     */
    public FirestoreConfig getFirestoreConfig() {
        return firestoreConfig.get();
    }

    /**
     * Configures Firestore.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withFirestoreConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FirestoreConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withFirestoreConfig(Consumer<FirestoreConfig.Builder> configurer) {
        return updateServiceConfig(firestoreConfig, configurer);
    }

    /**
     * Returns the Datastore configuration.
     *
     * @return the Datastore configuration
     */
    public DatastoreConfig getDatastoreConfig() {
        return datastoreConfig.get();
    }

    /**
     * Configures Datastore.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withDatastoreConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link DatastoreConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withDatastoreConfig(Consumer<DatastoreConfig.Builder> configurer) {
        return updateServiceConfig(datastoreConfig, configurer);
    }

    /**
     * Returns the IAM Service Account Credentials configuration.
     *
     * @return the IAM Service Account Credentials configuration
     */
    public IamCredentialsConfig getIamCredentialsConfig() {
        return iamCredentialsConfig.get();
    }

    /**
     * Configures IAM Service Account Credentials.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withIamCredentialsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IamCredentialsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withIamCredentialsConfig(Consumer<IamCredentialsConfig.Builder> configurer) {
        return updateServiceConfig(iamCredentialsConfig, configurer);
    }

    /**
     * Returns the Security Token Service configuration.
     *
     * @return the Security Token Service configuration
     */
    public StsConfig getStsConfig() {
        return stsConfig.get();
    }

    /**
     * Configures Security Token Service.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withStsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link StsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withStsConfig(Consumer<StsConfig.Builder> configurer) {
        return updateServiceConfig(stsConfig, configurer);
    }

    /**
     * Returns the Secret Manager configuration.
     *
     * @return the Secret Manager configuration
     */
    public SecretManagerConfig getSecretManagerConfig() {
        return secretManagerConfig.get();
    }

    /**
     * Configures Secret Manager.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withSecretManagerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link SecretManagerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withSecretManagerConfig(Consumer<SecretManagerConfig.Builder> configurer) {
        return updateServiceConfig(secretManagerConfig, configurer);
    }

    /**
     * Returns the Cloud Logging configuration.
     *
     * @return the Cloud Logging configuration
     */
    public LoggingConfig getLoggingConfig() {
        return loggingConfig.get();
    }

    /**
     * Configures Cloud Logging.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withLoggingConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link LoggingConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withLoggingConfig(Consumer<LoggingConfig.Builder> configurer) {
        return updateServiceConfig(loggingConfig, configurer);
    }

    /**
     * Returns the Cloud KMS configuration.
     *
     * @return the Cloud KMS configuration
     */
    public KmsConfig getKmsConfig() {
        return kmsConfig.get();
    }

    /**
     * Configures Cloud KMS.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withKmsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KmsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withKmsConfig(Consumer<KmsConfig.Builder> configurer) {
        return updateServiceConfig(kmsConfig, configurer);
    }

    /**
     * Returns the Cloud Tasks configuration.
     *
     * @return the Cloud Tasks configuration
     */
    public CloudTasksConfig getCloudTasksConfig() {
        return cloudTasksConfig.get();
    }

    /**
     * Configures Cloud Tasks.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withCloudTasksConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link CloudTasksConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withCloudTasksConfig(Consumer<CloudTasksConfig.Builder> configurer) {
        return updateServiceConfig(cloudTasksConfig, configurer);
    }

    /**
     * Returns the Cloud Monitoring configuration.
     *
     * @return the Cloud Monitoring configuration
     */
    public MonitoringConfig getMonitoringConfig() {
        return monitoringConfig.get();
    }

    /**
     * Configures Cloud Monitoring.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withMonitoringConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link MonitoringConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withMonitoringConfig(Consumer<MonitoringConfig.Builder> configurer) {
        return updateServiceConfig(monitoringConfig, configurer);
    }

    /**
     * Returns the Eventarc configuration.
     *
     * @return the Eventarc configuration
     */
    public EventarcConfig getEventarcConfig() {
        return eventarcConfig.get();
    }

    /**
     * Configures Eventarc.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withEventarcConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link EventarcConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withEventarcConfig(Consumer<EventarcConfig.Builder> configurer) {
        return updateServiceConfig(eventarcConfig, configurer);
    }

    /**
     * Returns the Service Usage configuration.
     *
     * @return the Service Usage configuration
     */
    public ServiceUsageConfig getServiceUsageConfig() {
        return serviceUsageConfig.get();
    }

    /**
     * Configures Service Usage.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withServiceUsageConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ServiceUsageConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withServiceUsageConfig(Consumer<ServiceUsageConfig.Builder> configurer) {
        return updateServiceConfig(serviceUsageConfig, configurer);
    }

    /**
     * Returns the Resource Manager configuration.
     *
     * @return the Resource Manager configuration
     */
    public ResourceManagerConfig getResourceManagerConfig() {
        return resourceManagerConfig.get();
    }

    /**
     * Configures Resource Manager.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withResourceManagerConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ResourceManagerConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withResourceManagerConfig(Consumer<ResourceManagerConfig.Builder> configurer) {
        return updateServiceConfig(resourceManagerConfig, configurer);
    }

    /**
     * Returns the Firebase Authentication configuration.
     *
     * @return the Firebase Authentication configuration
     */
    public FirebaseAuthConfig getFirebaseAuthConfig() {
        return firebaseAuthConfig.get();
    }

    /**
     * Configures Firebase Authentication.
     *
     * <pre>{@code
     * new FlociGcpContainer()
     *     .withFirebaseAuthConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FirebaseAuthConfig.Builder} to modify
     * @return this container instance
     */
    public FlociGcpContainer withFirebaseAuthConfig(Consumer<FirebaseAuthConfig.Builder> configurer) {
        return updateServiceConfig(firebaseAuthConfig, configurer);
    }
}
