package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.OrganizationsException;
import software.amazon.awssdk.services.securityhub.SecurityHubClient;
import software.amazon.awssdk.services.securityhub.model.AdminAccount;
import software.amazon.awssdk.services.securityhub.model.FindingAggregator;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class SecurityHubServiceTest extends AbstractServiceTest {

    static SecurityHubClient securityHub;
    static OrganizationsClient organizations;

    @BeforeAll
    static void setUp() {
        securityHub = client(SecurityHubClient.builder());
        organizations = client(OrganizationsClient.builder());

        // Organization administration requires the calling account to belong to an organization
        try {
            organizations.createOrganization(b -> b.featureSet("ALL"));
        } catch (OrganizationsException ignored) {
            // an organization already exists on the shared container — reuse it
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            organizations.deleteOrganization();
        } catch (OrganizationsException ignored) {
            // best-effort cleanup of the shared container
        }
    }

    @Test
    @Order(1)
    void shouldEnableOrganizationAdminAccount() {
        var response = securityHub.enableOrganizationAdminAccount(b -> b.adminAccountId(floci.getDefaultAccountId()));

        assertThat(response.adminAccountId()).isEqualTo(floci.getDefaultAccountId());
        assertThat(securityHub.listOrganizationAdminAccounts(b -> {}).adminAccounts())
                .extracting(AdminAccount::accountId)
                .contains(floci.getDefaultAccountId());
    }

    @Test
    @Order(2)
    void shouldCreateAndListFindingAggregator() {
        var response = securityHub.createFindingAggregator(b -> b.regionLinkingMode("ALL_REGIONS"));

        assertThat(response.findingAggregatorArn()).isNotBlank();
        assertThat(securityHub.listFindingAggregators(b -> {}).findingAggregators())
                .extracting(FindingAggregator::findingAggregatorArn)
                .contains(response.findingAggregatorArn());
    }
}
