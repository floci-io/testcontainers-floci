package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.neptune.NeptuneClient;
import software.amazon.awssdk.services.neptune.model.DBCluster;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class NeptuneServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_ID = "test-neptune-" + System.currentTimeMillis();

    static NeptuneClient neptune;
    static String clusterEndpoint;

    @BeforeAll
    static void setUp() {
        neptune = client(NeptuneClient.builder());
    }

    @Test
    @Order(1)
    void shouldDescribeDbClusters() {
        List<DBCluster> clusters = neptune.describeDBClusters(b -> {
        }).dbClusters();

        assertThat(clusters).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateAndDescribeDbCluster() {
        neptune.createDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .engine("neptune"));

        // Describe the specific cluster we just created
        DBCluster cluster = neptune.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID))
                .dbClusters()
                .get(0);

        assertThat(cluster.dbClusterIdentifier()).isEqualTo(CLUSTER_ID);
        assertThat(cluster.engine()).isEqualTo("neptune");

        // Store the endpoint for Gremlin client tests
        clusterEndpoint = cluster.endpoint();
        assertThat(clusterEndpoint).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldVerifyClusterStatus() {
        // Verify cluster status through AWS API
        DBCluster cluster = neptune.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID))
                .dbClusters()
                .get(0);

        assertThat(cluster.status()).isIn("available", "creating", "backing-up");
        assertThat(cluster.endpoint()).isNotNull();
        assertThat(cluster.port()).isEqualTo(8182);
    }

    @Test
    @Order(4)
    void shouldDeleteDbCluster() {
        neptune.deleteDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .skipFinalSnapshot(true));

        // Verify the cluster is deleted by checking list doesn't contain it
        List<String> clusterIds = neptune.describeDBClusters(b -> {
                }).dbClusters().stream()
                .map(DBCluster::dbClusterIdentifier)
                .toList();

        assertThat(clusterIds).doesNotContain(CLUSTER_ID);
    }
}
