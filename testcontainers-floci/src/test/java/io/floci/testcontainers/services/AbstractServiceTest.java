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

    // TODO: switch back to the default image (drop this constant and the constructor arg below)
    // once floci 1.6.1 — which adds AAS, SWF, Kinesis Analytics and MWAA support — reaches the
    // `floci/floci:latest` tag on Docker Hub. `latest` is still 1.6.0 as of this writing, which
    // doesn't implement these services' operations at all.
    private static final String FLOCI_IMAGE = "floci/floci:nightly";

    protected static final FlociContainer floci;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociContainer(FLOCI_IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociContainer(FLOCI_IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.withElbV2Config(c -> c.listenerPort(LB_LISTENER_PORT))
                // Real Flink JobManager containers require an application JAR staged in S3 to
                // start; mock mode exercises the full API surface without that, per its own
                // "useful for tests... without a Docker daemon" contract.
                .withKinesisAnalyticsConfig(c -> c.mock(true))
                // Real environments spin up dedicated Postgres + Airflow containers per
                // environment; mock mode exercises the full API surface without that, per its
                // own "environments go straight to AVAILABLE without starting real Docker
                // containers" contract.
                .withMwaaConfig(c -> c.mock(true))
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
