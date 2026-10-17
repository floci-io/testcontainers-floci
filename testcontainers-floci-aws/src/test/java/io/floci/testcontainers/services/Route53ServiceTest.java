package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.HostedZone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Route53ServiceTest extends AbstractServiceTest {

    static Route53Client route53;

    @BeforeAll
    static void setUp() {
        route53 = client(Route53Client.builder());
    }

    @Test
    void shouldListHostedZones() {
        List<HostedZone> zones = route53.listHostedZones(b -> {}).hostedZones();

        assertThat(zones).isNotNull();
    }

    @Test
    void shouldCreateAndListHostedZone() {
        String zoneName = "example-" + System.currentTimeMillis() + ".com";

        route53.createHostedZone(b -> b
                .name(zoneName)
                .callerReference("ref-" + System.currentTimeMillis()));

        List<String> zoneNames = route53.listHostedZones(b -> {}).hostedZones().stream()
                .map(HostedZone::name)
                .toList();

        assertThat(zoneNames).anyMatch(name -> name.startsWith(zoneName));
    }

}
