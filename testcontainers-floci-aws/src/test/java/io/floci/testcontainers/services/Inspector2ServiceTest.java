package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.DelegatedAdminAccount;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.OrganizationsException;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class Inspector2ServiceTest extends AbstractServiceTest {

    static Inspector2Client inspector;
    static OrganizationsClient organizations;

    @BeforeAll
    static void setUp() {
        inspector = client(Inspector2Client.builder());
        organizations = client(OrganizationsClient.builder());

        // Delegated administration requires the calling account to belong to an organization
        try {
            organizations.createOrganization(b -> b.featureSet("ALL"));
        } catch (OrganizationsException ignored) {
            // an organization already exists on the shared container — reuse it
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            inspector.disableDelegatedAdminAccount(b -> b.delegatedAdminAccountId(floci.getDefaultAccountId()));
            organizations.deleteOrganization();
        } catch (RuntimeException ignored) {
            // best-effort cleanup of the shared container
        }
    }

    @Test
    @Order(1)
    void shouldEnableDelegatedAdminAccount() {
        var response = inspector.enableDelegatedAdminAccount(b -> b.delegatedAdminAccountId(floci.getDefaultAccountId()));

        assertThat(response.delegatedAdminAccountId()).isEqualTo(floci.getDefaultAccountId());
        assertThat(inspector.listDelegatedAdminAccounts(b -> {}).delegatedAdminAccounts())
                .extracting(DelegatedAdminAccount::accountId)
                .contains(floci.getDefaultAccountId());
    }

    @Test
    @Order(2)
    void shouldUpdateOrganizationConfiguration() {
        inspector.updateOrganizationConfiguration(b -> b.autoEnable(a -> a.ec2(true).ecr(false)));

        var autoEnable = inspector.describeOrganizationConfiguration(b -> {}).autoEnable();
        assertThat(autoEnable.ec2()).isTrue();
        assertThat(autoEnable.ecr()).isFalse();
    }
}
