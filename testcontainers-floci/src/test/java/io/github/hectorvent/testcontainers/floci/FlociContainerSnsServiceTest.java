package io.github.hectorvent.testcontainers.floci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerSnsServiceTest extends AbstractFlociContainerServiceTest {

    static SnsClient sns;

    @BeforeAll
    static void setUp() {
        sns = client(SnsClient.builder());
    }

    @Test
    void shouldCreateAndListTopic() {
        String topicName = "test-topic-" + System.currentTimeMillis();

        String topicArn = sns.createTopic(b -> b.name(topicName)).topicArn();

        assertThat(topicArn).contains(topicName);

        List<String> topicArns = sns.listTopics().topics().stream()
                .map(Topic::topicArn)
                .toList();

        assertThat(topicArns).contains(topicArn);
    }

    @Test
    void shouldPublishMessage() {
        String topicArn = sns.createTopic(b -> b.name("test-publish-" + System.currentTimeMillis())).topicArn();

        String messageId = sns.publish(b -> b.topicArn(topicArn).message("Hello from Floci SNS!"))
                .messageId();

        assertThat(messageId).isNotBlank();
    }

}
