package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArmServiceTest extends AbstractServiceTest {

    @Test
    void shouldListDefaultSubscription() {
        RestResponse response = rest("GET", "/subscriptions?api-version=2022-12-01", null);

        assertThat(response.status()).as(response.toString()).isEqualTo(200);
        assertThat(response.json().path("value").path(0).path("subscriptionId").asText()).isEqualTo(floci.getSubscriptionId());
    }

    @Test
    void shouldListConfiguredSubscription() {
        try (FlociAzContainer armFloci = new FlociAzContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withArmConfig(c -> c.enabled(true).defaultSubscriptionId("22222222-2222-2222-2222-222222222222"))) {
            armFloci.start();

            RestResponse response = rest(armFloci, "GET", "/subscriptions?api-version=2022-12-01", null);

            assertThat(response.json().path("value").path(0).path("subscriptionId").asText())
                    .isEqualTo(armFloci.getSubscriptionId())
                    .isEqualTo("22222222-2222-2222-2222-222222222222");
        }
    }

    @Test
    void shouldListConfiguredTenantsAndSubscriptions() {
        String tenantId = "11111111-0000-0000-0000-000000000000";
        String subscriptionId = "33333333-0000-0000-0000-000000000000";
        try (FlociAzContainer armFloci = new FlociAzContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withArmConfig(c -> c.enabled(true))
                .withDirectoryConfig(c -> c
                        .tenant(tenantId, t -> t.displayName("Contoso").defaultDomain("contoso.example"))
                        .subscription(subscriptionId, s -> s.tenantId(tenantId).displayName("contoso-dev")))) {
            armFloci.start();

            RestResponse tenants = rest(armFloci, "GET", "/tenants?api-version=2022-12-01", null);
            RestResponse subscription = rest(armFloci, "GET", "/subscriptions/" + subscriptionId + "?api-version=2022-12-01", null);

            assertThat(tenants.status()).as(tenants.toString()).isEqualTo(200);
            assertThat(tenants.json().path("value").findValuesAsText("tenantId")).contains(tenantId);
            assertThat(tenants.json().path("value").findValuesAsText("displayName")).contains("Contoso");
            assertThat(subscription.status()).as(subscription.toString()).isEqualTo(200);
            assertThat(subscription.json().path("displayName").asText()).isEqualTo("contoso-dev");
            assertThat(subscription.json().path("tenantId").asText()).isEqualTo(tenantId);
        }
    }
}
