package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonitorServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateLogAnalyticsWorkspace() {
        String workspace = createResourceGroup() + "/providers/Microsoft.OperationalInsights/workspaces/logs";

        RestResponse created = rest("PUT", workspace + "?api-version=2022-10-01", "{\"location\":\"eastus\"}");
        RestResponse fetched = rest("GET", workspace + "?api-version=2022-10-01", null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("name").asText()).isEqualTo("logs");
        assertThat(fetched.json().path("type").asText()).isEqualTo("Microsoft.OperationalInsights/workspaces");
    }

    @Test
    void shouldRecordResourceGroupWriteInActivityLog() {
        String resourceGroup = createResourceGroup();
        String resourceGroupName = resourceGroup.substring(resourceGroup.lastIndexOf('/') + 1);

        RestResponse log = rest("GET", "/subscriptions/" + SUBSCRIPTION_ID
                + "/providers/Microsoft.Insights/eventtypes/management/values?api-version=2015-04-01"
                + "&$filter=eventTimestamp%20ge%20'2020-01-01T00:00:00Z'%20and%20resourceGroupName%20eq%20'"
                + resourceGroupName + "'", null);

        assertThat(log.status()).as(log.toString()).isEqualTo(200);
        assertThat(log.json().path("value").findValuesAsText("value"))
                .contains("Microsoft.Resources/subscriptions/resourceGroups/write");
    }
}
