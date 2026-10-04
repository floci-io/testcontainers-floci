package io.floci.testcontainers.oci;

import io.floci.testcontainers.core.AbstractFlociContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
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
 * <pre>{@code
 * try (FlociOciContainer floci = new FlociOciContainer()) {
 *     floci.start();
 *     ObjectStorageClient client = ObjectStorageClient.builder()
 *         .region(floci.getRegion())
 *         .build(authProvider);
 *     client.setEndpoint(floci.getEndpoint());
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

    private static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_OCI__LEVEL";
    private static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_OCI_SERVICES_DOCKER_NETWORK";
    private static final String INIT_PATH = "/_floci-oci/init";
    private static final Pattern READY_PATTERN = Pattern.compile("\"ready\"\\s*:\\s*true");

    private static final String REGION_ENV_VAR = "FLOCI_OCI_DEFAULT_REGION";
    private static final String REALM_ENV_VAR = "FLOCI_OCI_DEFAULT_REALM";
    private static final String TENANCY_ID_ENV_VAR = "FLOCI_OCI_DEFAULT_TENANCY_ID";
    private static final String NAMESPACE_ENV_VAR = "FLOCI_OCI_DEFAULT_NAMESPACE";

    private static final String DEFAULT_REGION = "us-ashburn-1";
    private static final String DEFAULT_REALM = "oc1";
    private static final String DEFAULT_TENANCY_ID =
            "ocid1.tenancy.oc1..flocilocaltenancy0000000000000000000000000000000000000000";
    private static final String DEFAULT_NAMESPACE = "floci-local";

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
        super(dockerImageName, DEFAULT_IMAGE_NAME, PORT, LOG_LEVEL_ENV_VAR, DOCKER_NETWORK_ENV_VAR);

        // /_floci-oci/init reports "ready": true once all startup hooks have completed
        waitingFor(Wait.forHttp(INIT_PATH)
                .forPort(PORT)
                .forStatusCode(200)
                .forResponsePredicate(body -> READY_PATTERN.matcher(body).find())
                .withStartupTimeout(Duration.ofSeconds(60)));

        withRegion(DEFAULT_REGION);
        withRealm(DEFAULT_REALM);
        withTenancyId(DEFAULT_TENANCY_ID);
        withNamespace(DEFAULT_NAMESPACE);

        applyAllConfigs();
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
}
