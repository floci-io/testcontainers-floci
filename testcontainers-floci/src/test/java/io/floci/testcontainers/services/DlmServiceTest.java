package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.dlm.DlmClient;
import software.amazon.awssdk.services.dlm.model.IntervalUnitValues;
import software.amazon.awssdk.services.dlm.model.LifecyclePolicySummary;
import software.amazon.awssdk.services.dlm.model.PolicyTypeValues;
import software.amazon.awssdk.services.dlm.model.ResourceTypeValues;
import software.amazon.awssdk.services.dlm.model.SettablePolicyStateValues;
import software.amazon.awssdk.services.dlm.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class DlmServiceTest extends AbstractServiceTest {

    static DlmClient dlm;

    static String policyId;

    @BeforeAll
    static void setUp() {
        dlm = client(DlmClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateLifecyclePolicy() {
        policyId = dlm.createLifecyclePolicy(b -> b
                .executionRoleArn("arn:aws:iam::000000000000:role/dlm")
                .description("daily snapshots")
                .state(SettablePolicyStateValues.ENABLED)
                .policyDetails(d -> d
                        .policyType(PolicyTypeValues.EBS_SNAPSHOT_MANAGEMENT)
                        .resourceTypes(ResourceTypeValues.VOLUME)
                        .targetTags(Tag.builder().key("backup").value("true").build())
                        .schedules(s -> s
                                .name("daily")
                                .createRule(r -> r.interval(24).intervalUnit(IntervalUnitValues.HOURS))
                                .retainRule(r -> r.count(7)))))
                .policyId();

        assertThat(policyId).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetAndListLifecyclePolicy() {
        assertThat(dlm.getLifecyclePolicy(b -> b.policyId(policyId)).policy().description()).isEqualTo("daily snapshots");
        assertThat(dlm.getLifecyclePolicies(b -> {}).policies())
                .extracting(LifecyclePolicySummary::policyId)
                .contains(policyId);
    }

    @Test
    @Order(3)
    void shouldDeleteLifecyclePolicy() {
        dlm.deleteLifecyclePolicy(b -> b.policyId(policyId));

        assertThat(dlm.getLifecyclePolicies(b -> {}).policies())
                .extracting(LifecyclePolicySummary::policyId)
                .doesNotContain(policyId);
    }
}
