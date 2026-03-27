package io.github.hectorvent.testcontainers.floci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerDynamoDbServiceTest extends AbstractFlociContainerServiceTest {

    static DynamoDbClient dynamoDb;

    @BeforeAll
    static void setUp() {
        dynamoDb = client(DynamoDbClient.builder());
    }

    @Test
    void shouldCreateAndListTable() {
        String tableName = "test-table-" + System.currentTimeMillis();

        dynamoDb.createTable(b -> b
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST));

        List<String> tableNames = dynamoDb.listTables().tableNames();

        assertThat(tableNames).contains(tableName);
    }

    @Test
    void shouldPutAndGetItem() {
        String tableName = "test-items-" + System.currentTimeMillis();

        dynamoDb.createTable(b -> b
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST));

        dynamoDb.putItem(b -> b
                .tableName(tableName)
                .item(Map.of(
                        "id", AttributeValue.builder().s("key1").build(),
                        "data", AttributeValue.builder().s("Hello from Floci DynamoDB!").build()
                )));

        GetItemResponse response = dynamoDb.getItem(b -> b
                .tableName(tableName)
                .key(Map.of("id", AttributeValue.builder().s("key1").build())));

        assertThat(response.item()).containsKey("data");
        assertThat(response.item().get("data").s()).isEqualTo("Hello from Floci DynamoDB!");
    }

}
