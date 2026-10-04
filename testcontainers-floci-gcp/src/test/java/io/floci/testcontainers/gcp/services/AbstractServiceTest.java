package io.floci.testcontainers.gcp.services;

import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * Base class for Floci GCP service integration tests. Provides a shared {@link FlociGcpContainer}
 * singleton (started once per JVM).
 */
abstract class AbstractServiceTest {

    // Integration tests run against the nightly build so newly added services are covered
    // before they land in a versioned release.
    protected static final String NIGHTLY_IMAGE = "floci/floci-gcp:nightly";

    private static final boolean DEBUG_LOGGING = false;

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
}
