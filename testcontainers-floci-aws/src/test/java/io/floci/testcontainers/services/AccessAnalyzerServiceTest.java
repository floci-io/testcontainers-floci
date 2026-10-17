package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.accessanalyzer.AccessAnalyzerClient;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;
import software.amazon.awssdk.services.accessanalyzer.model.Type;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AccessAnalyzerServiceTest extends AbstractServiceTest {

    private static final String ANALYZER_NAME = "floci-tc-analyzer";

    static AccessAnalyzerClient accessAnalyzer;

    @BeforeAll
    static void setUp() {
        accessAnalyzer = client(AccessAnalyzerClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateAnalyzer() {
        var response = accessAnalyzer.createAnalyzer(b -> b.analyzerName(ANALYZER_NAME).type(Type.ACCOUNT));

        assertThat(response.arn()).contains(":analyzer/" + ANALYZER_NAME);
    }

    @Test
    @Order(2)
    void shouldListAnalyzer() {
        assertThat(accessAnalyzer.listAnalyzers(b -> {}).analyzers())
                .extracting(AnalyzerSummary::name)
                .contains(ANALYZER_NAME);
    }

    @Test
    @Order(3)
    void shouldDeleteAnalyzer() {
        accessAnalyzer.deleteAnalyzer(b -> b.analyzerName(ANALYZER_NAME));

        assertThat(accessAnalyzer.listAnalyzers(b -> {}).analyzers())
                .extracting(AnalyzerSummary::name)
                .doesNotContain(ANALYZER_NAME);
    }
}
