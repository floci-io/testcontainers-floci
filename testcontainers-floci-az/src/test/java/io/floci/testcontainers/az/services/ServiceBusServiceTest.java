package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceBusServiceTest extends AbstractServiceTest {

    @Test
    void shouldApplyTopologyAtStartup() {
        try (FlociAzContainer topologyFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withServiceBusConfig(c -> c.enabled(true).topology("""
                        {"UserConfig": {"Namespaces": [{"Name": "default", "Queues": [{"Name": "orders"}], "Topics": []}]}}
                        """))) {
            topologyFloci.start();

            RestResponse queues = rest(topologyFloci, "GET", "/" + topologyFloci.getAccountName() + "-servicebus/default/queues", null);

            assertThat(queues.status()).as(queues.toString()).isEqualTo(200);
            assertThat(queues.body()).contains("orders");
        }
    }
}
