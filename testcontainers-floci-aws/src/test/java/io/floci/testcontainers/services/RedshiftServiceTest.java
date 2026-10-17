package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class RedshiftServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_ID = "floci-tc-redshift";
    private static final String MASTER_USER = "admin";
    private static final String MASTER_PASSWORD = "Password123";

    static RedshiftClient redshift;

    @BeforeAll
    static void setUp() {
        redshift = client(RedshiftClient.builder());
    }

    @Test
    @Order(1)
    void shouldDescribeClustersWhenNoneExist() {
        assertThat(redshift.describeClusters().clusters()).isEmpty();
    }

    @Test
    @Order(2)
    void shouldCreateCluster() {
        redshift.createCluster(b -> b
                .clusterIdentifier(CLUSTER_ID)
                .nodeType("dc2.large")
                .masterUsername(MASTER_USER)
                .masterUserPassword(MASTER_PASSWORD)
                .dbName("dev"));

        await().atMost(Duration.ofSeconds(120)).pollInterval(Duration.ofSeconds(3)).untilAsserted(() -> {
            Cluster cluster = redshift.describeClusters(b -> b.clusterIdentifier(CLUSTER_ID)).clusters().get(0);
            assertThat(cluster.clusterStatus()).isEqualTo("available");
        });
    }

    @Test
    @Order(3)
    void shouldExposeClusterEndpoint() {
        Cluster cluster = redshift.describeClusters(b -> b.clusterIdentifier(CLUSTER_ID)).clusters().get(0);

        assertThat(cluster.endpoint()).isNotNull();
        assertThat(cluster.endpoint().address()).isNotBlank();
        assertThat(cluster.endpoint().port()).isPositive();
    }

    @Test
    @Order(4)
    void shouldDeleteCluster() {
        redshift.deleteCluster(b -> b.clusterIdentifier(CLUSTER_ID).skipFinalClusterSnapshot(true));

        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(redshift.describeClusters().clusters())
                        .noneMatch(c -> CLUSTER_ID.equals(c.clusterIdentifier())));
    }
}
