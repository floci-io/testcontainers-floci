package io.floci.testcontainers.gcp.services;

import com.google.api.serviceusage.v1.EnableServiceRequest;
import com.google.api.serviceusage.v1.GetServiceRequest;
import com.google.api.serviceusage.v1.ServiceUsageClient;
import com.google.api.serviceusage.v1.ServiceUsageSettings;
import com.google.api.serviceusage.v1.State;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceUsageServiceTest extends AbstractServiceTest {

    @Test
    void shouldEnableService() throws Exception {
        String service = "projects/" + projectId() + "/services/pubsub.googleapis.com";

        try (ServiceUsageClient client = ServiceUsageClient.create(ServiceUsageSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.enableServiceAsync(EnableServiceRequest.newBuilder().setName(service).build())
                    .get(30, TimeUnit.SECONDS);

            assertThat(client.getService(GetServiceRequest.newBuilder().setName(service).build()).getState())
                    .isEqualTo(State.ENABLED);
        }
    }
}
