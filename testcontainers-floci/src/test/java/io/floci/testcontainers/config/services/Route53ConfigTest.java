package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Route53ConfigTest {

    @Test
    void shouldApplyDefaultRoute53Config() {
        Route53Config config = Route53Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultNameserver1()).isEqualTo("ns-1.awsdns-01.org");
        assertThat(config.getDefaultNameserver2()).isEqualTo("ns-2.awsdns-02.net");
        assertThat(config.getDefaultNameserver3()).isEqualTo("ns-3.awsdns-03.com");
        assertThat(config.getDefaultNameserver4()).isEqualTo("ns-4.awsdns-04.co.uk");
    }

    @Test
    void shouldApplyCustomRoute53Config() {
        Route53Config config = Route53Config.builder()
                .enabled(false)
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultNameserver1()).isEqualTo("ns1.example.com");
        assertThat(config.getDefaultNameserver2()).isEqualTo("ns2.example.com");
        assertThat(config.getDefaultNameserver3()).isEqualTo("ns3.example.com");
        assertThat(config.getDefaultNameserver4()).isEqualTo("ns4.example.com");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ROUTE53_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", "ns-1.awsdns-01.org")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2", "ns-2.awsdns-02.net")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3", "ns-3.awsdns-03.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4", "ns-4.awsdns-04.co.uk");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder()
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", "ns1.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2", "ns2.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3", "ns3.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4", "ns4.example.com");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ROUTE53_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Route53Config config = Route53Config.builder()
                .enabled(false)
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .build();
        Route53Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultNameserver1()).isEqualTo("ns1.example.com");
        assertThat(copy.getDefaultNameserver2()).isEqualTo("ns2.example.com");
        assertThat(copy.getDefaultNameserver3()).isEqualTo("ns3.example.com");
        assertThat(copy.getDefaultNameserver4()).isEqualTo("ns4.example.com");
    }

}
