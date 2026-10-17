package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.BrokerState;
import software.amazon.awssdk.services.mq.model.BrokerSummary;
import software.amazon.awssdk.services.mq.model.DeploymentMode;
import software.amazon.awssdk.services.mq.model.EngineType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AmazonMqServiceTest extends AbstractServiceTest {

    static MqClient mq;
    static String brokerId;

    private static final String BROKER_NAME = "test-broker-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        mq = client(MqClient.builder());
    }

    @Test
    @Order(1)
    void shouldListBrokers() {
        List<BrokerSummary> brokers = mq.listBrokers().brokerSummaries();

        assertThat(brokers).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateBroker() {
        var response = mq.createBroker(b -> b
                .brokerName(BROKER_NAME)
                .engineType(EngineType.RABBITMQ)
                .engineVersion("3.13")
                .hostInstanceType("mq.t3.micro")
                .deploymentMode(DeploymentMode.SINGLE_INSTANCE)
                .publiclyAccessible(false)
                .users(u -> u.username("admin").password("Test-Password123")));

        brokerId = response.brokerId();
        assertThat(brokerId).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldDescribeBroker() {
        var response = mq.describeBroker(b -> b.brokerId(brokerId));

        assertThat(response.brokerName()).isEqualTo(BROKER_NAME);
        assertThat(response.brokerState()).isNotEqualTo(BrokerState.UNKNOWN_TO_SDK_VERSION);
    }

    @Test
    @Order(4)
    void shouldListBrokersContainsCreatedBroker() {
        List<BrokerSummary> brokers = mq.listBrokers().brokerSummaries();

        assertThat(brokers).anyMatch(b -> b.brokerId().equals(brokerId));
    }

    @Test
    @Order(5)
    void shouldDeleteBroker() {
        mq.deleteBroker(b -> b.brokerId(brokerId));

        List<BrokerSummary> brokers = mq.listBrokers().brokerSummaries();
        assertThat(brokers).noneMatch(b -> b.brokerId().equals(brokerId));
    }
}
