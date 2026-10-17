package io.floci.testcontainers.oci;

import io.floci.testcontainers.core.AbstractFlociContainer;
import io.floci.testcontainers.core.CloudDescriptor;
import io.floci.testcontainers.oci.config.AuthConfig;
import io.floci.testcontainers.oci.config.TlsConfig;
import io.floci.testcontainers.oci.config.services.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Testcontainers module for <a href="https://github.com/floci-io/floci-oci">Floci OCI</a> — a
 * free, open-source local Oracle Cloud Infrastructure (OCI) emulator.
 *
 * <p>Starts a Floci OCI container that exposes all emulated OCI services on a single HTTP endpoint.
 * Use {@link #getEndpoint()} as the endpoint of OCI SDK clients, together with {@link #getRegion()}.
 * Floci OCI parses, but never verifies, request signatures, so an authentication provider with any
 * locally generated RSA key works.
 *
 * <p>Container-based services (Functions and OKE) require access to the Docker daemon. This module automatically
 * mounts the Docker socket only when at least one currently enabled service actually needs it. Use
 * {@link #withDockerSocket(boolean)} to override this auto-detection.
 *
 * <pre>{@code
 * try (FlociOciContainer floci = new FlociOciContainer()) {
 *     floci.start();
 *     OciConfigProfile profile = floci.getConfigProfile();
 *     ObjectStorageClient client = ObjectStorageClient.builder()
 *         .endpoint(floci.getEndpoint())
 *         .build(new ConfigFileAuthenticationDetailsProvider(profile.writeConfigFile().toString(), profile.getName()));
 * }
 * }</pre>
 */
public class FlociOciContainer extends AbstractFlociContainer<FlociOciContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("floci/floci-oci");
    private static final String DEFAULT_TAG = "latest";

    /**
     * Port Floci OCI serves all emulated services on.
     */
    public static final int PORT = 4599;

    /**
     * Description of the Floci OCI emulator. Every container starts with {@code FLOCI_OCI_TLS_HTTPS_PORT=0}, so no
     * extra HTTPS listener is bound on 443.
     */
    public static final CloudDescriptor DESCRIPTOR = CloudDescriptor.builder()
            .name("oci")
            .image(DEFAULT_IMAGE_NAME.getUnversionedPart())
            .port(PORT)
            .envPrefix("FLOCI_OCI_")
            .healthPath("/_floci-oci/health")
            .resetPath("/_floci-oci/state/reset")
            .logLevelEnvVar("QUARKUS_LOG_CATEGORY__IO_FLOCI_OCI__LEVEL")
            .networkEnvVar("FLOCI_OCI_SERVICES_DOCKER_NETWORK")
            .resourceNamespaceEnvVar("FLOCI_OCI_DOCKER_RESOURCE_NAMESPACE")
            .tlsHttpsPortEnvVar("FLOCI_OCI_TLS_HTTPS_PORT")
            .defaultEnv("FLOCI_OCI_TLS_HTTPS_PORT", "0")
            .build();

    private static final String INIT_PATH = "/_floci-oci/init";
    private static final Pattern READY_PATTERN = Pattern.compile("\"ready\"\\s*:\\s*true");
    private static final String NOT_AUTHENTICATED_CODE = "\"NotAuthenticated\"";
    private static final String SELF_SIGNED_CERT_PATH = "/app/data/tls/floci-selfsigned.crt";

    private static final String REGION_ENV_VAR = "FLOCI_OCI_DEFAULT_REGION";
    private static final String REALM_ENV_VAR = "FLOCI_OCI_DEFAULT_REALM";
    private static final String TENANCY_ID_ENV_VAR = "FLOCI_OCI_DEFAULT_TENANCY_ID";
    private static final String NAMESPACE_ENV_VAR = "FLOCI_OCI_DEFAULT_NAMESPACE";

    private static final String DEFAULT_REGION = "us-ashburn-1";
    private static final String DEFAULT_REALM = "oc1";
    private static final String DEFAULT_TENANCY_ID =
            "ocid1.tenancy.oc1..flocilocaltenancy0000000000000000000000000000000000000000";
    private static final String DEFAULT_NAMESPACE = "floci-local";

    private static final String CONFIG_PROFILE_USER_ID = "ocid1.user.oc1..testcontainersfloci";
    private static final int SIGNING_KEY_SIZE = 2048;

    private TlsConfig tlsConfig = TlsConfig.builder().build();
    private AuthConfig authConfig = AuthConfig.builder().build();

    // throwaway signing key of getConfigProfile(), generated on first use
    private Path signingKeyFile;
    private String signingKeyFingerprint;

    // Service configs
    private final ServiceConfigRef<IdentityConfig> identityConfig = registerServiceConfig(IdentityConfig.builder().build());
    private final ServiceConfigRef<ObjectStorageConfig> objectStorageConfig = registerServiceConfig(ObjectStorageConfig.builder().build());
    private final ServiceConfigRef<QueueConfig> queueConfig = registerServiceConfig(QueueConfig.builder().build());
    private final ServiceConfigRef<KmsConfig> kmsConfig = registerServiceConfig(KmsConfig.builder().build());
    private final ServiceConfigRef<VaultConfig> vaultConfig = registerServiceConfig(VaultConfig.builder().build());
    private final ServiceConfigRef<StreamingConfig> streamingConfig = registerServiceConfig(StreamingConfig.builder().build());
    private final ServiceConfigRef<FunctionsConfig> functionsConfig = registerServiceConfig(FunctionsConfig.builder().build());
    private final ServiceConfigRef<OkeConfig> okeConfig = registerServiceConfig(OkeConfig.builder().build());

    /**
     * Creates a new Floci OCI container with the default image ({@code floci/floci-oci:latest}).
     */
    public FlociOciContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Creates a new Floci OCI container with the specified image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-oci})
     */
    public FlociOciContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Creates a new Floci OCI container with the specified Docker image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-oci})
     */
    public FlociOciContainer(DockerImageName dockerImageName) {
        super(dockerImageName, DESCRIPTOR);

        // In addition to the health check, wait for the startup hooks:
        // /_floci-oci/init reports "ready": true once all startup hooks have completed. While signatures are
        // required (AuthConfig#requireSignature), Floci OCI also rejects unsigned requests to its own
        // /_floci-oci endpoints with 401 NotAuthenticated, which is then the best readiness signal available.
        waitingFor(Wait.forHttp(INIT_PATH)
                .forPort(PORT)
                .forStatusCodeMatching(status -> status == 200 || status == 401)
                .forResponsePredicate(body -> READY_PATTERN.matcher(body).find()
                        || body.contains(NOT_AUTHENTICATED_CODE))
                .withStartupTimeout(Duration.ofSeconds(60)));

        withRegion(DEFAULT_REGION);
        withRealm(DEFAULT_REALM);
        withTenancyId(DEFAULT_TENANCY_ID);
        withNamespace(DEFAULT_NAMESPACE);

        applyAllConfigs();
    }

    @Override
    protected void applyGlobalEnvVars() {
        tlsConfig.applyEnvVarsToContainer(this);
        authConfig.applyEnvVarsToContainer(this);
    }

    /**
     * Returns the endpoint URL for connecting to Floci OCI (e.g. {@code http://localhost:32781}). While TLS is
     * enabled via {@link #withTlsConfig(Consumer)}, this is an {@code https://} URL, matching the URLs Floci OCI
     * returns in API responses; clients then have to trust the certificate returned by
     * {@link #getTlsCertificate()}. Plain HTTP keeps working on the same port.
     *
     * @return the endpoint URL
     */
    @Override
    public String getEndpoint() {
        return String.format("%s://%s:%d", tlsConfig.isEnabled() ? "https" : "http", getHost(), getMappedPort(PORT));
    }

    /**
     * Reads the PEM-encoded certificate Floci OCI serves HTTPS with from the running container: either the one
     * configured via {@link TlsConfig.Builder#certPath(String)}, or the auto-generated self-signed one. Import it
     * into the trust store of HTTPS clients.
     *
     * @return the PEM-encoded TLS certificate
     * @throws IllegalStateException if TLS is not enabled
     */
    public String getTlsCertificate() {
        if (!tlsConfig.isEnabled()) {
            throw new IllegalStateException("TLS is not enabled, see withTlsConfig(...)");
        }

        String certPath = tlsConfig.getCertPath().orElse(SELF_SIGNED_CERT_PATH);
        return copyFileFromContainer(certPath, in -> new String(in.readAllBytes(), StandardCharsets.UTF_8));
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
     * Configures TLS/HTTPS for the Floci OCI server. When enabled, HTTP and HTTPS are served on the same port;
     * {@link #getEndpoint()} returns an {@code https://} URL and HTTPS clients have to trust
     * {@link #getTlsCertificate()}.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withTlsConfig(c -> c.enabled(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link TlsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withTlsConfig(Consumer<TlsConfig.Builder> configurer) {
        TlsConfig.Builder builder = tlsConfig.toBuilder();
        configurer.accept(builder);
        this.tlsConfig = builder.build();
        tlsConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the default region of Floci OCI (default {@value DEFAULT_REGION}), e.g. for the {@code region(...)}
     * of OCI SDK client builders.
     *
     * @return the region identifier
     */
    public String getRegion() {
        return getEnvMap().get(REGION_ENV_VAR);
    }

    /**
     * Sets the default region of Floci OCI, used for region-scoped OCIDs and in URLs Floci OCI returns.
     *
     * @param region the region identifier (default {@value DEFAULT_REGION})
     * @return this container instance
     */
    public FlociOciContainer withRegion(String region) {
        return withEnv(REGION_ENV_VAR, region);
    }

    /**
     * Returns the OCI realm key Floci OCI uses when minting OCIDs (default {@value DEFAULT_REALM}).
     *
     * @return the realm key
     */
    public String getRealm() {
        return getEnvMap().get(REALM_ENV_VAR);
    }

    /**
     * Sets the OCI realm key Floci OCI uses when minting OCIDs ({@code oc1} is the commercial realm).
     *
     * @param realm the realm key (default {@value DEFAULT_REALM})
     * @return this container instance
     */
    public FlociOciContainer withRealm(String realm) {
        return withEnv(REALM_ENV_VAR, realm);
    }

    /**
     * Returns the OCID of the default tenancy. Floci OCI uses it for requests that carry no parseable
     * {@code Authorization} signature; signed requests use the tenancy of their signing key instead.
     *
     * @return the tenancy OCID
     */
    public String getTenancyId() {
        return getEnvMap().get(TENANCY_ID_ENV_VAR);
    }

    /**
     * Sets the OCID of the default tenancy, used for requests that carry no parseable {@code Authorization}
     * signature.
     *
     * @param tenancyId the tenancy OCID (default {@value DEFAULT_TENANCY_ID})
     * @return this container instance
     */
    public FlociOciContainer withTenancyId(String tenancyId) {
        return withEnv(TENANCY_ID_ENV_VAR, tenancyId);
    }

    /**
     * Returns the Object Storage namespace of the emulated tenancy (default {@value DEFAULT_NAMESPACE}).
     *
     * @return the Object Storage namespace
     */
    public String getNamespace() {
        return getEnvMap().get(NAMESPACE_ENV_VAR);
    }

    /**
     * Sets the Object Storage namespace of the emulated tenancy, as returned by {@code GetNamespace}. Floci OCI
     * accepts any namespace on read.
     *
     * @param namespace the Object Storage namespace (default {@value DEFAULT_NAMESPACE})
     * @return this container instance
     */
    public FlociOciContainer withNamespace(String namespace) {
        return withEnv(NAMESPACE_ENV_VAR, namespace);
    }

    /**
     * Returns an OCI configuration file profile (user, tenancy, fingerprint, key file, region) for signing requests
     * to this container, which the OCI SDKs' file-based authentication can load (see {@link OciConfigProfile}).
     *
     * <p>Floci OCI parses, but never verifies, request signatures, so the profile signs with a throwaway RSA key that
     * is generated once per container and stored in a temporary PEM file deleted when the JVM exits. The tenancy and
     * region are the container's current {@link #getTenancyId()} and {@link #getRegion()}. The container does not
     * have to be running.
     *
     * @return the configuration profile
     * @throws UncheckedIOException  if the key file cannot be written
     * @throws IllegalStateException if no RSA key can be generated
     */
    public synchronized OciConfigProfile getConfigProfile() {
        if (signingKeyFile == null) {
            KeyPair keyPair = generateSigningKey();
            signingKeyFingerprint = fingerprint(keyPair);
            signingKeyFile = writePrivateKey(keyPair);
        }
        return new OciConfigProfile(OciConfigProfile.DEFAULT_PROFILE_NAME, CONFIG_PROFILE_USER_ID,
                signingKeyFingerprint, getTenancyId(), getRegion(), signingKeyFile);
    }

    private static KeyPair generateSigningKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(SIGNING_KEY_SIZE);
            return generator.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to generate an RSA key for signing OCI requests", e);
        }
    }

    /**
     * Returns the OCI API key fingerprint: the MD5 digest of the DER-encoded public key as colon-separated hex.
     */
    private static String fingerprint(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(keyPair.getPublic().getEncoded());
            StringJoiner fingerprint = new StringJoiner(":");
            for (byte b : digest) {
                fingerprint.add(String.format("%02x", b));
            }
            return fingerprint.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute the fingerprint of the OCI signing key", e);
        }
    }

    private static Path writePrivateKey(KeyPair keyPair) {
        String pem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
                .encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
        try {
            Path file = Files.createTempFile("floci-oci-key-", ".pem");
            file.toFile().deleteOnExit();
            return Files.writeString(file, pem, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write the OCI signing key", e);
        }
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
     * Configures authentication of the Floci OCI server, e.g. to reject unsigned requests:
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withAuthConfig(c -> c.requireSignature(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link AuthConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withAuthConfig(Consumer<AuthConfig.Builder> configurer) {
        AuthConfig.Builder builder = authConfig.toBuilder();
        configurer.accept(builder);
        this.authConfig = builder.build();
        authConfig.applyEnvVarsToContainer(this);
        return this;
    }

    /**
     * Returns the OCI Identity and Access Management configuration.
     *
     * @return the OCI Identity and Access Management configuration
     */
    public IdentityConfig getIdentityConfig() {
        return identityConfig.get();
    }

    /**
     * Configures OCI Identity and Access Management.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withIdentityConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link IdentityConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withIdentityConfig(Consumer<IdentityConfig.Builder> configurer) {
        return updateServiceConfig(identityConfig, configurer);
    }

    /**
     * Returns the OCI Object Storage configuration.
     *
     * @return the OCI Object Storage configuration
     */
    public ObjectStorageConfig getObjectStorageConfig() {
        return objectStorageConfig.get();
    }

    /**
     * Configures OCI Object Storage.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withObjectStorageConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link ObjectStorageConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withObjectStorageConfig(Consumer<ObjectStorageConfig.Builder> configurer) {
        return updateServiceConfig(objectStorageConfig, configurer);
    }

    /**
     * Returns the OCI Queue configuration.
     *
     * @return the OCI Queue configuration
     */
    public QueueConfig getQueueConfig() {
        return queueConfig.get();
    }

    /**
     * Configures OCI Queue.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withQueueConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link QueueConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withQueueConfig(Consumer<QueueConfig.Builder> configurer) {
        return updateServiceConfig(queueConfig, configurer);
    }

    /**
     * Returns the OCI Key Management configuration.
     *
     * @return the OCI Key Management configuration
     */
    public KmsConfig getKmsConfig() {
        return kmsConfig.get();
    }

    /**
     * Configures OCI Key Management.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withKmsConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link KmsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withKmsConfig(Consumer<KmsConfig.Builder> configurer) {
        return updateServiceConfig(kmsConfig, configurer);
    }

    /**
     * Returns the OCI Vault secrets configuration.
     *
     * @return the OCI Vault secrets configuration
     */
    public VaultConfig getVaultConfig() {
        return vaultConfig.get();
    }

    /**
     * Configures OCI Vault secrets.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withVaultConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link VaultConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withVaultConfig(Consumer<VaultConfig.Builder> configurer) {
        return updateServiceConfig(vaultConfig, configurer);
    }

    /**
     * Returns the OCI Streaming configuration.
     *
     * @return the OCI Streaming configuration
     */
    public StreamingConfig getStreamingConfig() {
        return streamingConfig.get();
    }

    /**
     * Configures OCI Streaming.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withStreamingConfig(c -> c.enabled(false));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link StreamingConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withStreamingConfig(Consumer<StreamingConfig.Builder> configurer) {
        return updateServiceConfig(streamingConfig, configurer);
    }

    /**
     * Returns the OCI Functions configuration.
     *
     * @return the OCI Functions configuration
     */
    public FunctionsConfig getFunctionsConfig() {
        return functionsConfig.get();
    }

    /**
     * Configures OCI Functions, which runs invocations in a sibling Fn server container and therefore requires
     * the Docker socket unless mocked.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withFunctionsConfig(c -> c.mock(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link FunctionsConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withFunctionsConfig(Consumer<FunctionsConfig.Builder> configurer) {
        return updateServiceConfig(functionsConfig, configurer);
    }

    /**
     * Returns the Oracle Container Engine for Kubernetes (OKE) configuration.
     *
     * @return the Oracle Container Engine for Kubernetes (OKE) configuration
     */
    public OkeConfig getOkeConfig() {
        return okeConfig.get();
    }

    /**
     * Configures Oracle Container Engine for Kubernetes (OKE), which runs clusters in sibling k3s containers and
     * therefore requires the Docker socket unless mocked.
     *
     * <pre>{@code
     * new FlociOciContainer()
     *     .withOkeConfig(c -> c.mock(true));
     * }</pre>
     *
     * @param configurer a consumer that receives a {@link OkeConfig.Builder} to modify
     * @return this container instance
     */
    public FlociOciContainer withOkeConfig(Consumer<OkeConfig.Builder> configurer) {
        return updateServiceConfig(okeConfig, configurer);
    }
}
