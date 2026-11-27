package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceGraphServiceTest extends AbstractServiceTest {

    @Test
    void shouldQueryResourceGroups() {
        String resourceGroup = createResourceGroup();
        String resourceGroupName = resourceGroup.substring(resourceGroup.lastIndexOf('/') + 1);

        RestResponse response = rest("POST", "/providers/Microsoft.ResourceGraph/resources?api-version=2024-04-01", """
                {"query": "resourcecontainers | where type =~ 'microsoft.resources/subscriptions/resourcegroups' | where name == '%s' | project name, location",
                 "subscriptions": ["%s"]}
                """.formatted(resourceGroupName, SUBSCRIPTION_ID));

        assertThat(response.status()).as(response.toString()).isEqualTo(200);
        assertThat(response.json().path("totalRecords").asInt()).isEqualTo(1);
        assertThat(response.json().path("data").path(0).path("name").asText()).isEqualTo(resourceGroupName);
        assertThat(response.json().path("data").path(0).path("location").asText()).isEqualTo("eastus");
    }

    @Test
    void shouldRejectQueryOnUnknownTable() {
        RestResponse response = rest("POST", "/providers/Microsoft.ResourceGraph/resources?api-version=2024-04-01", """
                {"query": "nosuchtable | project name"}
                """);

        assertThat(response.status()).as(response.toString()).isEqualTo(400);
    }
}
