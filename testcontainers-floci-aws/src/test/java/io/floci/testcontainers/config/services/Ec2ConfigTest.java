package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.TransferableCopyInspector.contentCopiedTo;
import static org.testcontainers.containers.TransferableCopyInspector.pendingCopies;

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
        assertThat(config.isAwsFaithfulPrivateIp()).isFalse();
        assertThat(config.getContainerIpsRoutable()).isEmpty();
        assertThat(config.getAutoScaling().enabled()).isTrue();
        assertThat(config.isReconcileContainersOnStartup()).isTrue();
        assertThat(config.isVolumeBlockDevices()).isTrue();
        assertThat(config.getVolumeHelperImage()).isEqualTo("alpine:3.21");
        assertThat(config.isInstanceResourceLimits()).isTrue();
        assertThat(config.isVpcNetworksEnabled()).isTrue();
        assertThat(config.getVpcNetworksFallbackPool()).isEqualTo("10.240.0.0/12");
        assertThat(config.getVpcNetworksFallbackPrefixLength()).isEqualTo(16);
        assertThat(config.isVpcNetworksReconcileOnStartup()).isTrue();
        assertThat(config.getVpcNetworksDriver()).isEqualTo("bridge");
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
                .awsFaithfulPrivateIp(true)
                .containerIpsRoutable(true)
                .autoScaling(false)
                .reconcileContainersOnStartup(false)
                .volumeBlockDevices(false)
                .volumeHelperImage("alpine:3.22")
                .instanceResourceLimits(false)
                .vpcNetworksEnabled(false)
                .vpcNetworksFallbackPool("10.200.0.0/14")
                .vpcNetworksFallbackPrefixLength(20)
                .vpcNetworksReconcileOnStartup(false)
                .vpcNetworksDriver("macvlan")
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
        assertThat(config.isAwsFaithfulPrivateIp()).isTrue();
        assertThat(config.getContainerIpsRoutable()).contains(true);
        assertThat(config.getAutoScaling().enabled()).isFalse();
        assertThat(config.isReconcileContainersOnStartup()).isFalse();
        assertThat(config.isVolumeBlockDevices()).isFalse();
        assertThat(config.getVolumeHelperImage()).isEqualTo("alpine:3.22");
        assertThat(config.isInstanceResourceLimits()).isFalse();
        assertThat(config.isVpcNetworksEnabled()).isFalse();
        assertThat(config.getVpcNetworksFallbackPool()).isEqualTo("10.200.0.0/14");
        assertThat(config.getVpcNetworksFallbackPrefixLength()).isEqualTo(20);
        assertThat(config.isVpcNetworksReconcileOnStartup()).isFalse();
        assertThat(config.getVpcNetworksDriver()).isEqualTo("macvlan");
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
                .containsEntry("FLOCI_SERVICES_EC2_AWS_FAITHFUL_PRIVATE_IP", "false")
                .containsEntry("FLOCI_SERVICES_AUTOSCALING_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE")
                .containsEntry("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP", "true")
                .containsEntry("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES", "true")
                .containsEntry("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE", "alpine:3.21")
                .containsEntry("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS", "true")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL", "10.240.0.0/12")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH", "16")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP", "true")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER", "bridge");
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
                .awsFaithfulPrivateIp(true)
                .containerIpsRoutable(true)
                .autoScaling(false)
                .reconcileContainersOnStartup(false)
                .volumeBlockDevices(false)
                .volumeHelperImage("alpine:3.22")
                .instanceResourceLimits(false)
                .vpcNetworksEnabled(false)
                .vpcNetworksFallbackPool("10.200.0.0/14")
                .vpcNetworksFallbackPrefixLength(20)
                .vpcNetworksReconcileOnStartup(false)
                .vpcNetworksDriver("macvlan")
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
                .containsEntry("FLOCI_SERVICES_EC2_AWS_FAITHFUL_PRIVATE_IP", "true")
                .containsEntry("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE", "true")
                .containsEntry("FLOCI_SERVICES_AUTOSCALING_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP", "false")
                .containsEntry("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES", "false")
                .containsEntry("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE", "alpine:3.22")
                .containsEntry("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS", "false")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL", "10.200.0.0/14")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH", "20")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP", "false")
                .containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER", "macvlan");
    }

    @Test
    void shouldApplyContainerIpsRoutableFalseEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder()
                .enabled(true)
                .containerIpsRoutable(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE", "false");
    }

    @Test
    void shouldNotApplyContainerIpsRoutableEnvVarWhenServiceDisabled() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder()
                .enabled(false)
                .containerIpsRoutable(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .doesNotContainKey("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EC2_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP")
                .doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER");
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
                .awsFaithfulPrivateIp(true)
                .containerIpsRoutable(true)
                .autoScaling(false)
                .reconcileContainersOnStartup(false)
                .volumeBlockDevices(false)
                .volumeHelperImage("alpine:3.22")
                .instanceResourceLimits(false)
                .vpcNetworksEnabled(false)
                .vpcNetworksFallbackPool("10.200.0.0/14")
                .vpcNetworksFallbackPrefixLength(20)
                .vpcNetworksReconcileOnStartup(false)
                .vpcNetworksDriver("macvlan")
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
        assertThat(copy.isAwsFaithfulPrivateIp()).isTrue();
        assertThat(copy.getContainerIpsRoutable()).contains(true);
        assertThat(copy.getAutoScaling().enabled()).isFalse();
        assertThat(copy.isReconcileContainersOnStartup()).isFalse();
        assertThat(copy.isVolumeBlockDevices()).isFalse();
        assertThat(copy.getVolumeHelperImage()).isEqualTo("alpine:3.22");
        assertThat(copy.isInstanceResourceLimits()).isFalse();
        assertThat(copy.isVpcNetworksEnabled()).isFalse();
        assertThat(copy.getVpcNetworksFallbackPool()).isEqualTo("10.200.0.0/14");
        assertThat(copy.getVpcNetworksFallbackPrefixLength()).isEqualTo(20);
        assertThat(copy.isVpcNetworksReconcileOnStartup()).isFalse();
        assertThat(copy.getVpcNetworksDriver()).isEqualTo("macvlan");
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(Ec2Config.builder().build().requiresDockerSocket()).isTrue();
        assertThat(Ec2Config.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(Ec2Config.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldUseBundledImageCatalogByDefault() {
        Ec2Config config = Ec2Config.builder().build();
        assertThat(config.getImageCatalogFile()).isEmpty();
        assertThat(config.getImageCatalog()).isEmpty();

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_IMAGE_CATALOG_PATH");
        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldSetImageCatalogPathForExplicitFile() {
        Ec2Config config = Ec2Config.builder()
                .imageCatalogFile("/etc/floci/image-catalog.yaml")
                .build();
        assertThat(config.toBuilder().build().getImageCatalogFile()).contains("/etc/floci/image-catalog.yaml");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);
        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EC2_IMAGE_CATALOG_PATH", "/etc/floci/image-catalog.yaml");
        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldCopyImageCatalogContentIntoContainer() {
        Ec2Config config = Ec2Config.builder()
                .imageCatalog(IMAGE_CATALOG_YAML)
                .build();
        String containerPath = config.getImageCatalogFile().orElseThrow();
        assertThat(containerPath).startsWith("/tmp/floci-ec2-image-catalog-").endsWith(".yaml");

        Ec2Config copy = config.toBuilder().mock(true).build();
        assertThat(copy.getImageCatalogFile()).contains(containerPath);
        assertThat(copy.getImageCatalog()).contains(IMAGE_CATALOG_YAML);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_IMAGE_CATALOG_PATH", containerPath);
        assertThat(pendingCopies(container)).hasSize(1);
        assertThat(contentCopiedTo(container, containerPath)).contains(IMAGE_CATALOG_YAML);

        GenericContainer<?> disabledContainer = genericContainer();
        Ec2Config disabled = config.toBuilder().enabled(false).build();
        disabled.applyEnvVarsToContainer(disabledContainer);
        disabled.applyFileMountsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_IMAGE_CATALOG_PATH");
        assertThat(pendingCopies(disabledContainer)).isEmpty();
    }

    @Test
    void shouldReplaceImageCatalogContentWithExplicitFileAndViceVersa() {
        Ec2Config explicit = Ec2Config.builder()
                .imageCatalog(IMAGE_CATALOG_YAML)
                .imageCatalogFile("/etc/floci/image-catalog.yaml")
                .build();
        assertThat(explicit.getImageCatalog()).isEmpty();
        assertThat(explicit.getImageCatalogFile()).contains("/etc/floci/image-catalog.yaml");

        Ec2Config content = Ec2Config.builder()
                .imageCatalogFile("/etc/floci/image-catalog.yaml")
                .imageCatalog(IMAGE_CATALOG_YAML)
                .build();
        assertThat(content.getImageCatalog()).contains(IMAGE_CATALOG_YAML);
        assertThat(content.getImageCatalogFile().orElseThrow()).startsWith("/tmp/floci-ec2-image-catalog-");
    }

    private static final String IMAGE_CATALOG_YAML = """
            images:
              - id: ami-0123456789abcdef0
                name: local-guest
            """;

}
