package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

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

    @Test
    void shouldCreateClusterBackedByBroker() {
        String clusters = "/v1/projects/" + projectId() + "/locations/us-central1/clusters";
        String clusterId = uniqueName("cluster");

        try {
            RestResponse created = rest("POST", clusters + "?clusterId=" + clusterId, "application/json", CLUSTER_BODY);
            RestResponse fetched = rest("GET", clusters + "/" + clusterId, null, null);

            assertThat(created.status()).as(created.body()).isEqualTo(200);
            assertThat(fetched.body()).contains("\"state\":\"ACTIVE\"", "\"bootstrapAddress\"");
        } finally {
            rest("DELETE", clusters + "/" + clusterId, null, null);
        }
    }
}
