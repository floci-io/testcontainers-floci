package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.Rule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerEventBridgeServiceTest extends AbstractFlociContainerServiceTest {

    static EventBridgeClient eventBridge;

    @BeforeAll
    static void setUp() {
        eventBridge = client(EventBridgeClient.builder());
    }

    @Test
    void shouldPutAndListRule() {
        String ruleName = "test-rule-" + System.currentTimeMillis();

        eventBridge.putRule(b -> b
                .name(ruleName)
                .eventPattern("{\"source\":[\"my.app\"]}"));

        List<String> ruleNames = eventBridge.listRules(b -> {}).rules().stream()
                .map(Rule::name)
                .toList();

        assertThat(ruleNames).contains(ruleName);
    }

    @Test
    void shouldPutEvents() {
        PutEventsResponse response = eventBridge.putEvents(b -> b
                .entries(PutEventsRequestEntry.builder()
                        .source("my.app")
                        .detailType("TestEvent")
                        .detail("{\"key\":\"value\"}")
                        .build()));

        assertThat(response.failedEntryCount()).isZero();
    }

}
