package io.floci.testcontainers.oci.services;

import com.oracle.bmc.tenantmanagercontrolplane.LinkClient;
import com.oracle.bmc.tenantmanagercontrolplane.OrganizationClient;
import com.oracle.bmc.tenantmanagercontrolplane.WorkRequestClient;
import com.oracle.bmc.tenantmanagercontrolplane.model.CreateChildTenancyDetails;
import com.oracle.bmc.tenantmanagercontrolplane.model.LinkSummary;
import com.oracle.bmc.tenantmanagercontrolplane.model.OrganizationSummary;
import com.oracle.bmc.tenantmanagercontrolplane.model.OrganizationTenancySummary;
import com.oracle.bmc.tenantmanagercontrolplane.requests.CreateChildTenancyRequest;
import com.oracle.bmc.tenantmanagercontrolplane.requests.GetWorkRequestRequest;
import com.oracle.bmc.tenantmanagercontrolplane.requests.ListLinksRequest;
import com.oracle.bmc.tenantmanagercontrolplane.requests.ListOrganizationTenanciesRequest;
import com.oracle.bmc.tenantmanagercontrolplane.requests.ListOrganizationsRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationsServiceTest extends AbstractServiceTest {

    private static OrganizationClient organizations;
    private static LinkClient links;
    private static WorkRequestClient workRequests;

    @BeforeAll
    static void createClients() {
        organizations = client(OrganizationClient.builder());
        links = client(LinkClient.builder());
        workRequests = client(WorkRequestClient.builder());
    }

    @AfterAll
    static void closeClients() {
        organizations.close();
        links.close();
        workRequests.close();
    }

    @Test
    void shouldCreateChildTenancyInOrganization() {
        var created = organizations.createChildTenancy(CreateChildTenancyRequest.builder()
                .createChildTenancyDetails(CreateChildTenancyDetails.builder()
                        .compartmentId(tenancyId())
                        .tenancyName("child-" + System.nanoTime())
                        .homeRegion(floci.getRegion())
                        .adminEmail("admin@example.com")
                        .features(List.of("SUBSCRIPTION_SHARING"))
                        .build())
                .build());
        // child tenancy creation is driven by a work request; the child tenancy is one of its resources
        var workRequest = workRequests.getWorkRequest(GetWorkRequestRequest.builder()
                .workRequestId(created.getOpcWorkRequestId())
                .build()).getWorkRequest();
        assertThat(workRequest.getStatus().getValue()).isEqualTo("SUCCEEDED");
        String childTenancyId = workRequest.getResources().get(0).getIdentifier();

        List<OrganizationSummary> organizationList = organizations.listOrganizations(ListOrganizationsRequest.builder()
                .compartmentId(tenancyId())
                .build()).getOrganizationCollection().getItems();
        assertThat(organizationList).hasSize(1);

        var members = organizations.listOrganizationTenancies(ListOrganizationTenanciesRequest.builder()
                .organizationId(organizationList.get(0).getId())
                .build()).getOrganizationTenancyCollection().getItems();
        assertThat(members).extracting(OrganizationTenancySummary::getTenancyId)
                .contains(tenancyId(), childTenancyId);

        var childLinks = links.listLinks(ListLinksRequest.builder()
                .childTenancyId(childTenancyId)
                .build()).getLinkCollection().getItems();
        assertThat(childLinks).extracting(LinkSummary::getFeature)
                .containsExactlyInAnyOrder("CORE", "SUBSCRIPTION_SHARING");
    }
}
