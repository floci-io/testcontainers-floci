package io.floci.testcontainers.gcp.services;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.ServiceAccount;
import io.floci.testcontainers.gcp.FlociGcpContainer;
import io.floci.testcontainers.gcp.config.services.IamConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IamServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateAndGetServiceAccount() throws Exception {
        Iam client = iamClient();
        String accountId = uniqueName("sa");
        String email = accountId + "@" + projectId() + ".iam.gserviceaccount.com";

        ServiceAccount created = client.projects().serviceAccounts().create("projects/" + projectId(),
                        new CreateServiceAccountRequest()
                                .setAccountId(accountId)
                                .setServiceAccount(new ServiceAccount().setDisplayName("Test SA")))
                .execute();
        ServiceAccount fetched = client.projects().serviceAccounts()
                .get("projects/" + projectId() + "/serviceAccounts/" + email)
                .execute();

        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(fetched.getEmail()).isEqualTo(email);
        assertThat(fetched.getDisplayName()).isEqualTo("Test SA");
    }

    @Test
    void shouldStartWithEnforcedAuthorization() {
        try (FlociGcpContainer enforcingFloci = new FlociGcpContainer(IMAGE)
                .disableAllServices()
                .withIamConfig(c -> c.enabled(true)
                        .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
                        .bootstrapAdminMember("allAuthenticatedUsers"))) {
            enforcingFloci.start();

            assertThat(enforcingFloci.getLogs()).contains("IAM authorization mode=ENFORCE");
        }
    }

    private static Iam iamClient() {
        // Floci GCP serves the IAM admin API over REST only, so the Cloud Client Library (gRPC) can't be used
        return new Iam.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
                .setApplicationName("testcontainers-floci")
                .setRootUrl(floci.getEndpoint() + "/")
                .build();
    }
}
