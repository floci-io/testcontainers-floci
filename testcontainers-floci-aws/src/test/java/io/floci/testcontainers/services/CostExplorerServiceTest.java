package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;

import static org.assertj.core.api.Assertions.assertThat;

class CostExplorerServiceTest extends AbstractServiceTest {

    static CostExplorerClient costExplorer;

    @BeforeAll
    static void setUp() {
        costExplorer = client(CostExplorerClient.builder());
    }

    @Test
    void shouldGetCostAndUsage() {
        GetCostAndUsageResponse response = costExplorer.getCostAndUsage(b -> b
                .timePeriod(DateInterval.builder()
                        .start("2025-01-01")
                        .end("2025-02-01")
                        .build())
                .granularity(Granularity.MONTHLY)
                .metrics("BlendedCost"));

        assertThat(response).isNotNull();
        assertThat(response.resultsByTime()).isNotNull();
    }

    @Test
    void shouldGetCostAndUsageWithGroupBy() {
        GetCostAndUsageResponse response = costExplorer.getCostAndUsage(b -> b
                .timePeriod(DateInterval.builder()
                        .start("2025-01-01")
                        .end("2025-03-01")
                        .build())
                .granularity(Granularity.MONTHLY)
                .metrics("UnblendedCost")
                .groupBy(g -> g.type("DIMENSION").key("SERVICE")));

        assertThat(response).isNotNull();
        assertThat(response.resultsByTime()).isNotNull();
    }
}
