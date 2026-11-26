package io.floci.testcontainers.gcp.services;

import com.google.cloud.billing.v1.BillingAccount;
import com.google.cloud.billing.v1.CloudBillingClient;
import com.google.cloud.billing.v1.CloudBillingSettings;
import com.google.cloud.billing.v1.ProjectBillingInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloudBillingServiceTest extends AbstractServiceTest {

    private static final String BILLING_ACCOUNT = "billingAccounts/000000-AAAAAA-BBBBBB";

    @Test
    void shouldLinkProjectToBillingAccount() throws Exception {
        String project = "projects/" + projectId();

        try (CloudBillingClient client = cloudBillingClient()) {
            client.updateProjectBillingInfo(project, ProjectBillingInfo.newBuilder()
                    .setBillingAccountName(BILLING_ACCOUNT)
                    .build());

            ProjectBillingInfo info = client.getProjectBillingInfo(project);
            assertThat(info.getProjectId()).isEqualTo(projectId());
            assertThat(info.getBillingAccountName()).isEqualTo(BILLING_ACCOUNT);
            assertThat(info.getBillingEnabled()).isTrue();

            assertThat(client.listProjectBillingInfo(BILLING_ACCOUNT).iterateAll())
                    .extracting(ProjectBillingInfo::getProjectId)
                    .contains(projectId());
        }
    }

    @Test
    void shouldGetBillingAccount() throws Exception {
        try (CloudBillingClient client = cloudBillingClient()) {
            BillingAccount account = client.getBillingAccount(BILLING_ACCOUNT);

            assertThat(account.getName()).isEqualTo(BILLING_ACCOUNT);
            assertThat(account.getOpen()).isTrue();
        }
    }

    private static CloudBillingClient cloudBillingClient() throws Exception {
        return CloudBillingClient.create(CloudBillingSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build());
    }
}
