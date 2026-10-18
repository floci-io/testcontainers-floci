package io.floci.testcontainers.gcp.services;

import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LocationsServiceTest extends AbstractServiceTest {

    private static final HttpClient REST_CLIENT = HttpClient.newHttpClient();

    @Test
    void shouldListCatalogLocations() {
        HttpResponse<String> response = get(floci, "/v1/projects/" + projectId() + "/locations");

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        assertThat(response.body()).contains("\"locationId\":\"us-central1\"", "\"locationId\":\"europe-west3\"");
    }

    @Test
    void shouldAcceptUnknownLocationByDefault() {
        HttpResponse<String> response = get(floci, "/v1/projects/" + projectId() + "/locations/mars-north1/clusters");

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    }

    @Test
    void shouldRejectUnknownLocationWhileStrict() {
        try (FlociGcpContainer strictFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withKafkaConfig(c -> c.enabled(true).mock(true))
                .withLocationsConfig(c -> c.strict(true))) {
            strictFloci.start();
            String locations = "/v1/projects/" + strictFloci.getProjectId() + "/locations/";

            HttpResponse<String> unknown = get(strictFloci, locations + "mars-north1/clusters");
            HttpResponse<String> known = get(strictFloci, locations + "us-central1/clusters");

            assertThat(unknown.statusCode()).as(unknown.body()).isEqualTo(400);
            assertThat(unknown.body()).contains("INVALID_ARGUMENT", "mars-north1");
            assertThat(known.statusCode()).as(known.body()).isEqualTo(200);
        }
    }

    /**
     * Sends a GET request to the given container. The locations checks span services, so they use plain REST
     * rather than the client library of one service.
     */
    private static HttpResponse<String> get(FlociGcpContainer container, String path) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(container.getEndpoint() + path)).GET().build();

        try {
            return REST_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("REST call GET " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("REST call GET " + path + " interrupted", e);
        }
    }
}
