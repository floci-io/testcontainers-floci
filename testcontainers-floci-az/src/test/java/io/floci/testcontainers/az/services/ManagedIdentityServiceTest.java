package io.floci.testcontainers.az.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class ManagedIdentityServiceTest extends AbstractServiceTest {

    private static final String SCOPE = "subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg/providers/Microsoft.Web/sites/app";

    @Test
    void shouldIssueImdsTokenForConfiguredSystemAssignedScope() throws Exception {
        try (FlociAzContainer identityFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withArmConfig(c -> c.enabled(true))
                .withEntraConfig(c -> c.enabled(true))
                .withManagedIdentityConfig(c -> c.enabled(true).systemAssignedScope(SCOPE))) {
            identityFloci.start();

            HttpResponse<String> token = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(identityFloci.getEndpoint()
                            + "/metadata/identity/oauth2/token?api-version=2018-02-01&resource=https%3A%2F%2Fmanagement.azure.com%2F"))
                    .header("Metadata", "true")
                    .build(), HttpResponse.BodyHandlers.ofString());
            RestResponse identity = rest(identityFloci, "GET",
                    "/" + SCOPE + "/providers/Microsoft.ManagedIdentity/identities/default?api-version=2023-01-31", null);

            ObjectMapper mapper = new ObjectMapper();
            String accessToken = mapper.readTree(token.body()).path("access_token").asText();
            String principalId = mapper.readTree(Base64.getUrlDecoder().decode(accessToken.split("\\.")[1])).path("oid").asText();

            assertThat(token.statusCode()).as(token.body()).isEqualTo(200);
            assertThat(principalId).isEqualTo(identity.json().path("properties").path("principalId").asText());
        }
    }

    @Test
    void shouldIssueAppServiceTokenForConfiguredPathAndHeaderSecret() throws Exception {
        try (FlociAzContainer identityFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withEntraConfig(c -> c.enabled(true))
                .withManagedIdentityConfig(c -> c.enabled(true)
                        .appServicePath("identity/token")
                        .appServiceHeaderSecret("my-identity-header"))) {
            identityFloci.start();

            HttpResponse<String> token = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(identityFloci.getEndpoint()
                            + "/identity/token?api-version=2019-08-01&resource=https%3A%2F%2Fmanagement.azure.com%2F"))
                    .header("X-IDENTITY-HEADER", "my-identity-header")
                    .build(), HttpResponse.BodyHandlers.ofString());

            assertThat(token.statusCode()).as(token.body()).isEqualTo(200);
            assertThat(new ObjectMapper().readTree(token.body()).path("access_token").asText()).isNotEmpty();
        }
    }
}
