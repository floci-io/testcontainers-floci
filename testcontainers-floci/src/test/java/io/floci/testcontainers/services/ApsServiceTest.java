package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.amp.AmpClient;
import software.amazon.awssdk.services.amp.model.WorkspaceStatusCode;
import software.amazon.awssdk.services.amp.model.WorkspaceSummary;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ApsServiceTest extends AbstractServiceTest {

    static AmpClient amp;

    static String workspaceId;

    @BeforeAll
    static void setUp() {
        amp = client(AmpClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateWorkspace() {
        var response = amp.createWorkspace(b -> b.alias("floci-tc"));

        assertThat(response.workspaceId()).startsWith("ws-");
        assertThat(response.arn()).contains(":workspace/ws-");
        workspaceId = response.workspaceId();
    }

    @Test
    @Order(2)
    void shouldDescribeWorkspace() {
        var response = amp.describeWorkspace(b -> b.workspaceId(workspaceId));

        assertThat(response.workspace().alias()).isEqualTo("floci-tc");
        assertThat(response.workspace().status().statusCode()).isEqualTo(WorkspaceStatusCode.ACTIVE);
    }

    @Test
    @Order(3)
    void shouldListWorkspacesContainsCreatedWorkspace() {
        var response = amp.listWorkspaces(b -> {});

        assertThat(response.workspaces())
                .extracting(WorkspaceSummary::workspaceId)
                .contains(workspaceId);
    }

    @Test
    @Order(4)
    void shouldDeleteWorkspace() {
        amp.deleteWorkspace(b -> b.workspaceId(workspaceId));

        var response = amp.listWorkspaces(b -> {});
        assertThat(response.workspaces())
                .extracting(WorkspaceSummary::workspaceId)
                .doesNotContain(workspaceId);
    }
}
