package io.github.hectorvent.testcontainers.floci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerDynamoDbStreamsServiceTest extends AbstractFlociContainerServiceTest {

    static DynamoDbClient dynamoDb;
    static DynamoDbStreamsClient streams;

    @BeforeAll
    static void setUp() {
        dynamoDb = client(DynamoDbClient.builder());
        streams = client(DynamoDbStreamsClient.builder());
    }

    @Test
    void shouldListStreamsForTable() {
        String tableName = "test-streams-" + System.currentTimeMillis();

        dynamoDb.createTable(b -> b
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .streamSpecification(StreamSpecification.builder()
                        .streamEnabled(true)
                        .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                        .build()));

        List<String> tableNames = streams.listStreams(b -> b.tableName(tableName))
                .streams().stream()
                .map(software.amazon.awssdk.services.dynamodb.model.Stream::tableName)
                .toList();

        assertThat(tableNames).contains(tableName);
    }

    @Test
    void shouldReadStreamRecords() {
        String tableName = "test-stream-records-" + System.currentTimeMillis();

        dynamoDb.createTable(b -> b
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .streamSpecification(StreamSpecification.builder()
                        .streamEnabled(true)
                        .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                        .build()));

        // Write an item to generate a stream record
        dynamoDb.putItem(b -> b
                .tableName(tableName)
                .item(Map.of("id", AttributeValue.builder().s("key1").build())));

        // Get the stream ARN
        String streamArn = streams.listStreams(b -> b.tableName(tableName))
                .streams().get(0).streamArn();

        // Get shards
        List<Shard> shards = streams.describeStream(b -> b.streamArn(streamArn))
                .streamDescription().shards();

        assertThat(shards).isNotEmpty();

        // Read records from the first shard
        String shardIterator = streams.getShardIterator(b -> b
                        .streamArn(streamArn)
                        .shardId(shards.get(0).shardId())
                        .shardIteratorType(ShardIteratorType.TRIM_HORIZON))
                .shardIterator();

        var records = streams.getRecords(b -> b.shardIterator(shardIterator));

        assertThat(records.records()).isNotEmpty();
        assertThat(records.records().get(0).dynamodb().keys()).containsKey("id");
    }

}
