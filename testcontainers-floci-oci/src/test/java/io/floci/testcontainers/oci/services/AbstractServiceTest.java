package io.floci.testcontainers.oci.services;

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.common.ClientBuilderBase;
import io.floci.testcontainers.core.testing.TestImages;
import io.floci.testcontainers.oci.FlociOciContainer;
import io.floci.testcontainers.oci.TestAuthenticationProviders;
import org.junit.jupiter.api.Tag;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * Base class for Floci OCI service integration tests. Provides a shared {@link FlociOciContainer}
 * singleton (started once per JVM) and a {@link #client(ClientBuilderBase)} helper that wires the
 * endpoint and a signing authentication provider (which also supplies the region) onto an OCI SDK
 * client builder.
 */
@Tag("integration")
abstract class AbstractServiceTest {

    // The emulator image pinned in .github/docker-images.txt
    protected static final String IMAGE = TestImages.pinnedImage(FlociOciContainer.DESCRIPTOR);

    private static final boolean DEBUG_LOGGING = false;

    protected static final FlociOciContainer floci;

    private static final SimpleAuthenticationDetailsProvider authProvider;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociOciContainer(IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociOciContainer(IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.start();
        authProvider = TestAuthenticationProviders.forContainer(floci);
    }

    /**
     * Builds an OCI SDK client from the given builder, pointing it at the shared Floci OCI container.
     *
     * @param builder the client builder, e.g. {@code ObjectStorageClient.builder()}
     */
    protected static <C> C client(ClientBuilderBase<?, C> builder) {
        return client(floci, authProvider, builder);
    }

    /**
     * Same as {@link #client(ClientBuilderBase)}, but against the given container instead of the shared one.
     */
    protected static <C> C client(FlociOciContainer container, ClientBuilderBase<?, C> builder) {
        return client(container, TestAuthenticationProviders.forContainer(container), builder);
    }

    private static <C> C client(FlociOciContainer container, SimpleAuthenticationDetailsProvider auth,
                                ClientBuilderBase<?, C> builder) {
        // the auth provider supplies the region; an explicit endpoint takes precedence over the region's endpoint
        builder.endpoint(container.getEndpoint());
        return builder.build(auth);
    }

    /**
     * Returns the OCID of the tenancy the requests of {@link #client(ClientBuilderBase)} clients are signed for,
     * to be used as compartment id.
     */
    protected static String tenancyId() {
        return floci.getTenancyId();
    }
}
