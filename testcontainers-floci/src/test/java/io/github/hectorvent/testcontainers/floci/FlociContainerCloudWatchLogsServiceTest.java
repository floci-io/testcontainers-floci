package io.github.hectorvent.testcontainers.floci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerCloudWatchLogsServiceTest extends AbstractFlociContainerServiceTest {

    static CloudWatchLogsClient logs;

    @BeforeAll
    static void setUp() {
        logs = client(CloudWatchLogsClient.builder());
    }

    @Test
    void shouldCreateAndDescribeLogGroup() {
        String logGroupName = "/test/logs-" + System.currentTimeMillis();

        logs.createLogGroup(b -> b.logGroupName(logGroupName));

        List<String> logGroupNames = logs.describeLogGroups(b -> b.logGroupNamePrefix(logGroupName))
                .logGroups().stream()
                .map(LogGroup::logGroupName)
                .toList();

        assertThat(logGroupNames).contains(logGroupName);
    }

    @Test
    void shouldCreateLogStreamAndPutEvents() {
        String logGroupName = "/test/stream-" + System.currentTimeMillis();
        String logStreamName = "test-stream";

        logs.createLogGroup(b -> b.logGroupName(logGroupName));
        logs.createLogStream(b -> b.logGroupName(logGroupName).logStreamName(logStreamName));

        logs.putLogEvents(b -> b
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .logEvents(e -> e
                        .timestamp(System.currentTimeMillis())
                        .message("Hello from Floci CloudWatch!")));

        var events = logs.getLogEvents(b -> b
                        .logGroupName(logGroupName)
                        .logStreamName(logStreamName))
                .events();

        assertThat(events).isNotEmpty();
        assertThat(events.get(0).message()).isEqualTo("Hello from Floci CloudWatch!");
    }

}
