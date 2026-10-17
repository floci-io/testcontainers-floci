package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.costandusagereport.CostAndUsageReportClient;
import software.amazon.awssdk.services.costandusagereport.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class CurServiceTest extends AbstractServiceTest {

    private static final String REPORT_NAME = "test-report-" + System.currentTimeMillis();
    private static final String BUCKET_NAME = "cur-test-bucket-" + System.currentTimeMillis();

    static CostAndUsageReportClient cur;

    @BeforeAll
    static void setUp() {
        cur = client(CostAndUsageReportClient.builder());
    }

    @Test
    @Order(1)
    void shouldDescribeReportDefinitions() {
        List<ReportDefinition> definitions = cur.describeReportDefinitions(b -> {}).reportDefinitions();

        assertThat(definitions).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateReportDefinition() {
        cur.putReportDefinition(b -> b
                .reportDefinition(ReportDefinition.builder()
                        .reportName(REPORT_NAME)
                        .timeUnit(TimeUnit.MONTHLY)
                        .format(ReportFormat.PARQUET)
                        .compression(CompressionFormat.PARQUET)
                        .additionalSchemaElements(SchemaElement.RESOURCES)
                        .s3Bucket(BUCKET_NAME)
                        .s3Prefix("reports/")
                        .s3Region(AWSRegion.US_EAST_1)
                        .reportVersioning(ReportVersioning.CREATE_NEW_REPORT)
                        .build()));

        List<String> reportNames = cur.describeReportDefinitions(b -> {}).reportDefinitions().stream()
                .map(ReportDefinition::reportName)
                .toList();

        assertThat(reportNames).contains(REPORT_NAME);
    }

    @Test
    @Order(3)
    void shouldDeleteReportDefinition() {
        cur.deleteReportDefinition(b -> b.reportName(REPORT_NAME));

        List<String> reportNames = cur.describeReportDefinitions(b -> {}).reportDefinitions().stream()
                .map(ReportDefinition::reportName)
                .toList();

        assertThat(reportNames).doesNotContain(REPORT_NAME);
    }
}
