package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Ec2ConfigTest {

    @Test
    void shouldApplyDefaultEc2Config() {
        Ec2Config config = Ec2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getImdsPort()).isEqualTo(9169);
        assertThat(config.getSshPortRangeStart()).isEqualTo(2200);
        assertThat(config.getSshPortRangeEnd()).isEqualTo(2299);
        assertThat(config.isPublishSecurityGroupPorts()).isTrue();
        assertThat(config.getAppPortRangeStart()).isEqualTo(30000);
        assertThat(config.getAppPortsCount()).isEqualTo(10);
        assertThat(config.getAppPortRangeEnd()).isEqualTo(30009);
        assertThat(config.getMaxPublishedPortsPerInstance()).isEqualTo(2);
        assertThat(config.getSocatImage()).isEqualTo("alpine/socat");
        assertThat(config.getAutoScaling().enabled()).isTrue();
    }

    @Test
    void shouldApplyCustomEc2Config() {
        Ec2Config config = Ec2Config.builder()
                .enabled(false)
                .mock(true)
                .imdsPort(9170)
                .sshPortRange(2300, 2399)
                .publishSecurityGroupPorts(false)
                .appPortRange(40000, 500)
                .maxPublishedPortsPerInstance(50)
                .socatImage("alpine/socat:1.8.0.0")
                .autoScaling(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getImdsPort()).isEqualTo(9170);
        assertThat(config.getSshPortRangeStart()).isEqualTo(2300);
        assertThat(config.getSshPortRangeEnd()).isEqualTo(2399);
        assertThat(config.isPublishSecurityGroupPorts()).isFalse();
        assertThat(config.getAppPortRangeStart()).isEqualTo(40000);
        assertThat(config.getAppPortsCount()).isEqualTo(500);
        assertThat(config.getAppPortRangeEnd()).isEqualTo(40499);
        assertThat(config.getMaxPublishedPortsPerInstance()).isEqualTo(50);
        assertThat(config.getSocatImage()).isEqualTo("alpine/socat:1.8.0.0");
        assertThat(config.getAutoScaling().enabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EC2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EC2_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_EC2_IMDS_PORT", "9169")
                .containsEntry("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_START", "2200")
                .containsEntry("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_END", "2299")
                .containsEntry("FLOCI_SERVICES_EC2_PUBLISH_SECURITY_GROUP_PORTS", "true")
                .containsEntry("FLOCI_SERVICES_EC2_APP_PORT_RANGE_START", "30000")
                .containsEntry("FLOCI_SERVICES_EC2_APP_PORT_RANGE_END", "30009")
                .containsEntry("FLOCI_SERVICES_EC2_MAX_PUBLISHED_PORTS_PER_INSTANCE", "2")
                .containsEntry("FLOCI_SERVICES_EC2_SOCAT_IMAGE", "alpine/socat")
                .containsEntry("FLOCI_SERVICES_AUTOSCALING_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder()
                .enabled(true)
                .mock(true)
                .imdsPort(9170)
                .sshPortRange(2300, 2399)
                .publishSecurityGroupPorts(false)
                .appPortRange(40000, 500)
                .maxPublishedPortsPerInstance(50)
                .socatImage("alpine/socat:1.8.0.0")
                .autoScaling(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EC2_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EC2_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_EC2_IMDS_PORT", "9170")
                .containsEntry("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_START", "2300")
                .containsEntry("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_END", "2399")
                .containsEntry("FLOCI_SERVICES_EC2_PUBLISH_SECURITY_GROUP_PORTS", "false")
                .containsEntry("FLOCI_SERVICES_EC2_APP_PORT_RANGE_START", "40000")
                .containsEntry("FLOCI_SERVICES_EC2_APP_PORT_RANGE_END", "40499")
                .containsEntry("FLOCI_SERVICES_EC2_MAX_PUBLISHED_PORTS_PER_INSTANCE", "50")
                .containsEntry("FLOCI_SERVICES_EC2_SOCAT_IMAGE", "alpine/socat:1.8.0.0")
                .containsEntry("FLOCI_SERVICES_AUTOSCALING_ENABLED", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_ENABLED", "false");
    }

    @Test
    void shouldExposeAppPortsWhenEnabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEc2Config(c -> c
                    .publishSecurityGroupPorts(true)
                    .appPortRange(30000, 10));

            var ports = container.getExposedPorts();
            for (int port = 30000; port < 30010; port++) {
                assertThat(ports).contains(port);
            }
        }
    }

    @Test
    void shouldNotExposeAppPortsWhenPublishSecurityGroupPortsDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEc2Config(c -> c
                    .publishSecurityGroupPorts(false)
                    .appPortRange(30000, 10));

            assertThat(container.getExposedPorts()).doesNotContain(30000);
        }
    }

    @Test
    void shouldNotExposeAppPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEc2Config(c -> c
                    .enabled(false)
                    .appPortRange(30000, 10));

            assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(30000);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Ec2Config config = Ec2Config.builder()
                .enabled(false)
                .mock(true)
                .imdsPort(9999)
                .sshPortRange(2100, 2199)
                .publishSecurityGroupPorts(false)
                .appPortRange(31000, 5)
                .maxPublishedPortsPerInstance(3)
                .socatImage("test/socat")
                .autoScaling(false)
                .build();
        Ec2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getImdsPort()).isEqualTo(9999);
        assertThat(copy.getSshPortRangeStart()).isEqualTo(2100);
        assertThat(copy.getSshPortRangeEnd()).isEqualTo(2199);
        assertThat(copy.isPublishSecurityGroupPorts()).isFalse();
        assertThat(copy.getAppPortRangeStart()).isEqualTo(31000);
        assertThat(copy.getAppPortsCount()).isEqualTo(5);
        assertThat(copy.getMaxPublishedPortsPerInstance()).isEqualTo(3);
        assertThat(copy.getSocatImage()).isEqualTo("test/socat");
        assertThat(copy.getAutoScaling().enabled()).isFalse();
    }

}
