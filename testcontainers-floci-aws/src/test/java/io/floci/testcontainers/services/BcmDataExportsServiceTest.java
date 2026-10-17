package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.bcmdataexports.BcmDataExportsClient;
import software.amazon.awssdk.services.bcmdataexports.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class BcmDataExportsServiceTest extends AbstractServiceTest {

    private static final String EXPORT_NAME = "test-export-" + System.currentTimeMillis();
    private static final String BUCKET_NAME = "bcm-exports-bucket-" + System.currentTimeMillis();

    static BcmDataExportsClient bcmDataExports;

    @BeforeAll
    static void setUp() {
        bcmDataExports = client(BcmDataExportsClient.builder());
    }

    @Test
    @Order(1)
    void shouldListExports() {
        List<ExportReference> exports = bcmDataExports.listExports(b -> {}).exports();

        assertThat(exports).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateExport() {
        bcmDataExports.createExport(b -> b
                .export(Export.builder()
                        .name(EXPORT_NAME)
                        .dataQuery(DataQuery.builder()
                                .queryStatement("SELECT * FROM COST_AND_USAGE_REPORT")
                                .build())
                        .destinationConfigurations(DestinationConfigurations.builder()
                                .s3Destination(S3Destination.builder()
                                        .s3Bucket(BUCKET_NAME)
                                        .s3Prefix("exports/")
                                        .s3Region("us-east-1")
                                        .s3OutputConfigurations(S3OutputConfigurations.builder()
                                                .overwrite(OverwriteOption.CREATE_NEW_REPORT)
                                                .format(FormatOption.PARQUET)
                                                .compression(CompressionOption.PARQUET)
                                                .outputType(S3OutputType.CUSTOM)
                                                .build())
                                        .build())
                                .build())
                        .refreshCadence(RefreshCadence.builder()
                                .frequency(FrequencyOption.SYNCHRONOUS)
                                .build())
                        .build()));

        List<String> exportNames = bcmDataExports.listExports(b -> {}).exports().stream()
                .map(ExportReference::exportName)
                .toList();

        assertThat(exportNames).contains(EXPORT_NAME);
    }

    @Test
    @Order(3)
    void shouldGetExport() {
        String exportArn = bcmDataExports.listExports(b -> {}).exports().stream()
                .filter(e -> EXPORT_NAME.equals(e.exportName()))
                .map(ExportReference::exportArn)
                .findFirst()
                .orElseThrow();

        Export export = bcmDataExports.getExport(b -> b.exportArn(exportArn)).export();

        assertThat(export.name()).isEqualTo(EXPORT_NAME);
    }

    @Test
    @Order(4)
    void shouldDeleteExport() {
        String exportArn = bcmDataExports.listExports(b -> {}).exports().stream()
                .filter(e -> EXPORT_NAME.equals(e.exportName()))
                .map(ExportReference::exportArn)
                .findFirst()
                .orElseThrow();

        bcmDataExports.deleteExport(b -> b.exportArn(exportArn));

        List<String> exportNames = bcmDataExports.listExports(b -> {}).exports().stream()
                .map(ExportReference::exportName)
                .toList();

        assertThat(exportNames).doesNotContain(EXPORT_NAME);
    }
}
