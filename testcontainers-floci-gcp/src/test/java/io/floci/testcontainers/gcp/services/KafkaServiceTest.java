package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_BODY = """
            {
              "capacityConfig": {"vcpuCount": 3, "memoryBytes": 3221225472},
              "gcpConfig": {"accessConfig": {"networkConfigs": [
                {"subnet": "projects/test/regions/us-central1/subnetworks/default"}
              ]}}
            }
            """;

    private static final HttpClient REST_CLIENT = HttpClient.newHttpClient();

    @Test
    void shouldCreateClusterBackedByBroker() {
        String clusters = "/v1/projects/" + projectId() + "/locations/us-central1/clusters";
        String clusterId = uniqueName("cluster");

        try {
            HttpResponse<String> created = rest("POST", clusters + "?clusterId=" + clusterId, CLUSTER_BODY);
            HttpResponse<String> fetched = rest("GET", clusters + "/" + clusterId, null);

            assertThat(created.statusCode()).as(created.body()).isEqualTo(200);
            assertThat(fetched.body()).contains("\"state\":\"ACTIVE\"", "\"bootstrapAddress\"");
        } finally {
            rest("DELETE", clusters + "/" + clusterId, null);
        }
    }

    /**
     * Sends a REST request with an optional JSON body to the shared container. The google-cloud-managedkafka client
     * can't be used: Floci GCP returns long-running operations whose response lacks the "@type" the client needs to
     * parse them, and rejects the int64 fields the client sends as JSON strings.
     */
    private static HttpResponse<String> rest(String method, String path, String body) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(floci.getEndpoint() + path));
        if (body != null) {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }

        try {
            return REST_CLIENT.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("REST call " + method + " " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("REST call " + method + " " + path + " interrupted", e);
        }
    }
}
