package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AcrServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2023-07-01";

    @Test
    void shouldCreateMockedRegistry() {
        // A non-mocked registry starts a registry:2 container; the mocked one has a cosmetic login server
        try (FlociAzContainer mockedFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withAcrConfig(c -> c.enabled(true).mocked(true))) {
            mockedFloci.start();
            String registry = "/subscriptions/" + SUBSCRIPTION_ID
                    + "/resourceGroups/rg/providers/Microsoft.ContainerRegistry/registries/myregistry";

            RestResponse created = rest(mockedFloci, "PUT", registry + API_VERSION, """
                    {"location": "eastus", "sku": {"name": "Basic"}}
                    """);
            RestResponse fetched = rest(mockedFloci, "GET", registry + API_VERSION, null);

            assertThat(created.isSuccessful()).as(created.toString()).isTrue();
            assertThat(fetched.json().path("properties").path("provisioningState").asText()).isEqualTo("Succeeded");
            assertThat(fetched.json().path("properties").path("loginServer").asText()).isEqualTo("myregistry.azurecr.io");
        }
    }
}
