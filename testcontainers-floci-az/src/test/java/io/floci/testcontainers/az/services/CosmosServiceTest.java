package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CosmosServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateDatabase() {
        String dbs = "/" + floci.getAccountName() + "-cosmos/dbs";

        RestResponse created = rest("POST", dbs, "{\"id\":\"db\"}");
        RestResponse fetched = rest("GET", dbs + "/db", null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("id").asText()).isEqualTo("db");
    }

    @Test
    void shouldNotStartDisabledApiEngine() {
        RestResponse response = rest("GET", "/" + floci.getAccountName() + "-cosmos-table/connect", null);

        assertThat(response.status()).as(response.toString()).isEqualTo(503);
    }

    @Test
    void shouldStartEnabledEmbeddedApiEngine() {
        try (FlociAzContainer engineFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withCosmosConfig(c -> c.enabled(true).table(api -> api.enabled(true)))) {
            engineFloci.start();

            RestResponse response = rest(engineFloci, "GET", "/" + engineFloci.getAccountName() + "-cosmos-table/connect", null);

            assertThat(response.status()).as(response.toString()).isEqualTo(200);
            assertThat(response.json().path("status").asText()).isEqualTo("running");
        }
    }
}
