package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Uses a dedicated, mocked Event Hubs container: a non-mocked namespace starts an Artemis sidecar on fixed
 * host ports, while the mocked one only keeps the namespace state.
 */
class EventHubServiceTest extends AbstractServiceTest {

    private static FlociAzContainer eventHubFloci;

    @BeforeAll
    static void startContainer() {
        eventHubFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withEventHubConfig(c -> c.enabled(true).mocked(true).amqpPort(15672));
        eventHubFloci.start();
    }

    @AfterAll
    static void stopContainer() {
        eventHubFloci.stop();
    }

    @Test
    void shouldCreateMockedNamespace() {
        RestResponse response = rest(eventHubFloci, "PUT",
                "/" + eventHubFloci.getAccountName() + "-eventhub/namespaces/orders", null);

        assertThat(response.status()).as(response.toString()).isEqualTo(201);
        assertThat(response.json().path("name").asText()).isEqualTo("orders");
        assertThat(response.json().path("mocked").asBoolean()).isTrue();
    }

    @Test
    void shouldReportConfiguredAmqpPort() {
        RestResponse response = rest(eventHubFloci, "GET", "/" + eventHubFloci.getAccountName() + "-eventhub/health", null);

        assertThat(response.json().path("amqp").path("port").asInt()).isEqualTo(15672);
    }
}
