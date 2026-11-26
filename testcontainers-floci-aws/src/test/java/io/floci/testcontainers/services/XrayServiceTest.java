package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.BatchGetTracesResponse;
import software.amazon.awssdk.services.xray.model.SamplingRuleRecord;
import software.amazon.awssdk.services.xray.model.ServiceId;
import software.amazon.awssdk.services.xray.model.TraceSummary;

import java.time.Instant;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class XrayServiceTest extends AbstractServiceTest {

    private static final Instant NOW = Instant.now();

    static XRayClient xray;

    @BeforeAll
    static void setUp() {
        xray = client(XRayClient.builder());
    }

    /** A root segment that called a second service. */
    private static String segment(String traceId) {
        double base = NOW.getEpochSecond();
        return String.format(Locale.ROOT,
                "{\"trace_id\":\"%s\",\"id\":\"0f1e2d3c4b5a6978\",\"name\":\"orders\","
                        + "\"start_time\":%.1f,\"end_time\":%.1f,"
                        + "\"http\":{\"response\":{\"status\":200}},"
                        + "\"subsegments\":[{\"id\":\"8a7b6c5d4e3f2019\",\"name\":\"payments\","
                        + "\"start_time\":%.1f,\"end_time\":%.1f}]}",
                traceId, base, base + 1.5, base + 0.2, base + 0.9);
    }

    @Test
    void shouldPutAndBatchGetTraceSegments() {
        String traceId = "1-0a1b2c3d-4e5f60718293a4b5c6d7e8f9";
        String document = segment(traceId);

        xray.putTraceSegments(b -> b.traceSegmentDocuments(document));

        BatchGetTracesResponse batch = xray.batchGetTraces(b -> b.traceIds(traceId));
        assertThat(batch.traces()).singleElement().satisfies(trace -> {
            assertThat(trace.id()).isEqualTo(traceId);
            assertThat(trace.segments()).singleElement()
                    .satisfies(segment -> assertThat(segment.document()).isEqualTo(document));
        });
    }

    @Test
    void shouldReportTraceSummaries() {
        String traceId = "1-0a1b2c3d-0123456789abcdef01234567";
        xray.putTraceSegments(b -> b.traceSegmentDocuments(segment(traceId)));

        assertThat(xray.getTraceSummaries(b -> b.startTime(NOW.minusSeconds(60)).endTime(NOW.plusSeconds(60)))
                .traceSummaries())
                .filteredOn(summary -> traceId.equals(summary.id()))
                .singleElement()
                .extracting(TraceSummary::serviceIds)
                .satisfies(services -> assertThat(services).extracting(ServiceId::name)
                        .contains("orders", "payments"));
    }

    @Test
    void shouldServeDefaultSamplingRule() {
        assertThat(xray.getSamplingRules(b -> { }).samplingRuleRecords())
                .extracting(SamplingRuleRecord::samplingRule)
                .anySatisfy(rule -> {
                    assertThat(rule.ruleName()).isEqualTo("Default");
                    assertThat(rule.priority()).isEqualTo(10000);
                });
    }
}
