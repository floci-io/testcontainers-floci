package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ManagementGroupsServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2023-04-01";
    private static final String GROUPS = "/providers/Microsoft.Management/managementGroups/";

    @Test
    void shouldBuildManagementGroupHierarchy() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String parent = "platform-" + suffix;
        String child = "corp-" + suffix;

        RestResponse createdParent = rest("PUT", GROUPS + parent + API_VERSION, """
                {"properties": {"displayName": "Platform"}}
                """);
        RestResponse createdChild = rest("PUT", GROUPS + child + API_VERSION, """
                {"properties": {"displayName": "Corp", "details": {"parent": {"id": "%s"}}}}
                """.formatted(GROUPS + parent));
        RestResponse membership = rest("PUT", GROUPS + child + "/subscriptions/" + SUBSCRIPTION_ID + API_VERSION, null);
        RestResponse fetched = rest("GET", GROUPS + parent + API_VERSION + "&$expand=children&$recurse=true", null);

        assertThat(createdParent.isSuccessful()).as(createdParent.toString()).isTrue();
        assertThat(createdChild.isSuccessful()).as(createdChild.toString()).isTrue();
        assertThat(membership.isSuccessful()).as(membership.toString()).isTrue();
        assertThat(fetched.status()).as(fetched.toString()).isEqualTo(200);
        assertThat(fetched.json().path("properties").path("displayName").asText()).isEqualTo("Platform");
        assertThat(fetched.json().path("properties").path("children").findValuesAsText("name"))
                .contains(child, SUBSCRIPTION_ID);
    }
}
