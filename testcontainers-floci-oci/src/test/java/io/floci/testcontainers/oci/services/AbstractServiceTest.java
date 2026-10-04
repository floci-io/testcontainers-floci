package io.floci.testcontainers.oci.services;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.common.ClientBuilderBase;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * Base class for Floci OCI service integration tests. Provides a shared {@link FlociOciContainer}
 * singleton (started once per JVM) and a {@link #client(ClientBuilderBase)} helper that wires the
 * endpoint and a signing authentication provider (which also supplies the region) onto an OCI SDK
 * client builder.
 */
abstract class AbstractServiceTest {

    // Integration tests run against the nightly build so newly added services are covered
    // before they land in a versioned release.
    protected static final String NIGHTLY_IMAGE = "floci/floci-oci:nightly";

    private static final boolean DEBUG_LOGGING = false;

    private static final String USER_ID = "ocid1.user.oc1..testcontainersfloci";
    // Floci OCI only parses the signature, so the fingerprint does not have to match the key
    private static final String FINGERPRINT = "aa:bb:cc:dd:ee:ff:00:11:22:33:44:55:66:77:88:99";

    protected static final FlociOciContainer floci;

    private static SimpleAuthenticationDetailsProvider authProvider;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociOciContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociOciContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.start();
    }

    /**
     * Builds an OCI SDK client from the given builder, pointing it at the shared Floci OCI container.
     *
     * @param builder the client builder, e.g. {@code ObjectStorageClient.builder()}
     */
    protected static <C> C client(ClientBuilderBase<?, C> builder) {
        return client(floci, builder);
    }

    /**
     * Same as {@link #client(ClientBuilderBase)}, but against the given container instead of the shared one.
     */
    protected static <C> C client(FlociOciContainer container, ClientBuilderBase<?, C> builder) {
        // the auth provider supplies the region; an explicit endpoint takes precedence over the region's endpoint
        builder.endpoint(container.getEndpoint());
        return builder.build(authProvider(container));
    }

    /**
     * Returns the OCID of the tenancy the requests of {@link #client(ClientBuilderBase)} clients are signed for,
     * to be used as compartment id.
     */
    protected static String tenancyId() {
        return floci.getTenancyId();
    }

    /**
     * Returns an authentication provider signing requests with a locally generated RSA key for the tenancy of
     * the given container. Floci OCI parses the signature for tenancy and user context, but never verifies it.
     */
    protected static synchronized SimpleAuthenticationDetailsProvider authProvider(FlociOciContainer container) {
        if (authProvider == null || !authProvider.getTenantId().equals(container.getTenancyId())) {
            String privateKeyPem = generatePrivateKeyPem();
            authProvider = SimpleAuthenticationDetailsProvider.builder()
                    .tenantId(container.getTenancyId())
                    .userId(USER_ID)
                    .fingerprint(FINGERPRINT)
                    .privateKeySupplier(() -> new ByteArrayInputStream(privateKeyPem.getBytes(StandardCharsets.UTF_8)))
                    .region(Region.fromRegionId(container.getRegion()))
                    .build();
        }
        return authProvider;
    }

    private static String generatePrivateKeyPem() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            return "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8))
                    .encodeToString(keyPair.getPrivate().getEncoded())
                    + "\n-----END PRIVATE KEY-----\n";
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to generate an RSA key for signing OCI requests", e);
        }
    }
}
