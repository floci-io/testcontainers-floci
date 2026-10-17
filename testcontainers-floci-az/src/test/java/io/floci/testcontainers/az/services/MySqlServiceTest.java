package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MySqlServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2023-12-30";

    @Test
    void shouldCreateMockedServer() {
        // A non-mocked server starts a MySQL container; the mocked one is ready without Docker
        try (FlociAzContainer mockedFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withMySqlConfig(c -> c.enabled(true).mocked(true))) {
            mockedFloci.start();
            String server = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg/providers/Microsoft.DBforMySQL/flexibleServers/server";

            RestResponse created = rest(mockedFloci, "PUT", server + API_VERSION, """
                    {"location": "eastus", "properties": {"administratorLogin": "admin", "administratorLoginPassword": "Str0ng_Passw0rd!"}}
                    """);
            RestResponse fetched = rest(mockedFloci, "GET", server + API_VERSION, null);

            assertThat(created.isSuccessful()).as(created.toString()).isTrue();
            assertThat(fetched.json().path("properties").path("administratorLogin").asText()).isEqualTo("admin");
            assertThat(fetched.json().path("properties").path("state").asText()).isEqualTo("Ready");
        }
    }
}
