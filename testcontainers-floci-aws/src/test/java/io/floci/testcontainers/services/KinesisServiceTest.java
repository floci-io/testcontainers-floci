package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kinesis.KinesisClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KinesisServiceTest extends AbstractServiceTest {

    static KinesisClient kinesis;

    @BeforeAll
    static void setUp() {
        kinesis = client(KinesisClient.builder());
    }

    @Test
    void shouldCreateAndListStream() {
        String streamName = "test-stream-" + System.currentTimeMillis();

        kinesis.createStream(b -> b.streamName(streamName).shardCount(1));

        List<String> streamNames = kinesis.listStreams().streamNames();

        assertThat(streamNames).contains(streamName);
    }

    @Test
    void shouldDescribeStream() {
        String streamName = "test-describe-" + System.currentTimeMillis();
        kinesis.createStream(b -> b.streamName(streamName).shardCount(1));

        String streamArn = kinesis.describeStream(b -> b.streamName(streamName))
                .streamDescription()
                .streamARN();

        assertThat(streamArn).contains(streamName);
    }

}
