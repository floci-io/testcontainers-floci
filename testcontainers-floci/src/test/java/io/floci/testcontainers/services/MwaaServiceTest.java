package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import software.amazon.awssdk.services.mwaa.MwaaClient;
import software.amazon.awssdk.services.mwaa.model.EnvironmentStatus;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

// TEMPORARY INSTRUMENTATION - not for merge as-is. Added to profile where
// MwaaServiceTest's wall-clock time actually goes (per-method timings, poll-by-poll
// status of the real Airflow bootstrap, and Floci's own container log alongside it).
@TestMethodOrder(OrderAnnotation.class)
class MwaaServiceTest extends AbstractServiceTest {

    private static final Logger TIMING = LoggerFactory.getLogger("MWAA_TIMING");

    static MwaaClient mwaa;

    private static final String SOURCE_BUCKET = "mwaa-bucket";
    private static final String ENVIRONMENT_NAME = "test-environment-" + System.currentTimeMillis();

    private static Instant classStart;
    private Instant testStart;

    @BeforeAll
    static void setUp() {
        classStart = Instant.now();

        // Stream Floci's own container log for the duration of this class so slow phases
        // (Postgres start, Airflow DB migrate, scheduler/webserver boot) show up interleaved
        // with the timings logged below. Attaching it here rather than in AbstractServiceTest
        // keeps the noise scoped to this investigation - fine as long as this class is run alone.
        floci.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("FLOCI_CONTAINER")));

        Instant t0 = Instant.now();
        mwaa = client(MwaaClient.builder());
        TIMING.info("client setup took {}", Duration.between(t0, Instant.now()));

        // Real (non-mock) mode doesn't validate the bucket up front, but it does poll
        // DagS3Path from it once the environment is up - create the bucket for realism.
        Instant t1 = Instant.now();
        S3Client s3 = client(S3Client.builder().forcePathStyle(true));
        s3.createBucket(b -> b.bucket(SOURCE_BUCKET));
        TIMING.info("bucket creation took {}", Duration.between(t1, Instant.now()));
    }

    @AfterAll
    static void logClassTotal() {
        TIMING.info("=== class total: {} ===", Duration.between(classStart, Instant.now()));
    }

    @BeforeEach
    void startTimer(TestInfo info) {
        testStart = Instant.now();
        TIMING.info(">>> {} starting ({} since class start)",
                info.getDisplayName(), Duration.between(classStart, testStart));
    }

    @AfterEach
    void stopTimer(TestInfo info) {
        TIMING.info("<<< {} finished, took {}", info.getDisplayName(), Duration.between(testStart, Instant.now()));
    }

    @Test
    @Order(1)
    void shouldCreateEnvironment() {
        Instant t0 = Instant.now();
        var response = mwaa.createEnvironment(b -> b
                .name(ENVIRONMENT_NAME)
                .executionRoleArn("arn:aws:iam::000000000000:role/mwaa-role")
                .sourceBucketArn("arn:aws:s3:::" + SOURCE_BUCKET)
                .dagS3Path("dags"));
        TIMING.info("createEnvironment call took {}", Duration.between(t0, Instant.now()));

        assertThat(response.arn()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetEnvironment() {
        // Real mode boots dedicated Postgres + Airflow containers and only reaches AVAILABLE
        // once Airflow's own /health endpoint reports the metadatabase and scheduler healthy,
        // so this happens asynchronously rather than immediately.
        AtomicInteger pollCount = new AtomicInteger();
        Instant pollingStart = Instant.now();

        await().atMost(Duration.ofSeconds(300))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    int attempt = pollCount.incrementAndGet();
                    Instant pollT0 = Instant.now();
                    var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
                    Duration callDuration = Duration.between(pollT0, Instant.now());
                    var status = response.environment().status();
                    TIMING.info("poll #{} at +{}: status={} (call took {})",
                            attempt, Duration.between(pollingStart, Instant.now()), status, callDuration);
                    assertThat(status).isEqualTo(EnvironmentStatus.AVAILABLE);
                });

        TIMING.info("environment reached AVAILABLE after {} ({} polls)",
                Duration.between(pollingStart, Instant.now()), pollCount.get());

        var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
        assertThat(response.environment().name()).isEqualTo(ENVIRONMENT_NAME);
        assertThat(response.environment().airflowVersion()).isEqualTo("2.10.5");
    }

    @Test
    @Order(3)
    void shouldListEnvironmentsContainsCreatedEnvironment() {
        Instant t0 = Instant.now();
        List<String> environments = mwaa.listEnvironments(b -> {}).environments();
        TIMING.info("listEnvironments call took {}", Duration.between(t0, Instant.now()));

        assertThat(environments).contains(ENVIRONMENT_NAME);
    }

    @Test
    @Order(4)
    void shouldUpdateEnvironment() {
        String newRoleArn = "arn:aws:iam::000000000000:role/mwaa-role-updated";

        Instant t0 = Instant.now();
        mwaa.updateEnvironment(b -> b
                .name(ENVIRONMENT_NAME)
                .executionRoleArn(newRoleArn));
        TIMING.info("updateEnvironment call took {}", Duration.between(t0, Instant.now()));

        var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
        assertThat(response.environment().executionRoleArn()).isEqualTo(newRoleArn);
    }

    @Test
    @Order(5)
    void shouldCreateWebLoginToken() {
        Instant t0 = Instant.now();
        var response = mwaa.createWebLoginToken(b -> b.name(ENVIRONMENT_NAME));
        TIMING.info("createWebLoginToken call took {}", Duration.between(t0, Instant.now()));

        assertThat(response.webToken()).isNotBlank();
        assertThat(response.webServerHostname()).isNotBlank();
    }

    @Test
    @Order(6)
    void shouldCreateCliToken() {
        Instant t0 = Instant.now();
        var response = mwaa.createCliToken(b -> b.name(ENVIRONMENT_NAME));
        TIMING.info("createCliToken call took {}", Duration.between(t0, Instant.now()));

        assertThat(response.cliToken()).isNotBlank();
        assertThat(response.webServerHostname()).isNotBlank();
    }

    @Test
    @Order(7)
    void shouldDeleteEnvironment() {
        Instant t0 = Instant.now();
        mwaa.deleteEnvironment(b -> b.name(ENVIRONMENT_NAME));
        TIMING.info("deleteEnvironment call took {}", Duration.between(t0, Instant.now()));

        List<String> environments = mwaa.listEnvironments(b -> {}).environments();
        assertThat(environments).doesNotContain(ENVIRONMENT_NAME);
    }
}
