package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.DirectoryType;
import software.amazon.awssdk.services.connect.model.InstanceStatus;
import software.amazon.awssdk.services.connect.model.InstanceSummary;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
@Disabled("Connect is not yet registered in the floci/floci:nightly image")
class ConnectServiceTest extends AbstractServiceTest {

    static ConnectClient connect;

    static String instanceId;

    @BeforeAll
    static void setUp() {
        connect = client(ConnectClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateInstance() {
        var response = connect.createInstance(b -> b
                .identityManagementType(DirectoryType.CONNECT_MANAGED)
                .instanceAlias("floci-tc-" + System.currentTimeMillis())
                .inboundCallsEnabled(true)
                .outboundCallsEnabled(false));

        assertThat(response.id()).isNotBlank();
        assertThat(response.arn()).contains(":connect:");
        instanceId = response.id();
    }

    @Test
    @Order(2)
    void shouldDescribeInstance() {
        var response = connect.describeInstance(b -> b.instanceId(instanceId));

        assertThat(response.instance().id()).isEqualTo(instanceId);
        assertThat(response.instance().instanceStatus()).isEqualTo(InstanceStatus.ACTIVE);
    }

    @Test
    @Order(3)
    void shouldListInstancesContainsCreatedInstance() {
        var response = connect.listInstances(b -> {});

        assertThat(response.instanceSummaryList())
                .extracting(InstanceSummary::id)
                .contains(instanceId);
    }

    @Test
    @Order(4)
    void shouldDeleteInstance() {
        connect.deleteInstance(b -> b.instanceId(instanceId));

        var response = connect.listInstances(b -> {});
        assertThat(response.instanceSummaryList())
                .extracting(InstanceSummary::id)
                .doesNotContain(instanceId);
    }
}
