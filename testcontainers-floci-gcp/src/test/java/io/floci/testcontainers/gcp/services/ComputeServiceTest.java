package io.floci.testcontainers.gcp.services;

import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworksClient;
import com.google.cloud.compute.v1.NetworksSettings;
import com.google.cloud.compute.v1.RegionsClient;
import com.google.cloud.compute.v1.RegionsSettings;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ComputeServiceTest extends AbstractServiceTest {

    @Test
    void shouldListConfiguredRegions() throws Exception {
        try (RegionsClient client = RegionsClient.create(RegionsSettings.newBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            List<String> regions = new ArrayList<>();
            client.list(projectId()).iterateAll().forEach(region -> regions.add(region.getName()));

            assertThat(regions).contains("us-central1", "europe-west1");
        }
    }

    @Test
    void shouldInsertNetwork() throws Exception {
        String name = uniqueName("network");

        try (NetworksClient client = NetworksClient.create(NetworksSettings.newBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.insertAsync(projectId(), Network.newBuilder()
                            .setName(name)
                            .setAutoCreateSubnetworks(false)
                            .build())
                    .get(30, TimeUnit.SECONDS);

            assertThat(client.get(projectId(), name).getName()).isEqualTo(name);
        }
    }
}
