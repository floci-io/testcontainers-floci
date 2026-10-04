package io.floci.testcontainers.gcp.services;

import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class GkeServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateMockedCluster() throws Exception {
        // A non-mocked cluster starts a privileged k3s container; the mocked one is control plane only
        try (FlociGcpContainer mockedFloci = new FlociGcpContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withGkeConfig(c -> c.enabled(true).mock(true))) {
            mockedFloci.start();
            String clusters = mockedFloci.getEndpoint() + "/container/v1/projects/" + mockedFloci.getProjectId()
                    + "/locations/us-central1/clusters";
            HttpClient http = HttpClient.newHttpClient();

            HttpResponse<String> created = http.send(HttpRequest.newBuilder(URI.create(clusters))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    "{\"cluster\": {\"name\": \"gke\", \"initialNodeCount\": 1}}"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> fetched = http.send(HttpRequest.newBuilder(URI.create(clusters + "/gke")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(created.statusCode()).as(created.body()).isEqualTo(200);
            assertThat(fetched.body()).contains("\"name\":\"gke\"", "\"status\":\"RUNNING\"");
        }
    }
}
