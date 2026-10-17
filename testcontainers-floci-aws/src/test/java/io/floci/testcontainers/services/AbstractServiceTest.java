package io.floci.testcontainers.services;

import io.floci.testcontainers.FlociContainer;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

/**
 * Base class for Floci service integration tests. Provides a shared {@link FlociContainer}
 * singleton (started once per JVM) and a convenience method to build pre-configured AWS SDK clients.
 */
@Tag("integration")
abstract class AbstractServiceTest {

    protected static final int LB_LISTENER_PORT = 8780;

    // The emulator image pinned in .github/docker-images.txt
    private static final String IMAGE = TestImages.pinnedImage(FlociContainer.DESCRIPTOR);

    private static final boolean DEBUG_LOGGING = false;

    protected static final FlociContainer floci;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociContainer(IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociContainer(IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.withElbV2Config(c -> c.listenerPort(LB_LISTENER_PORT))
                // advertise the MSK brokers' host listener on the Docker host, where the test JVM can reach it
                .withMskConfig(c -> c.bootstrapHostname(floci.getHost()))
                .start();

        // Floci speaks JSON 1.1 — disable CBOR which is used by some service clients (e.g. Kinesis SDK) as default
        System.setProperty("aws.cborEnabled", "false");
    }

    protected static <B extends AwsClientBuilder<B, C>, C> C client(B builder) {
        return builder
                .endpointOverride(URI.create(floci.getEndpoint()))
                .region(Region.of(floci.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(floci.getAccessKey(), floci.getSecretKey())))
                // Some services (e.g. MWAA) inject a per-operation host prefix (e.g. "api.")
                // even when a custom endpoint is set, which breaks routing to a single local
                // container. It's meaningless against a local endpoint, so disable it.
                .overrideConfiguration(o -> o.putAdvancedOption(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, true))
                .build();
    }

}
