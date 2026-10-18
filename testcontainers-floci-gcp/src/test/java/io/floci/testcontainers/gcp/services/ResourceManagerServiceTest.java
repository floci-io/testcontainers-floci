package io.floci.testcontainers.gcp.services;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.api.services.cloudresourcemanager.model.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceManagerServiceTest extends AbstractServiceTest {

    @Test
    void shouldGetProject() throws Exception {
        Project project = resourceManagerClient().projects().get(projectId()).execute();

        assertThat(project.getProjectId()).isEqualTo(projectId());
    }

    @Test
    void shouldGetIamPolicyOfProject() throws Exception {
        Policy policy = resourceManagerClient().projects().getIamPolicy(projectId(), new GetIamPolicyRequest())
                .execute();

        assertThat(policy.getEtag()).isNotBlank();
    }

    private static CloudResourceManager resourceManagerClient() {
        // Floci GCP serves Resource Manager v1, the Cloud Client Library speaks v3
        return new CloudResourceManager.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
                .setApplicationName("testcontainers-floci")
                .setRootUrl(floci.getEndpoint() + "/")
                .build();
    }
}
