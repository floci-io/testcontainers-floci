package io.floci.testcontainers.gcp.services;

import com.google.api.serviceusage.v1.EnableServiceRequest;
import com.google.api.serviceusage.v1.GetServiceRequest;
import com.google.api.serviceusage.v1.ServiceUsageClient;
import com.google.api.serviceusage.v1.ServiceUsageSettings;
import com.google.api.serviceusage.v1.State;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceUsageServiceTest extends AbstractServiceTest {

    @Test
    void shouldEnableService() throws Exception {
        String service = "projects/" + projectId() + "/services/pubsub.googleapis.com";

        try (ServiceUsageClient client = serviceUsageClient(floci)) {
            client.enableServiceAsync(EnableServiceRequest.newBuilder().setName(service).build())
                    .get(30, TimeUnit.SECONDS);

            assertThat(client.getService(GetServiceRequest.newBuilder().setName(service).build()).getState())
                    .isEqualTo(State.ENABLED);
        }
    }

    @Test
    void shouldRejectDisabledServiceWhileEnforcementEnabled() throws Exception {
        try (FlociGcpContainer enforcingFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withServiceUsageConfig(c -> c.enabled(true).enforcementEnabled(true))
                .withSecretManagerConfig(c -> c.enabled(true))) {
            enforcingFloci.start();
            String project = enforcingFloci.getProjectId();
            HttpRequest listSecrets = HttpRequest.newBuilder(
                    URI.create(enforcingFloci.getEndpoint() + "/v1/projects/" + project + "/secrets")).build();
            HttpClient http = HttpClient.newHttpClient();

            HttpResponse<String> rejected = http.send(listSecrets, HttpResponse.BodyHandlers.ofString());

            try (ServiceUsageClient client = serviceUsageClient(enforcingFloci)) {
                client.enableServiceAsync(EnableServiceRequest.newBuilder()
                                .setName("projects/" + project + "/services/secretmanager.googleapis.com")
                                .build())
                        .get(30, TimeUnit.SECONDS);
            }
            HttpResponse<String> accepted = http.send(listSecrets, HttpResponse.BodyHandlers.ofString());

            assertThat(rejected.statusCode()).as(rejected.body()).isEqualTo(403);
            assertThat(rejected.body()).contains("SERVICE_DISABLED");
            assertThat(accepted.statusCode()).as(accepted.body()).isEqualTo(200);
        }
    }

    private static ServiceUsageClient serviceUsageClient(FlociGcpContainer container) throws Exception {
        return ServiceUsageClient.create(ServiceUsageSettings.newHttpJsonBuilder()
                .setEndpoint(container.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build());
    }
}
