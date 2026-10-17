package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.detective.DetectiveClient;
import software.amazon.awssdk.services.detective.model.Account;
import software.amazon.awssdk.services.detective.model.Administrator;
import software.amazon.awssdk.services.detective.model.MemberDetail;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.OrganizationsException;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class DetectiveServiceTest extends AbstractServiceTest {

    private static final String MEMBER_ACCOUNT = "333333333333";

    static DetectiveClient detective;
    static OrganizationsClient organizations;

    static String graphArn;

    @BeforeAll
    static void setUp() {
        detective = client(DetectiveClient.builder());
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
        detective.enableOrganizationAdminAccount(b -> b.accountId(floci.getDefaultAccountId()));

        assertThat(detective.listOrganizationAdminAccounts(b -> {}).administrators())
                .extracting(Administrator::accountId)
                .contains(floci.getDefaultAccountId());
    }

    @Test
    @Order(2)
    void shouldListGraphs() {
        var graphs = detective.listGraphs(b -> {}).graphList();

        assertThat(graphs).isNotEmpty();
        graphArn = graphs.get(0).arn();
    }

    @Test
    @Order(3)
    void shouldCreateAndListMembers() {
        var response = detective.createMembers(b -> b
                .graphArn(graphArn)
                .accounts(Account.builder().accountId(MEMBER_ACCOUNT).emailAddress("member@example.com").build()));

        assertThat(response.members()).extracting(MemberDetail::accountId).containsExactly(MEMBER_ACCOUNT);
        assertThat(detective.listMembers(b -> b.graphArn(graphArn)).memberDetails())
                .extracting(MemberDetail::accountId)
                .contains(MEMBER_ACCOUNT);
    }
}
