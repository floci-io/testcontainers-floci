package io.floci.testcontainers.services;

import io.floci.testcontainers.FlociContainer;
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
abstract class AbstractServiceTest {

    protected static final int LB_LISTENER_PORT = 8780;

    private static final boolean DEBUG_LOGGING = false;

    protected static final FlociContainer floci;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociContainer()
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociContainer()
                    .withLogLevel(Level.INFO);
        }

        floci.withElbV2Config(c -> c.listenerPort(LB_LISTENER_PORT))
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
