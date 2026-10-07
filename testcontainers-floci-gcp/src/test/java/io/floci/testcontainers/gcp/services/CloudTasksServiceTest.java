package io.floci.testcontainers.gcp.services;

import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.CloudTasksSettings;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.HttpRequest;
import com.google.cloud.tasks.v2.LocationName;
import com.google.cloud.tasks.v2.Queue;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloudTasksServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateQueueAndTask() throws Exception {
        QueueName queueName = QueueName.of(projectId(), "us-central1", uniqueName("queue"));

        try (CloudTasksClient client = CloudTasksClient.create(CloudTasksSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.createQueue(LocationName.of(projectId(), "us-central1"),
                    Queue.newBuilder().setName(queueName.toString()).build());
            Task task = client.createTask(queueName, Task.newBuilder()
                    .setHttpRequest(HttpRequest.newBuilder()
                            .setHttpMethod(HttpMethod.POST)
                            .setUrl("http://localhost:1/unreachable"))
                    .build());

            assertThat(task.getName()).startsWith(queueName + "/tasks/");
            assertThat(client.getQueue(queueName).getName()).isEqualTo(queueName.toString());
        }
    }
}
