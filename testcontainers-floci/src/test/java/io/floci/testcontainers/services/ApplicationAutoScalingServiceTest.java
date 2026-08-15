package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.applicationautoscaling.ApplicationAutoScalingClient;
import software.amazon.awssdk.services.applicationautoscaling.model.MetricType;
import software.amazon.awssdk.services.applicationautoscaling.model.PolicyType;
import software.amazon.awssdk.services.applicationautoscaling.model.ScalableDimension;
import software.amazon.awssdk.services.applicationautoscaling.model.ScalableTarget;
import software.amazon.awssdk.services.applicationautoscaling.model.ScalingPolicy;
import software.amazon.awssdk.services.applicationautoscaling.model.ServiceNamespace;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ApplicationAutoScalingServiceTest extends AbstractServiceTest {

    static ApplicationAutoScalingClient applicationAutoScaling;

    private static final String TABLE_NAME = "test-table-" + System.currentTimeMillis();
    private static final String RESOURCE_ID = "table/" + TABLE_NAME;
    private static final String POLICY_NAME = "test-policy-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        applicationAutoScaling = client(ApplicationAutoScalingClient.builder());
    }

    @Test
    @Order(1)
    void shouldRegisterScalableTarget() {
        var response = applicationAutoScaling.registerScalableTarget(b -> b
                .serviceNamespace(ServiceNamespace.DYNAMODB)
                .resourceId(RESOURCE_ID)
                .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS)
                .minCapacity(1)
                .maxCapacity(10)
                .roleARN("arn:aws:iam::000000000000:role/autoscaling-role"));

        assertThat(response.scalableTargetARN()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldDescribeScalableTargets() {
        List<ScalableTarget> targets = applicationAutoScaling.describeScalableTargets(b -> b
                        .serviceNamespace(ServiceNamespace.DYNAMODB)
                        .resourceIds(RESOURCE_ID)
                        .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS))
                .scalableTargets();

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).minCapacity()).isEqualTo(1);
        assertThat(targets.get(0).maxCapacity()).isEqualTo(10);
    }

    @Test
    @Order(3)
    void shouldPutScalingPolicy() {
        var response = applicationAutoScaling.putScalingPolicy(b -> b
                .policyName(POLICY_NAME)
                .policyType(PolicyType.TARGET_TRACKING_SCALING)
                .serviceNamespace(ServiceNamespace.DYNAMODB)
                .resourceId(RESOURCE_ID)
                .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS)
                .targetTrackingScalingPolicyConfiguration(t -> t
                        .targetValue(70.0)
                        .predefinedMetricSpecification(p -> p
                                .predefinedMetricType(MetricType.DYNAMO_DB_READ_CAPACITY_UTILIZATION))));

        assertThat(response.policyARN()).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldDescribeScalingPolicies() {
        List<ScalingPolicy> policies = applicationAutoScaling.describeScalingPolicies(b -> b
                        .serviceNamespace(ServiceNamespace.DYNAMODB)
                        .resourceId(RESOURCE_ID)
                        .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS)
                        .policyNames(POLICY_NAME))
                .scalingPolicies();

        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).policyName()).isEqualTo(POLICY_NAME);
        assertThat(policies.get(0).targetTrackingScalingPolicyConfiguration().targetValue()).isEqualTo(70.0);
    }

    @Test
    @Order(5)
    void shouldTagAndListTagsForResource() {
        String resourceArn = applicationAutoScaling.describeScalableTargets(b -> b
                        .serviceNamespace(ServiceNamespace.DYNAMODB)
                        .resourceIds(RESOURCE_ID)
                        .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS))
                .scalableTargets().get(0).scalableTargetARN();

        applicationAutoScaling.tagResource(b -> b
                .resourceARN(resourceArn)
                .tags(Map.of("env", "test")));

        Map<String, String> tags = applicationAutoScaling.listTagsForResource(b -> b.resourceARN(resourceArn)).tags();
        assertThat(tags).containsEntry("env", "test");

        applicationAutoScaling.untagResource(b -> b.resourceARN(resourceArn).tagKeys("env"));
        tags = applicationAutoScaling.listTagsForResource(b -> b.resourceARN(resourceArn)).tags();
        assertThat(tags).doesNotContainKey("env");
    }

    @Test
    @Order(6)
    void shouldDeleteScalingPolicy() {
        applicationAutoScaling.deleteScalingPolicy(b -> b
                .policyName(POLICY_NAME)
                .serviceNamespace(ServiceNamespace.DYNAMODB)
                .resourceId(RESOURCE_ID)
                .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS));

        List<ScalingPolicy> policies = applicationAutoScaling.describeScalingPolicies(b -> b
                        .serviceNamespace(ServiceNamespace.DYNAMODB)
                        .resourceId(RESOURCE_ID)
                        .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS))
                .scalingPolicies();
        assertThat(policies).isEmpty();
    }

    @Test
    @Order(7)
    void shouldDeregisterScalableTarget() {
        applicationAutoScaling.deregisterScalableTarget(b -> b
                .serviceNamespace(ServiceNamespace.DYNAMODB)
                .resourceId(RESOURCE_ID)
                .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS));

        List<ScalableTarget> targets = applicationAutoScaling.describeScalableTargets(b -> b
                        .serviceNamespace(ServiceNamespace.DYNAMODB)
                        .resourceIds(RESOURCE_ID)
                        .scalableDimension(ScalableDimension.DYNAMODB_TABLE_READ_CAPACITY_UNITS))
                .scalableTargets();
        assertThat(targets).isEmpty();
    }
}
