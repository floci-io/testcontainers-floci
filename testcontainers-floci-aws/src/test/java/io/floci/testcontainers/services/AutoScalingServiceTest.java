package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutoScalingServiceTest extends AbstractServiceTest {

    static AutoScalingClient autoScaling;

    static final String LAUNCH_CONFIG_NAME = "test-lc-" + System.currentTimeMillis();
    static final String ASG_NAME = "test-asg-" + System.currentTimeMillis();
    static final String POLICY_NAME = "test-policy-" + System.currentTimeMillis();
    static final String HOOK_NAME = "test-hook-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        autoScaling = client(AutoScalingClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateLaunchConfiguration() {
        autoScaling.createLaunchConfiguration(b -> b
                .launchConfigurationName(LAUNCH_CONFIG_NAME)
                .imageId("ami-12345678")
                .instanceType("t3.micro"));

        List<LaunchConfiguration> configs = autoScaling.describeLaunchConfigurations(b -> b
                .launchConfigurationNames(LAUNCH_CONFIG_NAME))
                .launchConfigurations();

        assertThat(configs).hasSize(1);
        assertThat(configs.get(0).launchConfigurationName()).isEqualTo(LAUNCH_CONFIG_NAME);
        assertThat(configs.get(0).imageId()).isEqualTo("ami-12345678");
        assertThat(configs.get(0).instanceType()).isEqualTo("t3.micro");
    }

    @Test
    @Order(2)
    void shouldCreateAndDescribeAutoScalingGroup() {
        autoScaling.createAutoScalingGroup(b -> b
                .autoScalingGroupName(ASG_NAME)
                .launchConfigurationName(LAUNCH_CONFIG_NAME)
                .minSize(1)
                .maxSize(5)
                .desiredCapacity(1)
                .availabilityZones(floci.getDefaultAvailabilityZone()));

        List<AutoScalingGroup> groups = autoScaling.describeAutoScalingGroups(b -> b
                .autoScalingGroupNames(ASG_NAME))
                .autoScalingGroups();

        assertThat(groups).hasSize(1);
        AutoScalingGroup group = groups.get(0);
        assertThat(group.autoScalingGroupName()).isEqualTo(ASG_NAME);
        assertThat(group.launchConfigurationName()).isEqualTo(LAUNCH_CONFIG_NAME);
        assertThat(group.minSize()).isEqualTo(1);
        assertThat(group.maxSize()).isEqualTo(5);
        assertThat(group.desiredCapacity()).isEqualTo(1);
    }

    @Test
    @Order(3)
    void shouldUpdateAutoScalingGroup() {
        autoScaling.updateAutoScalingGroup(b -> b
                .autoScalingGroupName(ASG_NAME)
                .maxSize(10));

        AutoScalingGroup group = autoScaling.describeAutoScalingGroups(b -> b
                .autoScalingGroupNames(ASG_NAME))
                .autoScalingGroups().get(0);

        assertThat(group.maxSize()).isEqualTo(10);
    }

    @Test
    @Order(4)
    void shouldPutAndDescribeScalingPolicy() {
        autoScaling.putScalingPolicy(b -> b
                .autoScalingGroupName(ASG_NAME)
                .policyName(POLICY_NAME)
                .policyType("SimpleScaling")
                .adjustmentType("ChangeInCapacity")
                .scalingAdjustment(2)
                .cooldown(60));

        List<ScalingPolicy> policies = autoScaling.describePolicies(b -> b
                .autoScalingGroupName(ASG_NAME)
                .policyNames(POLICY_NAME))
                .scalingPolicies();

        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).policyName()).isEqualTo(POLICY_NAME);
        assertThat(policies.get(0).adjustmentType()).isEqualTo("ChangeInCapacity");
        assertThat(policies.get(0).scalingAdjustment()).isEqualTo(2);
    }

    @Test
    @Order(5)
    void shouldDeleteScalingPolicy() {
        autoScaling.deletePolicy(b -> b
                .autoScalingGroupName(ASG_NAME)
                .policyName(POLICY_NAME));

        List<ScalingPolicy> policies = autoScaling.describePolicies(b -> b
                .autoScalingGroupName(ASG_NAME)
                .policyNames(POLICY_NAME))
                .scalingPolicies();

        assertThat(policies).isEmpty();
    }

    // --- Lifecycle Hooks ---

    @Test
    @Order(6)
    void shouldPutAndDescribeLifecycleHook() {
        autoScaling.putLifecycleHook(b -> b
                .autoScalingGroupName(ASG_NAME)
                .lifecycleHookName(HOOK_NAME)
                .lifecycleTransition("autoscaling:EC2_INSTANCE_LAUNCHING")
                .defaultResult("CONTINUE")
                .heartbeatTimeout(300));

        List<LifecycleHook> hooks = autoScaling.describeLifecycleHooks(b -> b
                .autoScalingGroupName(ASG_NAME)
                .lifecycleHookNames(HOOK_NAME))
                .lifecycleHooks();

        assertThat(hooks).hasSize(1);
        assertThat(hooks.get(0).lifecycleHookName()).isEqualTo(HOOK_NAME);
        assertThat(hooks.get(0).lifecycleTransition()).isEqualTo("autoscaling:EC2_INSTANCE_LAUNCHING");
        assertThat(hooks.get(0).defaultResult()).isEqualTo("CONTINUE");
    }

    @Test
    @Order(7)
    void shouldDeleteLifecycleHook() {
        autoScaling.deleteLifecycleHook(b -> b
                .autoScalingGroupName(ASG_NAME)
                .lifecycleHookName(HOOK_NAME));

        List<LifecycleHook> hooks = autoScaling.describeLifecycleHooks(b -> b
                .autoScalingGroupName(ASG_NAME)
                .lifecycleHookNames(HOOK_NAME))
                .lifecycleHooks();

        assertThat(hooks).isEmpty();
    }

    // --- Activities ---

    @Test
    @Order(8)
    void shouldDescribeScalingActivities() {
        List<Activity> activities = autoScaling.describeScalingActivities(b -> b
                .autoScalingGroupName(ASG_NAME))
                .activities();

        assertThat(activities).isNotNull();
    }

    // --- Clean-up ---

    @Test
    @Order(9)
    void shouldDeleteAutoScalingGroup() {
        autoScaling.deleteAutoScalingGroup(b -> b
                .autoScalingGroupName(ASG_NAME)
                .forceDelete(true));

        List<AutoScalingGroup> groups = autoScaling.describeAutoScalingGroups(b -> b
                .autoScalingGroupNames(ASG_NAME))
                .autoScalingGroups();

        assertThat(groups).isEmpty();
    }

    @Test
    @Order(10)
    void shouldDeleteLaunchConfiguration() {
        autoScaling.deleteLaunchConfiguration(b -> b
                .launchConfigurationName(LAUNCH_CONFIG_NAME));

        List<LaunchConfiguration> configs = autoScaling.describeLaunchConfigurations(b -> b
                .launchConfigurationNames(LAUNCH_CONFIG_NAME))
                .launchConfigurations();

        assertThat(configs).isEmpty();
    }

    // --- Metadata ---

    @Test
    void shouldDescribeTerminationPolicyTypes() {
        List<String> types = autoScaling.describeTerminationPolicyTypes()
                .terminationPolicyTypes();

        assertThat(types).isNotEmpty();
    }

    @Test
    void shouldDescribeAdjustmentTypes() {
        List<AdjustmentType> types = autoScaling.describeAdjustmentTypes()
                .adjustmentTypes();

        assertThat(types).isNotEmpty();
    }

    @Test
    void shouldDescribeLifecycleHookTypes() {
        List<String> types = autoScaling.describeLifecycleHookTypes()
                .lifecycleHookTypes();

        assertThat(types).containsExactlyInAnyOrder(
                "autoscaling:EC2_INSTANCE_LAUNCHING",
                "autoscaling:EC2_INSTANCE_TERMINATING");
    }

    @Test
    void shouldDescribeAccountLimits() {
        DescribeAccountLimitsResponse limits = autoScaling.describeAccountLimits();

        assertThat(limits.maxNumberOfAutoScalingGroups()).isPositive();
        assertThat(limits.maxNumberOfLaunchConfigurations()).isPositive();
    }
}
