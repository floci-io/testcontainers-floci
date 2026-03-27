package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class FlociContainerCloudWatchMetricsServiceTest extends AbstractFlociContainerServiceTest {

    static CloudWatchClient cloudWatch;

    @BeforeAll
    static void setUp() {
        cloudWatch = client(CloudWatchClient.builder());
    }

    @Test
    void shouldPutMetricData() {
        assertThatNoException().isThrownBy(() ->
                cloudWatch.putMetricData(b -> b
                        .namespace("TestNamespace")
                        .metricData(MetricDatum.builder()
                                .metricName("TestMetric")
                                .value(42.0)
                                .unit(StandardUnit.COUNT)
                                .timestamp(Instant.now())
                                .dimensions(Dimension.builder()
                                        .name("TestDim")
                                        .value("TestValue")
                                        .build())
                                .build())));
    }

    @Test
    void shouldListMetrics() {
        cloudWatch.putMetricData(b -> b
                .namespace("ListTest")
                .metricData(MetricDatum.builder()
                        .metricName("Metric1")
                        .value(1.0)
                        .unit(StandardUnit.COUNT)
                        .timestamp(Instant.now())
                        .build()));

        var metrics = cloudWatch.listMetrics(b -> b.namespace("ListTest")).metrics();

        assertThat(metrics).isNotEmpty();
        assertThat(metrics.get(0).metricName()).isEqualTo("Metric1");
    }

}
