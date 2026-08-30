package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.route53resolver.Route53ResolverClient;
import software.amazon.awssdk.services.route53resolver.model.FirewallDomainListMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class Route53ResolverServiceTest extends AbstractServiceTest {

    static Route53ResolverClient route53Resolver;

    @BeforeAll
    static void setUp() {
        route53Resolver = client(Route53ResolverClient.builder());
    }

    @Test
    void shouldListAwsManagedFirewallDomainLists() {
        var response = route53Resolver.listFirewallDomainLists(b -> {});

        assertThat(response.firewallDomainLists())
                .extracting(FirewallDomainListMetadata::name)
                .contains("AWSManagedDomainsMalwareDomainList", "AWSManagedDomainsBotnetCommandandControl");
        assertThat(response.firewallDomainLists().get(0).id()).startsWith("rslvr-fdl-");
    }

    @Test
    void shouldReturnStableIdsAcrossCalls() {
        String first = route53Resolver.listFirewallDomainLists(b -> {}).firewallDomainLists().get(0).id();
        String second = route53Resolver.listFirewallDomainLists(b -> {}).firewallDomainLists().get(0).id();

        assertThat(first).isEqualTo(second);
    }
}
