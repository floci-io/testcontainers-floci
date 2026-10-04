package io.floci.testcontainers.gcp.services;

import io.floci.testcontainers.gcp.FlociGcpContainer;
import io.floci.testcontainers.gcp.config.services.IamConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IamServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateAndGetServiceAccount() {
        String accountId = uniqueName("sa");
        String email = accountId + "@" + projectId() + ".iam.gserviceaccount.com";
        String serviceAccounts = "/v1/projects/" + projectId() + "/serviceAccounts";

        RestResponse created = rest("POST", serviceAccounts, "application/json", """
                {"accountId": "%s", "serviceAccount": {"displayName": "Test SA"}}
                """.formatted(accountId));
        RestResponse fetched = rest("GET", serviceAccounts + "/" + email, null, null);

        assertThat(created.status()).as(created.body()).isEqualTo(200);
        assertThat(fetched.status()).as(fetched.body()).isEqualTo(200);
        assertThat(fetched.body()).contains("\"email\":\"" + email + "\"", "\"displayName\":\"Test SA\"");
    }

    @Test
    void shouldStartWithEnforcedAuthorization() {
        try (FlociGcpContainer enforcingFloci = new FlociGcpContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withIamConfig(c -> c.enabled(true)
                        .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
                        .bootstrapAdminMember("allAuthenticatedUsers"))) {
            enforcingFloci.start();

            assertThat(enforcingFloci.getLogs()).contains("IAM authorization mode=ENFORCE");
        }
    }
}
