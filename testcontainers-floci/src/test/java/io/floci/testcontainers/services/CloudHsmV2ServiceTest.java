package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.cloudhsmv2.CloudHsmV2Client;
import software.amazon.awssdk.services.cloudhsmv2.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class CloudHsmV2ServiceTest extends AbstractServiceTest {

    static CloudHsmV2Client cloudHsmV2;

    static String clusterId;

    @BeforeAll
    static void setUp() {
        cloudHsmV2 = client(CloudHsmV2Client.builder());
    }

    @Test
    @Order(1)
    void shouldCreateCluster() {
        var response = cloudHsmV2.createCluster(b -> b
                .hsmType("hsm1.medium")
                .subnetIds("subnet-12345678"));

        assertThat(response.cluster().clusterId()).isNotBlank();
        clusterId = response.cluster().clusterId();
    }

    @Test
    @Order(2)
    void shouldDescribeClustersContainsCreatedCluster() {
        var response = cloudHsmV2.describeClusters(b -> {});

        assertThat(response.clusters())
                .anySatisfy(cluster -> assertThat(cluster.clusterId()).isEqualTo(clusterId));
    }

    @Test
    @Order(3)
    void shouldTagAndListTagsForCluster() {
        cloudHsmV2.tagResource(b -> b
                .resourceId(clusterId)
                .tagList(Tag.builder().key("env").value("test").build()));

        var response = cloudHsmV2.listTags(b -> b.resourceId(clusterId));

        assertThat(response.tagList()).contains(Tag.builder().key("env").value("test").build());
    }

    @Test
    @Order(4)
    void shouldDeleteCluster() {
        var response = cloudHsmV2.deleteCluster(b -> b.clusterId(clusterId));

        assertThat(response.cluster().clusterId()).isEqualTo(clusterId);
    }
}
