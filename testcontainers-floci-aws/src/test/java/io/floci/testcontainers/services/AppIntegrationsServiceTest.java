package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.appintegrations.AppIntegrationsClient;
import software.amazon.awssdk.services.appintegrations.model.EventIntegration;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AppIntegrationsServiceTest extends AbstractServiceTest {

    private static final String EVENT_INTEGRATION_NAME = "floci-tc-event-integration";

    static AppIntegrationsClient appIntegrations;

    @BeforeAll
    static void setUp() {
        appIntegrations = client(AppIntegrationsClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateEventIntegration() {
        var response = appIntegrations.createEventIntegration(b -> b
                .name(EVENT_INTEGRATION_NAME)
                .eventBridgeBus("default")
                .eventFilter(f -> f.source("aws.partner/example.com/1234")));

        assertThat(response.eventIntegrationArn()).contains(":event-integration/" + EVENT_INTEGRATION_NAME);
    }

    @Test
    @Order(2)
    void shouldGetAndListEventIntegration() {
        assertThat(appIntegrations.getEventIntegration(b -> b.name(EVENT_INTEGRATION_NAME)).eventFilter().source())
                .isEqualTo("aws.partner/example.com/1234");
        assertThat(appIntegrations.listEventIntegrations(b -> {}).eventIntegrations())
                .extracting(EventIntegration::name)
                .contains(EVENT_INTEGRATION_NAME);
    }

    @Test
    @Order(3)
    void shouldDeleteEventIntegration() {
        appIntegrations.deleteEventIntegration(b -> b.name(EVENT_INTEGRATION_NAME));

        assertThat(appIntegrations.listEventIntegrations(b -> {}).eventIntegrations())
                .extracting(EventIntegration::name)
                .doesNotContain(EVENT_INTEGRATION_NAME);
    }
}
