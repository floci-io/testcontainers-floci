package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.s3tables.S3TablesClient;
import software.amazon.awssdk.services.s3tables.model.NamespaceSummary;
import software.amazon.awssdk.services.s3tables.model.OpenTableFormat;
import software.amazon.awssdk.services.s3tables.model.TableBucketSummary;
import software.amazon.awssdk.services.s3tables.model.TableSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class S3TablesServiceTest extends AbstractServiceTest {

    static S3TablesClient s3Tables;

    private static final String BUCKET_NAME = "test-table-bucket-" + System.currentTimeMillis();
    private static final String NAMESPACE_NAME = "test_namespace";
    private static final String TABLE_NAME = "test_table";

    static String tableBucketArn;

    @BeforeAll
    static void setUp() {
        s3Tables = client(S3TablesClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateTableBucket() {
        var response = s3Tables.createTableBucket(b -> b.name(BUCKET_NAME));

        tableBucketArn = response.arn();
        assertThat(tableBucketArn).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetTableBucket() {
        var response = s3Tables.getTableBucket(b -> b.tableBucketARN(tableBucketArn));

        assertThat(response.name()).isEqualTo(BUCKET_NAME);
        assertThat(response.arn()).isEqualTo(tableBucketArn);
    }

    @Test
    @Order(3)
    void shouldListTableBucketsContainsCreatedBucket() {
        List<TableBucketSummary> buckets = s3Tables.listTableBuckets(b -> {}).tableBuckets();

        assertThat(buckets).anyMatch(bucket -> bucket.name().equals(BUCKET_NAME));
    }

    @Test
    @Order(4)
    void shouldCreateNamespace() {
        var response = s3Tables.createNamespace(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME));

        assertThat(response.namespace()).containsExactly(NAMESPACE_NAME);
    }

    @Test
    @Order(5)
    void shouldGetNamespace() {
        var response = s3Tables.getNamespace(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME));

        assertThat(response.namespace()).containsExactly(NAMESPACE_NAME);
    }

    @Test
    @Order(6)
    void shouldListNamespacesContainsCreatedNamespace() {
        List<NamespaceSummary> namespaces = s3Tables.listNamespaces(b -> b.tableBucketARN(tableBucketArn))
                .namespaces();

        assertThat(namespaces).anyMatch(ns -> ns.namespace().contains(NAMESPACE_NAME));
    }

    @Test
    @Order(7)
    void shouldCreateTable() {
        var response = s3Tables.createTable(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME)
                .name(TABLE_NAME)
                .format(OpenTableFormat.ICEBERG));

        assertThat(response.tableARN()).isNotBlank();
        assertThat(response.versionToken()).isNotBlank();
    }

    @Test
    @Order(8)
    void shouldGetTable() {
        var response = s3Tables.getTable(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME)
                .name(TABLE_NAME));

        assertThat(response.name()).isEqualTo(TABLE_NAME);
        assertThat(response.namespace()).containsExactly(NAMESPACE_NAME);
    }

    @Test
    @Order(9)
    void shouldListTablesContainsCreatedTable() {
        List<TableSummary> tables = s3Tables.listTables(b -> b.tableBucketARN(tableBucketArn)).tables();

        assertThat(tables).anyMatch(table -> table.name().equals(TABLE_NAME));
    }

    @Test
    @Order(10)
    void shouldDeleteTable() {
        s3Tables.deleteTable(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME)
                .name(TABLE_NAME));

        List<TableSummary> tables = s3Tables.listTables(b -> b.tableBucketARN(tableBucketArn)).tables();
        assertThat(tables).noneMatch(table -> table.name().equals(TABLE_NAME));
    }

    @Test
    @Order(11)
    void shouldDeleteNamespace() {
        s3Tables.deleteNamespace(b -> b
                .tableBucketARN(tableBucketArn)
                .namespace(NAMESPACE_NAME));

        List<NamespaceSummary> namespaces = s3Tables.listNamespaces(b -> b.tableBucketARN(tableBucketArn))
                .namespaces();
        assertThat(namespaces).noneMatch(ns -> ns.namespace().contains(NAMESPACE_NAME));
    }

    @Test
    @Order(12)
    void shouldDeleteTableBucket() {
        s3Tables.deleteTableBucket(b -> b.tableBucketARN(tableBucketArn));

        List<TableBucketSummary> buckets = s3Tables.listTableBuckets(b -> {}).tableBuckets();
        assertThat(buckets).noneMatch(bucket -> bucket.name().equals(BUCKET_NAME));
    }
}
