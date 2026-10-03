package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingClient;
import software.amazon.awssdk.services.elasticloadbalancing.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElbServiceTest extends AbstractServiceTest {

    static ElasticLoadBalancingClient elb;

    static String lbName;

    @BeforeAll
    static void setUp() {
        elb = client(ElasticLoadBalancingClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateLoadBalancer() {
        lbName = "classic-lb-" + System.currentTimeMillis();

        String dnsName = elb.createLoadBalancer(b -> b
                        .loadBalancerName(lbName)
                        .availabilityZones("us-east-1a")
                        .listeners(Listener.builder()
                                .protocol("HTTP")
                                .loadBalancerPort(80)
                                .instanceProtocol("HTTP")
                                .instancePort(8080)
                                .build())
                        .tags(Tag.builder().key("env").value("test").build()))
                .dnsName();

        assertThat(dnsName).startsWith(lbName + "-");
    }

    @Test
    @Order(2)
    void shouldDescribeLoadBalancer() {
        LoadBalancerDescription description = elb.describeLoadBalancers(b -> b.loadBalancerNames(lbName))
                .loadBalancerDescriptions().get(0);

        assertThat(description.loadBalancerName()).isEqualTo(lbName);
        assertThat(description.listenerDescriptions())
                .extracting(ListenerDescription::listener)
                .extracting(Listener::loadBalancerPort)
                .containsExactly(80);
        assertThat(elb.describeTags(b -> b.loadBalancerNames(lbName)).tagDescriptions().get(0).tags())
                .extracting(Tag::key, Tag::value)
                .containsExactly(tuple("env", "test"));
    }

    @Test
    @Order(3)
    void shouldConfigureHealthCheck() {
        HealthCheck healthCheck = elb.configureHealthCheck(b -> b
                        .loadBalancerName(lbName)
                        .healthCheck(HealthCheck.builder()
                                .target("HTTP:8080/health")
                                .interval(30)
                                .timeout(5)
                                .healthyThreshold(2)
                                .unhealthyThreshold(3)
                                .build()))
                .healthCheck();

        assertThat(healthCheck.target()).isEqualTo("HTTP:8080/health");
    }

    @Test
    @Order(4)
    void shouldAddAndRemoveListener() {
        elb.createLoadBalancerListeners(b -> b
                .loadBalancerName(lbName)
                .listeners(Listener.builder()
                        .protocol("TCP")
                        .loadBalancerPort(8443)
                        .instanceProtocol("TCP")
                        .instancePort(8443)
                        .build()));
        assertThat(listenerPorts()).containsExactlyInAnyOrder(80, 8443);

        elb.deleteLoadBalancerListeners(b -> b.loadBalancerName(lbName).loadBalancerPorts(8443));
        assertThat(listenerPorts()).containsExactly(80);
    }

    @Test
    @Order(5)
    void shouldDeleteLoadBalancer() {
        elb.deleteLoadBalancer(b -> b.loadBalancerName(lbName));

        assertThatThrownBy(() -> elb.describeLoadBalancers(b -> b.loadBalancerNames(lbName)))
                .isInstanceOf(LoadBalancerNotFoundException.class);
    }

    private static List<Integer> listenerPorts() {
        return elb.describeLoadBalancers(b -> b.loadBalancerNames(lbName))
                .loadBalancerDescriptions().get(0)
                .listenerDescriptions().stream()
                .map(d -> d.listener().loadBalancerPort())
                .toList();
    }
}
