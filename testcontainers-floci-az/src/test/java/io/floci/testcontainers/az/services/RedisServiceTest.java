package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedisServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-03-01";

    @Test
    void shouldCreateMockedCache() {
        // A non-mocked cache starts a Valkey container; the mocked one reports hostName=localhost
        try (FlociAzContainer mockedFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withRedisConfig(c -> c.enabled(true).mocked(true))) {
            mockedFloci.start();
            String cache = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg/providers/Microsoft.Cache/redis/cache";

            RestResponse created = rest(mockedFloci, "PUT", cache + API_VERSION, """
                    {"location": "eastus", "properties": {"sku": {"name": "Basic", "family": "C", "capacity": 0}}}
                    """);
            RestResponse fetched = rest(mockedFloci, "GET", cache + API_VERSION, null);

            assertThat(created.isSuccessful()).as(created.toString()).isTrue();
            assertThat(fetched.json().path("properties").path("provisioningState").asText()).isEqualTo("Succeeded");
            assertThat(fetched.json().path("properties").path("hostName").asText()).isEqualTo("localhost");
        }
    }
}
