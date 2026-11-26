package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.eventbridgev2.EventBridgeV2Client;
import software.amazon.awssdk.services.eventbridgev2.model.CreateEventBusResponse;
import software.amazon.awssdk.services.eventbridgev2.model.FilterScope;
import software.amazon.awssdk.services.eventbridgev2.model.InvalidInputException;
import software.amazon.awssdk.services.eventbridgev2.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class EventBridgeV2ServiceTest extends AbstractServiceTest {

    static EventBridgeV2Client events;
    static SqsClient sqs;

    @BeforeAll
    static void setUp() {
        events = client(EventBridgeV2Client.builder());
        sqs = client(SqsClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateEventBusAndRejectDuplicateName() {
        String name = "orders-" + System.currentTimeMillis();

        CreateEventBusResponse created = events.createEventBus(b -> b
                .name(name)
                .description("Order events")
                .tags(Map.of("team", "orders")));
        try {
            assertThat(created.eventBusArn()).contains(":event-busv2/" + name + "/");
            assertThat(created.name()).isEqualTo(name);
            assertThat(created.description()).isEqualTo("Order events");

            assertThatThrownBy(() -> events.createEventBus(b -> b.name(name)))
                    .isInstanceOf(ResourceAlreadyExistsException.class);
        } finally {
            events.deleteEventBus(b -> b.eventBusArn(created.eventBusArn()));
        }
    }

    @Test
    @Order(2)
    void shouldRejectInvalidEventBusName() {
        assertThatThrownBy(() -> events.createEventBus(b -> b.name("not a valid name")))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    @Order(3)
    void shouldDeliverFilteredEventsToSqs() {
        String name = "orders-sqs-" + System.currentTimeMillis();
        String queueUrl = sqs.createQueue(b -> b.queueName(name)).queueUrl();
        String queueArn = sqs.getQueueAttributes(b -> b.queueUrl(queueUrl)
                        .attributeNames(QueueAttributeName.QUEUE_ARN))
                .attributes().get(QueueAttributeName.QUEUE_ARN);
        String bus = events.createEventBus(b -> b.name(name)).eventBusArn();
        String subscriber = events.createSubscriber(b -> b
                        .name("large-orders")
                        .eventBusArn(bus)
                        .invokeConfiguration(i -> i
                                .targetArn(queueArn)
                                .roleArn("arn:aws:iam::000000000000:role/EventBusDelivery"))
                        .filterConfiguration(f -> f.filters(filter -> filter
                                .scope(FilterScope.DATA)
                                .pattern("{\"detail\":{\"total\":[{\"numeric\":[\">\",500]}]}}"))))
                .subscriberArn();
        try {
            events.putEvents(b -> b.eventBusArn(bus).entries(
                    e -> e.source("com.shop").detailType("Order Placed").detail("{\"total\":750}"),
                    e -> e.source("com.shop").detailType("Order Placed").detail("{\"total\":5}")));

            List<Message> messages = sqs.receiveMessage(b -> b
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(2)).messages();
            assertThat(messages).hasSize(1);
            assertThat(messages.get(0).body()).contains("\"total\":750");
        } finally {
            events.deleteSubscriber(b -> b.subscriberArn(subscriber));
            events.deleteEventBus(b -> b.eventBusArn(bus));
            sqs.deleteQueue(b -> b.queueUrl(queueUrl));
        }
    }
}
