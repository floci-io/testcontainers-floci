package io.floci.testcontainers.gcp.services;

import com.google.cloud.eventarc.v1.CloudRun;
import com.google.cloud.eventarc.v1.CreateTriggerRequest;
import com.google.cloud.eventarc.v1.Destination;
import com.google.cloud.eventarc.v1.EventFilter;
import com.google.cloud.eventarc.v1.EventarcClient;
import com.google.cloud.eventarc.v1.EventarcSettings;
import com.google.cloud.eventarc.v1.LocationName;
import com.google.cloud.eventarc.v1.Trigger;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class EventarcServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateTrigger() throws Exception {
        String triggerId = uniqueName("trigger");

        try (EventarcClient client = EventarcClient.create(EventarcSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            Trigger created = client.createTriggerAsync(CreateTriggerRequest.newBuilder()
                            .setParent(LocationName.of(projectId(), "us-central1").toString())
                            .setTriggerId(triggerId)
                            .setTrigger(Trigger.newBuilder()
                                    .setDestination(Destination.newBuilder()
                                            .setCloudRun(CloudRun.newBuilder().setService("hello").setRegion("us-central1")))
                                    .addEventFilters(EventFilter.newBuilder()
                                            .setAttribute("type")
                                            .setValue("google.cloud.pubsub.topic.v1.messagePublished"))
                                    .setServiceAccount("test@" + projectId() + ".iam.gserviceaccount.com"))
                            .build())
                    .get(30, TimeUnit.SECONDS);

            assertThat(created.getName()).endsWith("/triggers/" + triggerId);
            assertThat(client.getTrigger(created.getName()).getDestination().getCloudRun().getService())
                    .isEqualTo("hello");
        }
    }
}
