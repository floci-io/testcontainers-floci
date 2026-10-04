package io.floci.testcontainers.gcp.services;

import com.google.cloud.NoCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BigQueryServiceTest extends AbstractServiceTest {

    @Test
    void shouldQueryInsertedRows() throws Exception {
        BigQuery bigQuery = BigQueryOptions.newBuilder()
                .setHost(floci.getEndpoint())
                .setLocation("US")
                .setProjectId(projectId())
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
        String dataset = uniqueName("dataset").replace('-', '_');
        TableId table = TableId.of(dataset, "users");

        bigQuery.create(DatasetInfo.of(dataset));
        bigQuery.create(TableInfo.of(table, StandardTableDefinition.of(Schema.of(
                Field.of("name", StandardSQLTypeName.STRING),
                Field.of("age", StandardSQLTypeName.INT64)))));
        bigQuery.insertAll(InsertAllRequest.newBuilder(table)
                .addRow(Map.of("name", "Alice", "age", 30))
                .addRow(Map.of("name", "Bob", "age", 25))
                .build());

        // runs in the floci-duck sidecar container, which Floci GCP starts on the first query
        TableResult result = bigQuery.query(QueryJobConfiguration.of(
                "SELECT name FROM `" + projectId() + "." + dataset + ".users` WHERE age > 26"));

        assertThat(result.iterateAll()).singleElement()
                .satisfies(row -> assertThat(row.get("name").getStringValue()).isEqualTo("Alice"));
    }
}
