package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.kinesisanalyticsv2.KinesisAnalyticsV2Client;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.ApplicationStatus;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.ApplicationSummary;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.CodeContentType;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.RuntimeEnvironment;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.SnapshotStatus;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class KinesisAnalyticsServiceTest extends AbstractServiceTest {

    static KinesisAnalyticsV2Client kinesisAnalytics;

    private static final String CODE_BUCKET = "flink-code-bucket";
    private static final String CODE_KEY = "app.jar";
    private static final String APPLICATION_NAME = "test-flink-app-" + System.currentTimeMillis();
    private static final String SNAPSHOT_NAME = "test-snapshot";

    static Long applicationVersionId;
    static String applicationArn;

    @BeforeAll
    static void setUp() {
        kinesisAnalytics = client(KinesisAnalyticsV2Client.builder());

        // Real (non-mock) mode boots an actual Flink JobManager and submits the application JAR
        // to it via Flink's REST API, so the JAR must be a real, unbounded Flink job for the
        // application to ever reach RUNNING - see src/test/resources/kinesisanalytics/README.md.
        S3Client s3 = client(S3Client.builder().forcePathStyle(true));
        s3.createBucket(b -> b.bucket(CODE_BUCKET));
        s3.putObject(b -> b.bucket(CODE_BUCKET).key(CODE_KEY), RequestBody.fromBytes(readFlinkJobJar()));
    }

    private static byte[] readFlinkJobJar() {
        try (InputStream in = KinesisAnalyticsServiceTest.class.getResourceAsStream("/kinesisanalytics/flink-job.jar")) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    @Order(1)
    void shouldCreateApplication() {
        var response = kinesisAnalytics.createApplication(b -> b
                .applicationName(APPLICATION_NAME)
                .runtimeEnvironment(RuntimeEnvironment.FLINK_1_20)
                .serviceExecutionRole("arn:aws:iam::000000000000:role/kinesis-analytics-role")
                .applicationConfiguration(ac -> ac
                        .applicationCodeConfiguration(cc -> cc
                                .codeContentType(CodeContentType.ZIPFILE)
                                .codeContent(c -> c.s3ContentLocation(s3 -> s3
                                        .bucketARN("arn:aws:s3:::" + CODE_BUCKET)
                                        .fileKey(CODE_KEY))))));

        applicationArn = response.applicationDetail().applicationARN();
        applicationVersionId = response.applicationDetail().applicationVersionId();
        assertThat(applicationArn).isNotBlank();
        assertThat(response.applicationDetail().applicationStatus()).isEqualTo(ApplicationStatus.READY);
    }

    @Test
    @Order(2)
    void shouldDescribeApplication() {
        var response = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME));

        assertThat(response.applicationDetail().applicationName()).isEqualTo(APPLICATION_NAME);
        assertThat(response.applicationDetail().runtimeEnvironment()).isEqualTo(RuntimeEnvironment.FLINK_1_20);
    }

    @Test
    @Order(3)
    void shouldListApplicationsContainsCreatedApplication() {
        List<ApplicationSummary> applications = kinesisAnalytics.listApplications(b -> {}).applicationSummaries();

        assertThat(applications).anyMatch(app -> app.applicationName().equals(APPLICATION_NAME));
    }

    @Test
    @Order(4)
    void shouldStartApplication() {
        kinesisAnalytics.startApplication(b -> b.applicationName(APPLICATION_NAME));

        // Real mode boots an actual Flink JobManager container, so RUNNING is reached
        // asynchronously rather than immediately.
        await().atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var response = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME));
                    assertThat(response.applicationDetail().applicationStatus()).isEqualTo(ApplicationStatus.RUNNING);
                });
    }

    @Test
    @Order(5)
    // Floci bug: the savepoints Docker volume it mounts into the Flink JobManager container is
    // created fresh as root:root (apache/flink images don't ship /opt/flink/savepoints, so there's
    // nothing for Docker to copy ownership from), but the container itself runs as the non-root
    // "flink" user (uid 9999) - every CreateApplicationSnapshot call fails with
    // "java.io.IOException: Failed to create savepoint directory at /opt/flink/savepoints".
    // Re-enable once that's fixed upstream.
    @Disabled("Floci real-mode snapshots are broken: savepoints volume isn't writable by the flink user")
    void shouldCreateAndDescribeApplicationSnapshot() {
        kinesisAnalytics.createApplicationSnapshot(b -> b
                .applicationName(APPLICATION_NAME)
                .snapshotName(SNAPSHOT_NAME));

        // Real mode takes an actual Flink savepoint, so the snapshot leaves CREATING
        // asynchronously rather than immediately.
        await().atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var response = kinesisAnalytics.describeApplicationSnapshot(b -> b
                            .applicationName(APPLICATION_NAME)
                            .snapshotName(SNAPSHOT_NAME));
                    assertThat(response.snapshotDetails().snapshotName()).isEqualTo(SNAPSHOT_NAME);
                    assertThat(response.snapshotDetails().snapshotStatus()).isEqualTo(SnapshotStatus.READY);
                });
    }

    @Test
    @Order(6)
    @Disabled("Floci real-mode snapshots are broken: savepoints volume isn't writable by the flink user")
    void shouldListApplicationSnapshotsContainsCreatedSnapshot() {
        var summaries = kinesisAnalytics.listApplicationSnapshots(b -> b.applicationName(APPLICATION_NAME))
                .snapshotSummaries();

        assertThat(summaries).anyMatch(s -> s.snapshotName().equals(SNAPSHOT_NAME));
    }

    @Test
    @Order(7)
    @Disabled("Floci real-mode snapshots are broken: savepoints volume isn't writable by the flink user")
    void shouldDeleteApplicationSnapshot() {
        var snapshotCreationTimestamp = kinesisAnalytics.describeApplicationSnapshot(b -> b
                        .applicationName(APPLICATION_NAME)
                        .snapshotName(SNAPSHOT_NAME))
                .snapshotDetails().snapshotCreationTimestamp();

        kinesisAnalytics.deleteApplicationSnapshot(b -> b
                .applicationName(APPLICATION_NAME)
                .snapshotName(SNAPSHOT_NAME)
                .snapshotCreationTimestamp(snapshotCreationTimestamp));

        var summaries = kinesisAnalytics.listApplicationSnapshots(b -> b.applicationName(APPLICATION_NAME))
                .snapshotSummaries();
        assertThat(summaries).noneMatch(s -> s.snapshotName().equals(SNAPSHOT_NAME));
    }

    @Test
    @Order(8)
    void shouldTagAndListTagsForResource() {
        kinesisAnalytics.tagResource(b -> b
                .resourceARN(applicationArn)
                .tags(t -> t.key("env").value("test")));

        var tags = kinesisAnalytics.listTagsForResource(b -> b.resourceARN(applicationArn)).tags();
        assertThat(tags).anyMatch(t -> t.key().equals("env") && t.value().equals("test"));

        kinesisAnalytics.untagResource(b -> b.resourceARN(applicationArn).tagKeys("env"));
        tags = kinesisAnalytics.listTagsForResource(b -> b.resourceARN(applicationArn)).tags();
        assertThat(tags).noneMatch(t -> t.key().equals("env"));
    }

    @Test
    @Order(9)
    void shouldStopApplication() {
        kinesisAnalytics.stopApplication(b -> b.applicationName(APPLICATION_NAME));

        await().atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var response = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME));
                    assertThat(response.applicationDetail().applicationStatus()).isEqualTo(ApplicationStatus.READY);
                });
    }

    @Test
    @Order(10)
    void shouldUpdateApplication() {
        var response = kinesisAnalytics.updateApplication(b -> b
                .applicationName(APPLICATION_NAME)
                .currentApplicationVersionId(applicationVersionId)
                .applicationConfigurationUpdate(ac -> ac
                        .flinkApplicationConfigurationUpdate(f -> f
                                .parallelismConfigurationUpdate(p -> p.parallelismUpdate(2)))));

        assertThat(response.applicationDetail().applicationVersionId()).isEqualTo(applicationVersionId + 1);
    }

    @Test
    @Order(11)
    void shouldDeleteApplication() {
        var createTimestamp = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME))
                .applicationDetail().createTimestamp();

        kinesisAnalytics.deleteApplication(b -> b
                .applicationName(APPLICATION_NAME)
                .createTimestamp(createTimestamp));

        List<ApplicationSummary> applications = kinesisAnalytics.listApplications(b -> {}).applicationSummaries();
        assertThat(applications).noneMatch(app -> app.applicationName().equals(APPLICATION_NAME));
    }
}
