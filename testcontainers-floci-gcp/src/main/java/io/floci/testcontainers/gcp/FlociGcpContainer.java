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
    private final ServiceConfigRef<PubSubConfig> pubSubConfig = registerServiceConfig(PubSubConfig.builder().build());
    private final ServiceConfigRef<FirestoreConfig> firestoreConfig = registerServiceConfig(FirestoreConfig.builder().build());
    private final ServiceConfigRef<DatastoreConfig> datastoreConfig = registerServiceConfig(DatastoreConfig.builder().build());

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
}
