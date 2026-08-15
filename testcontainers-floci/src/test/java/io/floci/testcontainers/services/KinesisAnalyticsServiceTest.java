package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.kinesisanalyticsv2.KinesisAnalyticsV2Client;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.ApplicationStatus;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.ApplicationSummary;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.CodeContentType;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.RuntimeEnvironment;
import software.amazon.awssdk.services.kinesisanalyticsv2.model.SnapshotStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class KinesisAnalyticsServiceTest extends AbstractServiceTest {

    static KinesisAnalyticsV2Client kinesisAnalytics;

    private static final String APPLICATION_NAME = "test-flink-app-" + System.currentTimeMillis();
    private static final String SNAPSHOT_NAME = "test-snapshot";

    static Long applicationVersionId;
    static String applicationArn;

    @BeforeAll
    static void setUp() {
        kinesisAnalytics = client(KinesisAnalyticsV2Client.builder());
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
                                        .bucketARN("arn:aws:s3:::flink-code-bucket")
                                        .fileKey("app.jar"))))));

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

        var response = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME));
        assertThat(response.applicationDetail().applicationStatus()).isEqualTo(ApplicationStatus.RUNNING);
    }

    @Test
    @Order(5)
    void shouldCreateAndDescribeApplicationSnapshot() {
        kinesisAnalytics.createApplicationSnapshot(b -> b
                .applicationName(APPLICATION_NAME)
                .snapshotName(SNAPSHOT_NAME));

        var response = kinesisAnalytics.describeApplicationSnapshot(b -> b
                .applicationName(APPLICATION_NAME)
                .snapshotName(SNAPSHOT_NAME));
        assertThat(response.snapshotDetails().snapshotName()).isEqualTo(SNAPSHOT_NAME);
        assertThat(response.snapshotDetails().snapshotStatus()).isEqualTo(SnapshotStatus.READY);
    }

    @Test
    @Order(6)
    void shouldListApplicationSnapshotsContainsCreatedSnapshot() {
        var summaries = kinesisAnalytics.listApplicationSnapshots(b -> b.applicationName(APPLICATION_NAME))
                .snapshotSummaries();

        assertThat(summaries).anyMatch(s -> s.snapshotName().equals(SNAPSHOT_NAME));
    }

    @Test
    @Order(7)
    void shouldDeleteApplicationSnapshot() {
        kinesisAnalytics.deleteApplicationSnapshot(b -> b
                .applicationName(APPLICATION_NAME)
                .snapshotName(SNAPSHOT_NAME));

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

        var response = kinesisAnalytics.describeApplication(b -> b.applicationName(APPLICATION_NAME));
        assertThat(response.applicationDetail().applicationStatus()).isEqualTo(ApplicationStatus.READY);
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
