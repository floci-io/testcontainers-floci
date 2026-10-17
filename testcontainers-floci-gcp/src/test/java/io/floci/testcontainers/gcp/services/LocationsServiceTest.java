package io.floci.testcontainers.gcp.services;

import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LocationsServiceTest extends AbstractServiceTest {

    @Test
    void shouldListCatalogLocations() {
        RestResponse response = rest("GET", "/v1/projects/" + projectId() + "/locations", null, null);

        assertThat(response.status()).as(response.body()).isEqualTo(200);
        assertThat(response.body()).contains("\"locationId\":\"us-central1\"", "\"locationId\":\"europe-west3\"");
    }

    @Test
    void shouldAcceptUnknownLocationByDefault() {
        RestResponse response = rest("GET", "/v1/projects/" + projectId() + "/locations/mars-north1/clusters",
                null, null);

        assertThat(response.status()).as(response.body()).isEqualTo(200);
    }

    @Test
    void shouldRejectUnknownLocationWhileStrict() throws Exception {
        try (FlociGcpContainer strictFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withKafkaConfig(c -> c.enabled(true).mock(true))
                .withLocationsConfig(c -> c.strict(true))) {
            strictFloci.start();
            String locations = strictFloci.getEndpoint() + "/v1/projects/" + strictFloci.getProjectId() + "/locations/";
            HttpClient http = HttpClient.newHttpClient();

            HttpResponse<String> unknown = http.send(
                    HttpRequest.newBuilder(URI.create(locations + "mars-north1/clusters")).build(),
                    HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> known = http.send(
                    HttpRequest.newBuilder(URI.create(locations + "us-central1/clusters")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(unknown.statusCode()).as(unknown.body()).isEqualTo(400);
            assertThat(unknown.body()).contains("INVALID_ARGUMENT", "mars-north1");
            assertThat(known.statusCode()).as(known.body()).isEqualTo(200);
        }
    }
}
