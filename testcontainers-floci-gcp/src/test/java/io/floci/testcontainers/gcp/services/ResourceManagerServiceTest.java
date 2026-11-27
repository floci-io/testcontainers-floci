package io.floci.testcontainers.gcp.services;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.api.services.cloudresourcemanager.model.Project;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldOnlyKnowRegisteredProjectsWhileStrict() throws Exception {
        try (FlociGcpContainer strictFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withResourceManagerConfig(c -> c.enabled(true))
                .withProjectsConfig(c -> c.strict(true))) {
            strictFloci.start();
            CloudResourceManager client = resourceManagerClient(strictFloci);
            String createdProject = uniqueName("project");

            client.projects().create(new Project().setProjectId(createdProject).setName("Strict test")).execute();

            assertThat(client.projects().get(strictFloci.getProjectId()).execute().getProjectId())
                    .isEqualTo(strictFloci.getProjectId());
            assertThat(client.projects().get(createdProject).execute().getProjectId()).isEqualTo(createdProject);
            assertThatThrownBy(() -> client.projects().get(uniqueName("unknown")).execute())
                    .isInstanceOfSatisfying(GoogleJsonResponseException.class,
                            e -> assertThat(e.getStatusCode()).isEqualTo(403));
        }
    }

    private static CloudResourceManager resourceManagerClient() {
        return resourceManagerClient(floci);
    }

    private static CloudResourceManager resourceManagerClient(FlociGcpContainer container) {
        // Floci GCP serves Resource Manager v1, the Cloud Client Library speaks v3
        return new CloudResourceManager.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
                .setApplicationName("testcontainers-floci")
                .setRootUrl(container.getEndpoint() + "/")
                .build();
    }
}
