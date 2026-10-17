package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AksServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-05-01";

    @Test
    void shouldCreateMockedCluster() {
        // A non-mocked cluster starts a k3s container; the mocked one gets a synthetic kubeconfig
        try (FlociAzContainer mockedFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withAksConfig(c -> c.enabled(true).mocked(true))) {
            mockedFloci.start();
            String cluster = "/subscriptions/" + SUBSCRIPTION_ID
                    + "/resourceGroups/rg/providers/Microsoft.ContainerService/managedClusters/aks";

            RestResponse created = rest(mockedFloci, "PUT", cluster + API_VERSION, """
                    {
                      "location": "eastus",
                      "properties": {
                        "dnsPrefix": "aks",
                        "agentPoolProfiles": [{"name": "default", "count": 1, "vmSize": "Standard_DS2_v2", "mode": "System"}]
                      }
                    }
                    """);
            RestResponse fetched = rest(mockedFloci, "GET", cluster + API_VERSION, null);

            assertThat(created.isSuccessful()).as(created.toString()).isTrue();
            assertThat(fetched.json().path("properties").path("provisioningState").asText()).isEqualTo("Succeeded");
        }
    }
}
