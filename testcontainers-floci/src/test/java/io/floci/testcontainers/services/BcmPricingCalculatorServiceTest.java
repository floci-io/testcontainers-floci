package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.bcmpricingcalculator.BcmPricingCalculatorClient;
import software.amazon.awssdk.services.bcmpricingcalculator.model.BatchCreateWorkloadEstimateUsageEntry;
import software.amazon.awssdk.services.bcmpricingcalculator.model.ResourceNotFoundException;
import software.amazon.awssdk.services.bcmpricingcalculator.model.WorkloadEstimateRateType;
import software.amazon.awssdk.services.bcmpricingcalculator.model.WorkloadEstimateStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class BcmPricingCalculatorServiceTest extends AbstractServiceTest {

    static BcmPricingCalculatorClient pricingCalculator;

    static String workloadEstimateId;

    @BeforeAll
    static void setUp() {
        pricingCalculator = client(BcmPricingCalculatorClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateWorkloadEstimate() {
        var response = pricingCalculator.createWorkloadEstimate(b -> b
                .name("floci-tc-estimate")
                .clientToken("floci-tc-estimate-token")
                .rateType(WorkloadEstimateRateType.BEFORE_DISCOUNTS));

        assertThat(response.id()).isNotBlank();
        workloadEstimateId = response.id();
    }

    @Test
    @Order(2)
    void shouldAddUsageAndCalculateTotalCost() {
        var batch = pricingCalculator.batchCreateWorkloadEstimateUsage(b -> b
                .workloadEstimateId(workloadEstimateId)
                .clientToken("floci-tc-usage-token")
                .usage(BatchCreateWorkloadEstimateUsageEntry.builder()
                        .serviceCode("AmazonEC2")
                        .usageType("BoxUsage:t3.micro")
                        .operation("")
                        .key("usage1")
                        .usageAccountId(floci.getDefaultAccountId())
                        .group("compute")
                        .amount(730d)
                        .build()));
        assertThat(batch.errors()).isEmpty();
        assertThat(batch.items()).hasSize(1);

        var estimate = pricingCalculator.getWorkloadEstimate(b -> b.identifier(workloadEstimateId));
        assertThat(estimate.status()).isEqualTo(WorkloadEstimateStatus.VALID);
        assertThat(estimate.totalCost()).isPositive();
    }

    @Test
    @Order(3)
    void shouldDeleteWorkloadEstimate() {
        pricingCalculator.deleteWorkloadEstimate(b -> b.identifier(workloadEstimateId));

        assertThatThrownBy(() -> pricingCalculator.getWorkloadEstimate(b -> b.identifier(workloadEstimateId)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
