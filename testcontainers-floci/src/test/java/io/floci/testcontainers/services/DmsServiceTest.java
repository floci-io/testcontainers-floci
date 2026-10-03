package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.databasemigration.DatabaseMigrationClient;
import software.amazon.awssdk.services.databasemigration.model.ResourceNotFoundException;
import software.amazon.awssdk.services.databasemigration.model.ReplicationSubnetGroup;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Subnet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class DmsServiceTest extends AbstractServiceTest {

    private static final String SUBNET_GROUP_ID = "floci-tc-subnet-group";

    static DatabaseMigrationClient dms;
    static Ec2Client ec2;

    @BeforeAll
    static void setUp() {
        dms = client(DatabaseMigrationClient.builder());
        ec2 = client(Ec2Client.builder());
    }

    @Test
    @Order(1)
    void shouldCreateReplicationSubnetGroup() {
        // A replication subnet group needs subnets of one VPC in at least two availability zones,
        // which the default VPC provides
        Map<String, Map<String, Subnet>> subnetsByVpcAndZone = ec2.describeSubnets().subnets().stream()
                .collect(Collectors.groupingBy(Subnet::vpcId,
                        Collectors.toMap(Subnet::availabilityZone, s -> s, (first, second) -> first)));
        List<String> subnetIds = subnetsByVpcAndZone.values().stream()
                .map(Map::values)
                .filter(subnets -> subnets.size() >= 2)
                .findFirst()
                .map(Collection::stream)
                .orElseThrow()
                .limit(2)
                .map(Subnet::subnetId)
                .toList();

        dms.createReplicationSubnetGroup(b -> b
                .replicationSubnetGroupIdentifier(SUBNET_GROUP_ID)
                .replicationSubnetGroupDescription("floci testcontainers")
                .subnetIds(subnetIds));

        ReplicationSubnetGroup group = describeSubnetGroup();
        assertThat(group.replicationSubnetGroupIdentifier()).isEqualTo(SUBNET_GROUP_ID);
        assertThat(group.subnetGroupStatus()).isEqualTo("Complete");
        assertThat(group.subnets()).hasSize(2);
    }

    @Test
    @Order(2)
    void shouldDeleteReplicationSubnetGroup() {
        dms.deleteReplicationSubnetGroup(b -> b.replicationSubnetGroupIdentifier(SUBNET_GROUP_ID));

        assertThatThrownBy(DmsServiceTest::describeSubnetGroup)
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private static ReplicationSubnetGroup describeSubnetGroup() {
        return dms.describeReplicationSubnetGroups(b -> b
                        .filters(f -> f.name("replication-subnet-group-id").values(SUBNET_GROUP_ID)))
                .replicationSubnetGroups().get(0);
    }
}
