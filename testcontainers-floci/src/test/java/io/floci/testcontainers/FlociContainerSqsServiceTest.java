package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerSqsServiceTest extends AbstractFlociContainerServiceTest {

    static SqsClient sqs;

    @BeforeAll
    static void setUp() {
        sqs = client(SqsClient.builder());
    }

    @Test
    void shouldCreateQueueAndListIt() {
        String queueName = "test-queue-" + System.currentTimeMillis();

        sqs.createQueue(b -> b.queueName(queueName));

        List<String> queueUrls = sqs.listQueues().queueUrls();

        assertThat(queueUrls).anyMatch(url -> url.contains(queueName));
    }

    @Test
    void shouldSendAndReceiveMessage() {
        String queueName = "test-msg-" + System.currentTimeMillis();
        String queueUrl = sqs.createQueue(b -> b.queueName(queueName)).queueUrl();

        String body = "Hello from Floci SQS!";
        sqs.sendMessage(b -> b.queueUrl(queueUrl).messageBody(body));

        List<Message> messages = sqs.receiveMessage(b -> b.queueUrl(queueUrl).maxNumberOfMessages(1))
                .messages();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).body()).isEqualTo(body);
    }

}
