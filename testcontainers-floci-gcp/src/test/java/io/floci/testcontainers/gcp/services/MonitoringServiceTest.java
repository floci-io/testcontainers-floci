package io.floci.testcontainers.gcp.services;

import com.google.api.Metric;
import com.google.api.MetricDescriptor;
import com.google.api.MonitoredResource;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceSettings;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.Point;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.monitoring.v3.TypedValue;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MonitoringServiceTest extends AbstractServiceTest {

    @Test
    void shouldWriteAndListTimeSeries() throws Exception {
        String metricType = "custom.googleapis.com/" + uniqueName("metric");
        Instant now = Instant.now();

        try (MetricServiceClient client = MetricServiceClient.create(MetricServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.createTimeSeries(ProjectName.of(projectId()), List.of(TimeSeries.newBuilder()
                    .setMetric(Metric.newBuilder().setType(metricType))
                    .setResource(MonitoredResource.newBuilder().setType("global"))
                    .setMetricKind(MetricDescriptor.MetricKind.GAUGE)
                    .setValueType(MetricDescriptor.ValueType.DOUBLE)
                    .addPoints(Point.newBuilder()
                            .setInterval(TimeInterval.newBuilder().setEndTime(timestamp(now)))
                            .setValue(TypedValue.newBuilder().setDoubleValue(42.0)))
                    .build()));

            List<TimeSeries> timeSeries = new ArrayList<>();
            client.listTimeSeries(ListTimeSeriesRequest.newBuilder()
                            .setName(ProjectName.of(projectId()).toString())
                            .setFilter("metric.type = \"" + metricType + "\"")
                            .setInterval(TimeInterval.newBuilder()
                                    .setStartTime(timestamp(now.minusSeconds(300)))
                                    .setEndTime(timestamp(now.plusSeconds(300))))
                            .build())
                    .iterateAll().forEach(timeSeries::add);

            assertThat(timeSeries).singleElement()
                    .satisfies(series -> assertThat(series.getPoints(0).getValue().getDoubleValue()).isEqualTo(42.0));
        }
    }

    private static Timestamp timestamp(Instant instant) {
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }
}
