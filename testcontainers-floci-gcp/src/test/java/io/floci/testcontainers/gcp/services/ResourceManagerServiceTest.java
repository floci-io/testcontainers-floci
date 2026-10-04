package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceManagerServiceTest extends AbstractServiceTest {

    @Test
    void shouldGetProject() {
        RestResponse response = rest("GET", "/v1/projects/" + projectId(), null, null);

        assertThat(response.status()).as(response.body()).isEqualTo(200);
        assertThat(response.body()).contains("\"projectId\":\"" + projectId() + "\"");
    }

    @Test
    void shouldGetIamPolicyOfProject() {
        RestResponse response = rest("POST", "/v1/projects/" + projectId() + ":getIamPolicy", "application/json", "{}");

        assertThat(response.status()).as(response.body()).isEqualTo(200);
        assertThat(response.body()).contains("\"etag\"");
    }
}
