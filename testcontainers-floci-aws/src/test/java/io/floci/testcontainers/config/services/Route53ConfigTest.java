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
        assertThat(config.getDefaultNameserver1()).isNull();
        assertThat(config.getDefaultNameserver2()).isNull();
        assertThat(config.getDefaultNameserver3()).isNull();
        assertThat(config.getDefaultNameserver4()).isNull();
        assertThat(config.getVpcAssociationControlPlaneDelayMs()).isEqualTo(0L);
    }

    @Test
    void shouldApplyCustomRoute53Config() {
        Route53Config config = Route53Config.builder()
                .enabled(false)
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .vpcAssociationControlPlaneDelayMs(2000L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultNameserver1()).isEqualTo("ns1.example.com");
        assertThat(config.getDefaultNameserver2()).isEqualTo("ns2.example.com");
        assertThat(config.getDefaultNameserver3()).isEqualTo("ns3.example.com");
        assertThat(config.getDefaultNameserver4()).isEqualTo("ns4.example.com");
        assertThat(config.getVpcAssociationControlPlaneDelayMs()).isEqualTo(2000L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ROUTE53_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1")
                .doesNotContainKey("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2")
                .doesNotContainKey("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3")
                .doesNotContainKey("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4")
                .containsEntry("FLOCI_SERVICES_ROUTE53_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder()
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .vpcAssociationControlPlaneDelayMs(2000L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", "ns1.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2", "ns2.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3", "ns3.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4", "ns4.example.com")
                .containsEntry("FLOCI_SERVICES_ROUTE53_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS", "2000");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ROUTE53_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ROUTE53_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Route53Config config = Route53Config.builder()
                .enabled(false)
                .defaultNameserver1("ns1.example.com")
                .defaultNameserver2("ns2.example.com")
                .defaultNameserver3("ns3.example.com")
                .defaultNameserver4("ns4.example.com")
                .vpcAssociationControlPlaneDelayMs(2000L)
                .build();
        Route53Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultNameserver1()).isEqualTo("ns1.example.com");
        assertThat(copy.getDefaultNameserver2()).isEqualTo("ns2.example.com");
        assertThat(copy.getDefaultNameserver3()).isEqualTo("ns3.example.com");
        assertThat(copy.getDefaultNameserver4()).isEqualTo("ns4.example.com");
        assertThat(copy.getVpcAssociationControlPlaneDelayMs()).isEqualTo(2000L);
    }

}
