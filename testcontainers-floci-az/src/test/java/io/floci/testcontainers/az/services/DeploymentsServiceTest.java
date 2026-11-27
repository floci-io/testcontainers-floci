package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class DeploymentsServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2025-04-01";

    @Test
    void shouldDeployTemplateIntoResourceGroup() {
        String resourceGroup = createResourceGroup();
        String accountName = "deploy" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String deployment = resourceGroup + "/providers/Microsoft.Resources/deployments/storage";

        RestResponse created = rest("PUT", deployment + API_VERSION, """
                {"properties": {"mode": "Incremental", "template": {
                  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
                  "contentVersion": "1.0.0.0",
                  "parameters": {"accountName": {"type": "string"}},
                  "resources": [{
                    "type": "Microsoft.Storage/storageAccounts", "apiVersion": "2023-05-01",
                    "name": "[parameters('accountName')]", "location": "eastus",
                    "sku": {"name": "Standard_LRS"}, "kind": "StorageV2"}],
                  "outputs": {"accountId": {"type": "string",
                    "value": "[resourceId('Microsoft.Storage/storageAccounts', parameters('accountName'))]"}}},
                  "parameters": {"accountName": {"value": "%s"}}}}
                """.formatted(accountName));
        assertThat(created.isSuccessful()).as(created.toString()).isTrue();

        await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofMillis(500)).untilAsserted(() -> {
            RestResponse fetched = rest("GET", deployment + API_VERSION, null);
            assertThat(fetched.json().path("properties").path("provisioningState").asText())
                    .as(fetched.toString()).isEqualTo("Succeeded");
        });

        RestResponse fetched = rest("GET", deployment + API_VERSION, null);
        RestResponse account = rest("GET", resourceGroup + "/providers/Microsoft.Storage/storageAccounts/" + accountName
                + "?api-version=2023-05-01", null);

        assertThat(fetched.json().path("properties").path("outputs").path("accountId").path("value").asText())
                .endsWith("/providers/Microsoft.Storage/storageAccounts/" + accountName);
        assertThat(account.status()).as(account.toString()).isEqualTo(200);
        assertThat(account.json().path("name").asText()).isEqualTo(accountName);
    }
}
