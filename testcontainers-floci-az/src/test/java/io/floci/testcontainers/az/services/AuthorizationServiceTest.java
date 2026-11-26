package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2022-04-01";

    /** GUID of Azure's built-in {@code Reader} role. */
    private static final String READER_ROLE_ID = "acdd72a7-3385-48ef-bd42-f606fba81ae7";

    @Test
    void shouldListBuiltInRoleDefinitionByName() {
        RestResponse roles = rest("GET", "/subscriptions/" + SUBSCRIPTION_ID
                + "/providers/Microsoft.Authorization/roleDefinitions" + API_VERSION
                + "&$filter=roleName%20eq%20'Reader'", null);

        assertThat(roles.status()).as(roles.toString()).isEqualTo(200);
        assertThat(roles.json().path("value")).hasSize(1);
        assertThat(roles.json().path("value").path(0).path("name").asText()).isEqualTo(READER_ROLE_ID);
        assertThat(roles.json().path("value").path(0).path("properties").path("type").asText()).isEqualTo("BuiltInRole");
    }

    @Test
    void shouldAssignRoleOnResourceGroup() {
        String resourceGroup = createResourceGroup();
        String principalId = UUID.randomUUID().toString();
        String roleDefinitionId = "/subscriptions/" + SUBSCRIPTION_ID + "/providers/Microsoft.Authorization/roleDefinitions/"
                + READER_ROLE_ID;
        String assignment = resourceGroup + "/providers/Microsoft.Authorization/roleAssignments/" + UUID.randomUUID();

        RestResponse created = rest("PUT", assignment + API_VERSION, """
                {"properties": {"roleDefinitionId": "%s", "principalId": "%s", "principalType": "ServicePrincipal"}}
                """.formatted(roleDefinitionId, principalId));
        RestResponse fetched = rest("GET", assignment + API_VERSION, null);

        assertThat(created.status()).as(created.toString()).isEqualTo(201);
        assertThat(fetched.status()).as(fetched.toString()).isEqualTo(200);
        assertThat(fetched.json().path("properties").path("scope").asText()).isEqualTo(resourceGroup);
        assertThat(fetched.json().path("properties").path("principalId").asText()).isEqualTo(principalId);
        assertThat(fetched.json().path("properties").path("roleDefinitionId").asText()).isEqualTo(roleDefinitionId);
    }
}
