package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.*;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class EmrServiceTest extends AbstractServiceTest {

    static EmrClient emr;
    static String clusterId;

    @BeforeAll
    static void setUp() {
        emr = client(EmrClient.builder());
    }

    @Test
    @Order(1)
    void shouldListClusters() {
        List<ClusterSummary> clusters = emr.listClusters().clusters();

        assertThat(clusters).isNotNull();
    }

    @Test
    @Order(2)
    void shouldRunJobFlow() {
        RunJobFlowResponse response = emr.runJobFlow(b -> b
                .name("test-emr-cluster-" + System.currentTimeMillis())
                .releaseLabel("emr-7.5.0")
                .serviceRole("arn:aws:iam::000000000000:role/EMR_DefaultRole")
                .jobFlowRole("arn:aws:iam::000000000000:instance-profile/EMR_EC2_DefaultRole")
                .instances(JobFlowInstancesConfig.builder()
                        .instanceCount(1)
                        .masterInstanceType("m5.xlarge")
                        .slaveInstanceType("m5.xlarge")
                        .keepJobFlowAliveWhenNoSteps(true)
                        .build()));

        clusterId = response.jobFlowId();
        assertThat(clusterId).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldDescribeCluster() {
        Cluster cluster = emr.describeCluster(b -> b.clusterId(clusterId)).cluster();

        assertThat(cluster.id()).isEqualTo(clusterId);
        assertThat(cluster.name()).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldWaitForClusterWaiting() {
        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    ClusterState state = emr.describeCluster(b -> b.clusterId(clusterId))
                            .cluster()
                            .status()
                            .state();
                    assertThat(state).isEqualTo(ClusterState.WAITING);
                });
    }

    @Test
    @Order(5)
    void shouldListClustersContainsCreatedCluster() {
        List<ClusterSummary> clusters = emr.listClusters().clusters();

        assertThat(clusters).anyMatch(c -> c.id().equals(clusterId));
    }

    @Test
    @Order(6)
    void shouldTerminateCluster() {
        emr.terminateJobFlows(b -> b.jobFlowIds(clusterId));

        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    ClusterState state = emr.describeCluster(b -> b.clusterId(clusterId))
                            .cluster()
                            .status()
                            .state();
                    assertThat(state).isEqualTo(ClusterState.TERMINATED);
                });
    }
}
