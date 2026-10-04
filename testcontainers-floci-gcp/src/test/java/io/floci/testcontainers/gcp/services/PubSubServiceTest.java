package io.floci.testcontainers.gcp.services;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.ReceivedMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PubSubServiceTest extends AbstractServiceTest {

    @Test
    void shouldPublishAndPullMessage() throws Exception {
        TopicName topic = TopicName.of(projectId(), uniqueName("topic"));
        SubscriptionName subscription = SubscriptionName.of(projectId(), uniqueName("subscription"));

        try (TopicAdminClient topics = TopicAdminClient.create(TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build());
             SubscriptionAdminClient subscriptions = SubscriptionAdminClient.create(SubscriptionAdminSettings.newBuilder()
                     .setTransportChannelProvider(grpcChannelProvider())
                     .setCredentialsProvider(noCredentials())
                     .build())) {
            topics.createTopic(topic);
            subscriptions.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 10);

            topics.publish(topic, List.of(PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8("hello")).build()));
            List<ReceivedMessage> messages = subscriptions.pull(subscription, 1).getReceivedMessagesList();

            assertThat(messages).singleElement()
                    .satisfies(message -> assertThat(message.getMessage().getData().toStringUtf8()).isEqualTo("hello"));
        }
    }
}
