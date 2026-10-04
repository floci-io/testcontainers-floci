package io.floci.testcontainers.oci.services;

import com.oracle.bmc.queue.QueueAdminClient;
import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.model.CreateQueueDetails;
import com.oracle.bmc.queue.model.PutMessagesDetails;
import com.oracle.bmc.queue.model.PutMessagesDetailsEntry;
import com.oracle.bmc.queue.requests.CreateQueueRequest;
import com.oracle.bmc.queue.requests.GetMessagesRequest;
import com.oracle.bmc.queue.requests.GetWorkRequestRequest;
import com.oracle.bmc.queue.requests.PutMessagesRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueueServiceTest extends AbstractServiceTest {

    private static QueueAdminClient admin;
    private static QueueClient data;

    @BeforeAll
    static void createClients() {
        admin = client(QueueAdminClient.builder());
        // the data plane is served on the same endpoint (the queue's messagesEndpoint is Floci's own base URL)
        data = client(QueueClient.builder());
    }

    @AfterAll
    static void closeClients() {
        admin.close();
        data.close();
    }

    @Test
    void shouldPutAndGetMessages() {
        String queueId = createQueue("queue-" + System.nanoTime());

        data.putMessages(PutMessagesRequest.builder()
                .queueId(queueId)
                .putMessagesDetails(PutMessagesDetails.builder()
                        .messages(List.of(
                                PutMessagesDetailsEntry.builder().content("message one").build(),
                                PutMessagesDetailsEntry.builder().content("message two").build()))
                        .build())
                .build());
        var messages = data.getMessages(GetMessagesRequest.builder()
                .queueId(queueId)
                .limit(10)
                .timeoutInSeconds(0)
                .build()).getGetMessages().getMessages();

        assertThat(messages).extracting(m -> m.getContent()).containsExactly("message one", "message two");
    }

    /**
     * Queue creation is driven by a work request; the created queue's id is one of its resources.
     */
    private static String createQueue(String name) {
        var created = admin.createQueue(CreateQueueRequest.builder()
                .createQueueDetails(CreateQueueDetails.builder()
                        .displayName(name)
                        .compartmentId(tenancyId())
                        .build())
                .build());
        var workRequest = admin.getWorkRequest(GetWorkRequestRequest.builder()
                .workRequestId(created.getOpcWorkRequestId())
                .build()).getWorkRequest();
        return workRequest.getResources().get(0).getIdentifier();
    }
}
