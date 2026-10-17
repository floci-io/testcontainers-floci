package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TextractServiceTest extends AbstractServiceTest {

    static TextractClient textract;

    private static final Document DOCUMENT = Document.builder()
            .s3Object(S3Object.builder().bucket("my-bucket").name("test.pdf").build())
            .build();

    private static final DocumentLocation DOCUMENT_LOCATION = DocumentLocation.builder()
            .s3Object(S3Object.builder().bucket("my-bucket").name("test.pdf").build())
            .build();

    @BeforeAll
    static void setUp() {
        textract = client(TextractClient.builder());
    }

    @Test
    void shouldDetectDocumentText() {
        List<Block> blocks = textract.detectDocumentText(b -> b.document(DOCUMENT)).blocks();

        assertThat(blocks).isNotEmpty();
        Map<BlockType, List<Block>> byType = blocks.stream().collect(Collectors.groupingBy(Block::blockType));
        assertThat(byType).containsKeys(BlockType.PAGE, BlockType.LINE, BlockType.WORD);
    }

    @Test
    void shouldAnalyzeDocument() {
        List<Block> blocks = textract.analyzeDocument(b -> b
                .document(DOCUMENT)
                .featureTypes(FeatureType.TABLES, FeatureType.FORMS))
                .blocks();

        assertThat(blocks).isNotEmpty();
        Map<BlockType, List<Block>> byType = blocks.stream().collect(Collectors.groupingBy(Block::blockType));
        assertThat(byType).containsKeys(BlockType.PAGE, BlockType.LINE, BlockType.WORD);
    }

    @Test
    void shouldStartAndGetDocumentTextDetection() {
        String jobId = textract.startDocumentTextDetection(b -> b
                .documentLocation(DOCUMENT_LOCATION))
                .jobId();

        assertThat(jobId).isNotBlank();

        GetDocumentTextDetectionResponse result = textract.getDocumentTextDetection(b -> b.jobId(jobId));

        assertThat(result.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);
        assertThat(result.blocks()).isNotEmpty();
    }

    @Test
    void shouldStartAndGetDocumentAnalysis() {
        String jobId = textract.startDocumentAnalysis(b -> b
                .documentLocation(DOCUMENT_LOCATION)
                .featureTypes(FeatureType.TABLES, FeatureType.FORMS))
                .jobId();

        assertThat(jobId).isNotBlank();

        GetDocumentAnalysisResponse result = textract.getDocumentAnalysis(b -> b.jobId(jobId));

        assertThat(result.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);
        assertThat(result.blocks()).isNotEmpty();
    }
}
