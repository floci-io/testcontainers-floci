package io.floci.testcontainers.az.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class EntraServiceTest extends AbstractServiceTest {

    private static final String TENANT_ID = "11111111-1111-1111-1111-111111111111";

    private static FlociAzContainer entraFloci;

    @BeforeAll
    static void startContainer() {
        entraFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withEntraConfig(c -> c.enabled(true).defaultTenantId(TENANT_ID).tokenLifetimeSeconds(600));
        entraFloci.start();
    }

    @AfterAll
    static void stopContainer() {
        entraFloci.stop();
    }

    @Test
    void shouldIssueTokenForDefaultTenant() throws IOException {
        RestResponse response = restForm(entraFloci, "/organizations/oauth2/v2.0/token",
                "grant_type=client_credentials&client_id=my-app&client_secret=secret"
                        + "&scope=https%3A%2F%2Fmanagement.azure.com%2F.default");

        assertThat(response.status()).as(response.toString()).isEqualTo(200);
        assertThat(response.json().path("expires_in").asLong()).isEqualTo(600);
        assertThat(claims(response.json().path("access_token").asText()).path("tid").asText())
                .isEqualTo(entraFloci.getTenantId())
                .isEqualTo(TENANT_ID);
    }

    @Test
    void shouldServeDiscoveryDocument() {
        RestResponse response = rest(entraFloci, "GET", "/" + TENANT_ID + "/v2.0/.well-known/openid-configuration", null);

        assertThat(response.status()).as(response.toString()).isEqualTo(200);
        assertThat(response.json().path("token_endpoint").asText()).endsWith("/" + TENANT_ID + "/oauth2/v2.0/token");
    }

    @Test
    void shouldRejectUnregisteredClientWhenValidatingClientCredentials() throws IOException {
        try (FlociAzContainer validatingFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withEntraConfig(c -> c.enabled(true).validateClientCredentials(true))) {
            validatingFloci.start();

            RestResponse response = restForm(validatingFloci, "/organizations/oauth2/v2.0/token",
                    "grant_type=client_credentials&client_id=unregistered-app&client_secret=secret"
                            + "&scope=https%3A%2F%2Fmanagement.azure.com%2F.default");

            assertThat(response.status()).as(response.toString()).isEqualTo(400);
            assertThat(response.json().path("error").asText()).isEqualTo("unauthorized_client");
            assertThat(response.json().path("error_description").asText()).contains("AADSTS700016");
        }
    }

    private static JsonNode claims(String jwt) throws IOException {
        return new ObjectMapper().readTree(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]));
    }
}
