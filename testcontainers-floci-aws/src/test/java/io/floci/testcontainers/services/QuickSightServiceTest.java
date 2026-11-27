package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.ResourceNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuickSightServiceTest extends AbstractServiceTest {

    static QuickSightClient quickSight;

    @BeforeAll
    static void setUp() {
        quickSight = client(QuickSightClient.builder());
    }

    @Test
    void shouldCreateDescribeAndDeleteDashboard() {
        String account = "000000000000";
        String dashboardId = "dashboard-" + System.currentTimeMillis();

        String arn = quickSight.createDashboard(b -> b
                        .awsAccountId(account)
                        .dashboardId(dashboardId)
                        .name("Metadata dashboard")
                        .definition(d -> d.sheets(List.of())))
                .arn();

        assertThat(arn).endsWith(":dashboard/" + dashboardId);
        assertThat(quickSight.describeDashboard(b -> b.awsAccountId(account).dashboardId(dashboardId))
                .dashboard().name()).isEqualTo("Metadata dashboard");

        quickSight.deleteDashboard(b -> b.awsAccountId(account).dashboardId(dashboardId));

        assertThatThrownBy(() -> quickSight.describeDashboard(b -> b.awsAccountId(account).dashboardId(dashboardId)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
