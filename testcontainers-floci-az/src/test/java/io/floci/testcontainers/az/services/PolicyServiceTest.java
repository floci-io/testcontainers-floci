package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2025-03-01";

    @Test
    void shouldAssignPolicyDefinitionToResourceGroup() {
        String resourceGroup = createResourceGroup();
        String definitionId = "/subscriptions/" + SUBSCRIPTION_ID + "/providers/Microsoft.Authorization/policyDefinitions/allowed-locations";
        String assignment = resourceGroup + "/providers/Microsoft.Authorization/policyAssignments/allowed-locations";

        RestResponse definition = rest("PUT", definitionId + API_VERSION, """
                {"properties": {"displayName": "Allowed locations", "mode": "All",
                 "policyRule": {"if": {"field": "location", "notIn": ["eastus"]}, "then": {"effect": "deny"}}}}
                """);
        RestResponse created = rest("PUT", assignment + API_VERSION, """
                {"properties": {"policyDefinitionId": "%s"}}
                """.formatted(definitionId));
        RestResponse fetched = rest("GET", assignment + API_VERSION, null);

        assertThat(definition.isSuccessful()).as(definition.toString()).isTrue();
        assertThat(definition.json().path("properties").path("policyType").asText()).isEqualTo("Custom");
        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("properties").path("scope").asText()).isEqualTo(resourceGroup);
        assertThat(fetched.json().path("properties").path("policyDefinitionId").asText()).isEqualTo(definitionId);
    }

    @Test
    void shouldDenyResourceWriteViolatingAssignedPolicy() {
        String resourceGroup = createResourceGroup();
        String definitionId = "/subscriptions/" + SUBSCRIPTION_ID + "/providers/Microsoft.Authorization/policyDefinitions/"
                + "eastus-only-" + resourceGroup.substring(resourceGroup.lastIndexOf('-') + 1);

        rest("PUT", definitionId + API_VERSION, """
                {"properties": {"displayName": "East US only", "mode": "All",
                 "policyRule": {"if": {"field": "location", "notIn": ["eastus"]}, "then": {"effect": "deny"}}}}
                """);
        RestResponse assigned = rest("PUT", resourceGroup + "/providers/Microsoft.Authorization/policyAssignments/eastus-only"
                + API_VERSION, """
                {"properties": {"policyDefinitionId": "%s"}}
                """.formatted(definitionId));
        RestResponse denied = rest("PUT", resourceGroup + "/providers/Microsoft.Storage/storageAccounts/policydenied"
                + "?api-version=2023-05-01", """
                {"location": "westeurope", "sku": {"name": "Standard_LRS"}, "kind": "StorageV2"}
                """);

        assertThat(assigned.isSuccessful()).as(assigned.toString()).isTrue();
        assertThat(denied.isSuccessful()).as(denied.toString()).isFalse();
        assertThat(denied.json().path("error").path("code").asText()).isEqualTo("RequestDisallowedByPolicy");
    }
}
