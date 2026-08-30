package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.OrganizationalUnit;
import software.amazon.awssdk.services.organizations.model.OrganizationsException;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class OrganizationsServiceTest extends AbstractServiceTest {

    static OrganizationsClient organizations;

    static String organizationId;
    static String rootId;
    static String ouId;

    @BeforeAll
    static void setUp() {
        organizations = client(OrganizationsClient.builder());
    }

    @AfterAll
    static void tearDown() {
        try {
            if (ouId != null) {
                organizations.deleteOrganizationalUnit(b -> b.organizationalUnitId(ouId));
            }
            organizations.deleteOrganization();
        } catch (OrganizationsException ignored) {
            // best-effort cleanup of the shared container
        }
    }

    @Test
    @Order(1)
    void shouldCreateOrganization() {
        var response = organizations.createOrganization(b -> b.featureSet("ALL"));

        assertThat(response.organization().id()).matches("o-[a-z0-9]{10}");
        assertThat(response.organization().featureSetAsString()).isEqualTo("ALL");
        organizationId = response.organization().id();
    }

    @Test
    @Order(2)
    void shouldDescribeOrganization() {
        var response = organizations.describeOrganization();

        assertThat(response.organization().id()).isEqualTo(organizationId);
        assertThat(response.organization().masterAccountId()).isEqualTo("000000000000");
    }

    @Test
    @Order(3)
    void shouldListRoots() {
        var response = organizations.listRoots();

        assertThat(response.roots()).hasSize(1);
        assertThat(response.roots().get(0).name()).isEqualTo("Root");
        rootId = response.roots().get(0).id();
    }

    @Test
    @Order(4)
    void shouldCreateAndListOrganizationalUnit() {
        var created = organizations.createOrganizationalUnit(b -> b.parentId(rootId).name("Workloads"));
        ouId = created.organizationalUnit().id();
        assertThat(ouId).matches("ou-[a-z0-9]{4}-[a-z0-9]{8}");

        var response = organizations.listOrganizationalUnitsForParent(b -> b.parentId(rootId));
        assertThat(response.organizationalUnits())
                .extracting(OrganizationalUnit::id)
                .contains(ouId);
    }
}
