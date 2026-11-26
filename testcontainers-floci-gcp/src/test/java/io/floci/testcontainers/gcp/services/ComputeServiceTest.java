package io.floci.testcontainers.gcp.services;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.AttachedDiskInitializeParams;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesSettings;
import com.google.cloud.compute.v1.Items;
import com.google.cloud.compute.v1.Metadata;
import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworkInterface;
import com.google.cloud.compute.v1.NetworksClient;
import com.google.cloud.compute.v1.NetworksSettings;
import com.google.cloud.compute.v1.RegionsClient;
import com.google.cloud.compute.v1.RegionsSettings;
import com.google.cloud.compute.v1.Subnetwork;
import com.google.cloud.compute.v1.SubnetworksClient;
import com.google.cloud.compute.v1.SubnetworksSettings;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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

    @Test
    void shouldRunStartupScriptInInstanceContainer() throws Exception {
        String zone = "us-central1-a";
        String name = uniqueName("vm");

        try (FlociGcpContainer containerFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withComputeConfig(c -> c.enabled(true).mock(false).shutdownScriptTimeout(Duration.ofSeconds(5)))) {
            containerFloci.start();
            String project = containerFloci.getProjectId();

            try (NetworksClient networks = NetworksClient.create(NetworksSettings.newBuilder()
                         .setEndpoint(containerFloci.getEndpoint()).setCredentialsProvider(noCredentials()).build());
                 SubnetworksClient subnets = SubnetworksClient.create(SubnetworksSettings.newBuilder()
                         .setEndpoint(containerFloci.getEndpoint()).setCredentialsProvider(noCredentials()).build());
                 InstancesClient instances = InstancesClient.create(InstancesSettings.newBuilder()
                         .setEndpoint(containerFloci.getEndpoint()).setCredentialsProvider(noCredentials()).build())) {
                networks.insertAsync(project, Network.newBuilder()
                                .setName("net")
                                .setAutoCreateSubnetworks(false)
                                .build())
                        .get(30, TimeUnit.SECONDS);
                subnets.insertAsync(project, "us-central1", Subnetwork.newBuilder()
                                .setName("subnet")
                                .setNetwork("global/networks/net")
                                .setIpCidrRange("10.40.0.0/24")
                                .build())
                        .get(30, TimeUnit.SECONDS);

                // The operation only completes once the image is pulled and the instance container is up
                instances.insertAsync(project, zone, Instance.newBuilder()
                                .setName(name)
                                .setMachineType("zones/" + zone + "/machineTypes/e2-small")
                                .addNetworkInterfaces(NetworkInterface.newBuilder()
                                        .setSubnetwork("regions/us-central1/subnetworks/subnet"))
                                .addDisks(AttachedDisk.newBuilder()
                                        .setBoot(true)
                                        .setAutoDelete(true)
                                        .setInitializeParams(AttachedDiskInitializeParams.newBuilder()
                                                .setSourceImage("projects/debian-cloud/global/images/family/debian-12")))
                                .setMetadata(Metadata.newBuilder()
                                        .addItems(Items.newBuilder()
                                                .setKey("startup-script")
                                                .setValue("#!/bin/sh\necho \"hello from $(hostname)\"")))
                                .build())
                        .get(6, TimeUnit.MINUTES);

                try {
                    assertThat(instances.get(project, zone, name).getStatus()).isEqualTo("RUNNING");
                    await().atMost(Duration.ofMinutes(2)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() ->
                            assertThat(instances.getSerialPortOutput(project, zone, name).getContents())
                                    .contains("startup-script: hello from")
                                    .contains("startup-script exit status 0"));
                } finally {
                    instances.deleteAsync(project, zone, name).get(2, TimeUnit.MINUTES);
                }
            }
        }
    }
}
