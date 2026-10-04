package io.floci.testcontainers.gcp.services;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.UUID;

/**
 * Base class for Floci GCP service integration tests. Provides a shared {@link FlociGcpContainer}
 * singleton (started once per JVM).
 */
abstract class AbstractServiceTest {

    // Integration tests run against the nightly build so newly added services are covered
    // before they land in a versioned release.
    protected static final String NIGHTLY_IMAGE = "floci/floci-gcp:nightly";

    private static final boolean DEBUG_LOGGING = true;

    protected static final FlociGcpContainer floci;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociGcpContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociGcpContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.start();
    }

    /**
     * Returns the default project id of the shared container.
     */
    protected static String projectId() {
        return floci.getProjectId();
    }

    /**
     * Returns a transport channel provider for gRPC clients, connecting to the shared container via a plaintext
     * channel. Every client created with it owns (and closes) its own channel.
     */
    protected static TransportChannelProvider grpcChannelProvider() {
        return InstantiatingGrpcChannelProvider.newBuilder()
                .setEndpoint(floci.getEmulatorHost())
                .setChannelConfigurator(builder -> builder.usePlaintext())
                .build();
    }

    /**
     * Returns a credentials provider for clients talking to Floci GCP, which does not require credentials.
     */
    protected static CredentialsProvider noCredentials() {
        return NoCredentialsProvider.create();
    }

    /**
     * Returns a unique resource name with the given prefix.
     */
    protected static String uniqueName(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
