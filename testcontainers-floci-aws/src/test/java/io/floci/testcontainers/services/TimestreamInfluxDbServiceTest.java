package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.timestreaminfluxdb.TimestreamInfluxDbClient;
import software.amazon.awssdk.services.timestreaminfluxdb.model.DbInstanceSummary;
import software.amazon.awssdk.services.timestreaminfluxdb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.timestreaminfluxdb.model.Status;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class TimestreamInfluxDbServiceTest extends AbstractServiceTest {

    static TimestreamInfluxDbClient influxDb;

    static String dbInstanceId;

    @BeforeAll
    static void setUp() {
        influxDb = client(TimestreamInfluxDbClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateDbInstance() {
        var response = influxDb.createDbInstance(b -> b
                .name("floci-tc-influxdb")
                .password("password123")
                .dbInstanceType("db.influx.medium")
                .allocatedStorage(20)
                .vpcSubnetIds("subnet-abc123")
                .vpcSecurityGroupIds("sg-abc123"));

        assertThat(response.id()).isNotBlank();
        dbInstanceId = response.id();

        await().atMost(Duration.ofSeconds(180)).pollInterval(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThat(influxDb.getDbInstance(b -> b.identifier(dbInstanceId)).status()).isEqualTo(Status.AVAILABLE));
    }

    @Test
    @Order(2)
    void shouldListDbInstances() {
        assertThat(influxDb.listDbInstances(b -> {}).items())
                .extracting(DbInstanceSummary::id)
                .contains(dbInstanceId);
    }

    @Test
    @Order(3)
    void shouldExposeDbInstanceEndpoint() {
        var instance = influxDb.getDbInstance(b -> b.identifier(dbInstanceId));

        assertThat(instance.endpoint()).isNotBlank();
        assertThat(instance.port()).isPositive();
    }

    @Test
    @Order(4)
    void shouldDeleteDbInstance() {
        influxDb.deleteDbInstance(b -> b.identifier(dbInstanceId));

        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThatThrownBy(() -> influxDb.getDbInstance(b -> b.identifier(dbInstanceId)))
                        .isInstanceOf(ResourceNotFoundException.class));
    }
}
