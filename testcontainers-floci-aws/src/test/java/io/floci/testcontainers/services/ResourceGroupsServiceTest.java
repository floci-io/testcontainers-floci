package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CapacityReservation;
import software.amazon.awssdk.services.resourcegroups.ResourceGroupsClient;
import software.amazon.awssdk.services.resourcegroups.model.GroupConfigurationItem;
import software.amazon.awssdk.services.resourcegroups.model.GroupConfigurationParameter;
import software.amazon.awssdk.services.resourcegroups.model.GroupResourcesResponse;
import software.amazon.awssdk.services.resourcegroups.model.NotFoundException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Resource Groups with an EC2 capacity reservation pool, the group type Floci verifies membership for.
 */
@TestMethodOrder(OrderAnnotation.class)
class ResourceGroupsServiceTest extends AbstractServiceTest {

    static ResourceGroupsClient groups;
    static Ec2Client ec2;

    static String groupName;
    static String groupArn;
    static CapacityReservation reservation;

    @BeforeAll
    static void setUp() {
        groups = client(ResourceGroupsClient.builder());
        ec2 = client(Ec2Client.builder());
        reservation = ec2.createCapacityReservation(b -> b
                        .instanceType("t3.micro")
                        .instancePlatform("Linux/UNIX")
                        .availabilityZone(floci.getRegion() + "a")
                        .instanceCount(1))
                .capacityReservation();
    }

    @AfterAll
    static void tearDown() {
        ec2.cancelCapacityReservation(b -> b.capacityReservationId(reservation.capacityReservationId()));
    }

    @Test
    @Order(1)
    void shouldCreateGroup() {
        groupName = "capacity-pool-" + System.currentTimeMillis();
        groupArn = groups.createGroup(b -> b
                        .name(groupName)
                        .tags(Map.of("purpose", "test"))
                        .configuration(List.of(
                                GroupConfigurationItem.builder().type("AWS::EC2::CapacityReservationPool").build(),
                                GroupConfigurationItem.builder()
                                        .type("AWS::ResourceGroups::Generic")
                                        .parameters(GroupConfigurationParameter.builder()
                                                .name("allowed-resource-types")
                                                .values("AWS::EC2::CapacityReservation")
                                                .build())
                                        .build())))
                .group().groupArn();

        assertThat(groups.getGroup(b -> b.groupName(groupName)).group().groupArn()).isEqualTo(groupArn);
    }

    @Test
    @Order(2)
    void shouldGroupAndListResources() {
        GroupResourcesResponse added = groups.groupResources(b -> b
                .group(groupArn)
                .resourceArns(reservation.capacityReservationArn()));

        assertThat(added.succeeded()).containsExactly(reservation.capacityReservationArn());
        assertThat(added.failed()).isEmpty();
        assertThat(groups.listGroupResources(b -> b.group(groupArn)).resourceIdentifiers())
                .singleElement()
                .satisfies(identifier -> {
                    assertThat(identifier.resourceArn()).isEqualTo(reservation.capacityReservationArn());
                    assertThat(identifier.resourceType()).isEqualTo("AWS::EC2::CapacityReservation");
                });
    }

    @Test
    @Order(3)
    void shouldDeleteGroup() {
        groups.deleteGroup(b -> b.groupName(groupName));

        assertThatThrownBy(() -> groups.getGroup(b -> b.groupName(groupName)))
                .isInstanceOf(NotFoundException.class);
    }
}
