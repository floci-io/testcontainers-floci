package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DbClusterNotFoundException;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(OrderAnnotation.class)
class DocumentDbServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_ID = "test-docdb-" + System.currentTimeMillis();
    private static final String MASTER_USER = "docdbadmin";
    private static final String MASTER_PASSWORD = "password123";

    static RdsClient rds;

    @BeforeAll
    static void setUp() {
        rds = client(RdsClient.builder());
    }

    @Test
    @Order(1)
    void shouldDescribeDbClusters() {
        assertThrows(DbClusterNotFoundException.class, () -> {
            rds.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID));
        });
    }

    @Test
    @Order(2)
    void shouldCreateDocumentDbCluster() {
        rds.createDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .engine("docdb")
                .masterUsername(MASTER_USER)
                .masterUserPassword(MASTER_PASSWORD));

        rds.waiter().waitUntilDBClusterAvailable(b -> b.dbClusterIdentifier(CLUSTER_ID),
                c -> c.waitTimeout(Duration.ofSeconds(30)));

        DBCluster cluster = rds.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID)).dbClusters().get(0);
        assertThat(cluster.engine()).isEqualTo("docdb");
        assertThat(cluster.dbClusterArn()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldListDocumentDbClusters() {
        List<DBCluster> clusters = rds.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID)).dbClusters();

        assertThat(clusters).isNotEmpty();
        assertThat(clusters).anyMatch(c -> c.dbClusterIdentifier().equals(CLUSTER_ID));
    }

    @Test
    @Order(4)
    void shouldDeleteDocumentDbCluster() {
        rds.deleteDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .skipFinalSnapshot(true));

        assertThrows(DbClusterNotFoundException.class, () -> {
            rds.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID));
        });
    }
}
