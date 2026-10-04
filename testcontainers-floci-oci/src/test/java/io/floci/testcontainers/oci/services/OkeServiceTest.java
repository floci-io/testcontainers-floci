package io.floci.testcontainers.oci.services;

import com.oracle.bmc.containerengine.ContainerEngineClient;
import com.oracle.bmc.containerengine.model.Cluster;
import com.oracle.bmc.containerengine.model.ClusterLifecycleState;
import com.oracle.bmc.containerengine.model.ClusterSummary;
import com.oracle.bmc.containerengine.model.CreateClusterDetails;
import com.oracle.bmc.containerengine.requests.CreateClusterRequest;
import com.oracle.bmc.containerengine.requests.CreateKubeconfigRequest;
import com.oracle.bmc.containerengine.requests.DeleteClusterRequest;
import com.oracle.bmc.containerengine.requests.GetClusterRequest;
import com.oracle.bmc.containerengine.requests.ListClustersRequest;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class OkeServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateMockedClusterWithKubeconfig() throws Exception {
        try (FlociOciContainer mocked = new FlociOciContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withOkeConfig(c -> c.enabled(true).mock(true));
             ContainerEngineClient oke = startAndCreateClient(mocked)) {
            String clusterId = createCluster(oke, mocked.getTenancyId(), "mocked-cluster");

            Cluster cluster = getCluster(oke, clusterId);
            String kubeconfig;
            try (InputStream content = oke.createKubeconfig(CreateKubeconfigRequest.builder()
                    .clusterId(clusterId)
                    .build()).getInputStream()) {
                kubeconfig = new String(content.readAllBytes(), StandardCharsets.UTF_8);
            }

            assertThat(cluster.getLifecycleState()).isEqualTo(ClusterLifecycleState.Active);
            assertThat(kubeconfig).contains("clusters:");
        }
    }

    @Test
    void shouldRunClusterInK3sContainer() {
        try (ContainerEngineClient oke = client(ContainerEngineClient.builder())) {
            String clusterId = createCluster(oke, tenancyId(), "k3s-cluster-" + System.nanoTime());
            try {
                Cluster cluster = getCluster(oke, clusterId);
                // the k3s API server is published by the k3s container on the Docker host
                int apiServerPort = URI.create(cluster.getEndpoints().getKubernetes()).getPort();

                assertThat(cluster.getLifecycleState()).isEqualTo(ClusterLifecycleState.Active);
                assertThat(apiServerPort).isBetween(
                        floci.getOkeConfig().getApiServerBasePort(), floci.getOkeConfig().getApiServerMaxPort());
                await().atMost(Duration.ofSeconds(60))
                        .untilAsserted(() -> assertThat(isListening(floci.getHost(), apiServerPort)).isTrue());
            } finally {
                oke.deleteCluster(DeleteClusterRequest.builder().clusterId(clusterId).build());
            }
        }
    }

    private static ContainerEngineClient startAndCreateClient(FlociOciContainer container) {
        container.start();
        return client(container, ContainerEngineClient.builder());
    }

    /**
     * Cluster creation is driven by a work request without a response body, so the cluster is looked up by name.
     */
    private static String createCluster(ContainerEngineClient oke, String compartmentId, String name) {
        oke.createCluster(CreateClusterRequest.builder()
                .createClusterDetails(CreateClusterDetails.builder()
                        .compartmentId(compartmentId)
                        .name(name)
                        .vcnId("ocid1.vcn.oc1..testcontainers")
                        .kubernetesVersion("v1.30.1")
                        .build())
                .build());
        return oke.listClusters(ListClustersRequest.builder()
                        .compartmentId(compartmentId)
                        .name(name)
                        .build()).getItems().stream()
                .map(ClusterSummary::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Cluster " + name + " not found"));
    }

    private static Cluster getCluster(ContainerEngineClient oke, String clusterId) {
        return oke.getCluster(GetClusterRequest.builder().clusterId(clusterId).build()).getCluster();
    }

    private static boolean isListening(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
