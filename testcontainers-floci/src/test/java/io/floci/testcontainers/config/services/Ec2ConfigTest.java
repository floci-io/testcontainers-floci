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
                .doesNotContainKey("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE");
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
                .containsEntry("FLOCI_SERVICES_AUTOSCALING_ENABLED", "false");
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
                .awsFaithfulPrivateIp(true)
                .containerIpsRoutable(true)
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
        assertThat(copy.isAwsFaithfulPrivateIp()).isTrue();
        assertThat(copy.getContainerIpsRoutable()).contains(true);
        assertThat(copy.getAutoScaling().enabled()).isFalse();
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(Ec2Config.builder().build().requiresDockerSocket()).isTrue();
        assertThat(Ec2Config.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(Ec2Config.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldApplyReconcileContainersOnStartup() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.isReconcileContainersOnStartup()).isEqualTo(true);

        Ec2Config config = Ec2Config.builder().reconcileContainersOnStartup(false).build();
        assertThat(config.isReconcileContainersOnStartup()).isEqualTo(false);
        assertThat(config.toBuilder().build().isReconcileContainersOnStartup()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP");
    }

    @Test
    void shouldApplyVolumeBlockDevices() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.isVolumeBlockDevices()).isEqualTo(true);

        Ec2Config config = Ec2Config.builder().volumeBlockDevices(false).build();
        assertThat(config.isVolumeBlockDevices()).isEqualTo(false);
        assertThat(config.toBuilder().build().isVolumeBlockDevices()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES");
    }

    @Test
    void shouldApplyVolumeHelperImage() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.getVolumeHelperImage()).isEqualTo("alpine:3.21");

        Ec2Config config = Ec2Config.builder().volumeHelperImage("alpine:3.22").build();
        assertThat(config.getVolumeHelperImage()).isEqualTo("alpine:3.22");
        assertThat(config.toBuilder().build().getVolumeHelperImage()).isEqualTo("alpine:3.22");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE", "alpine:3.22");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE", "alpine:3.21");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE");
    }

    @Test
    void shouldApplyInstanceResourceLimits() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.isInstanceResourceLimits()).isEqualTo(true);

        Ec2Config config = Ec2Config.builder().instanceResourceLimits(false).build();
        assertThat(config.isInstanceResourceLimits()).isEqualTo(false);
        assertThat(config.toBuilder().build().isInstanceResourceLimits()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS");
    }

    @Test
    void shouldApplyVpcNetworksEnabled() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.isVpcNetworksEnabled()).isEqualTo(true);

        Ec2Config config = Ec2Config.builder().vpcNetworksEnabled(false).build();
        assertThat(config.isVpcNetworksEnabled()).isEqualTo(false);
        assertThat(config.toBuilder().build().isVpcNetworksEnabled()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED");
    }

    @Test
    void shouldApplyVpcNetworksFallbackPool() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.getVpcNetworksFallbackPool()).isEqualTo("10.240.0.0/12");

        Ec2Config config = Ec2Config.builder().vpcNetworksFallbackPool("10.200.0.0/14").build();
        assertThat(config.getVpcNetworksFallbackPool()).isEqualTo("10.200.0.0/14");
        assertThat(config.toBuilder().build().getVpcNetworksFallbackPool()).isEqualTo("10.200.0.0/14");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL", "10.200.0.0/14");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL", "10.240.0.0/12");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL");
    }

    @Test
    void shouldApplyVpcNetworksFallbackPrefixLength() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.getVpcNetworksFallbackPrefixLength()).isEqualTo(16);

        Ec2Config config = Ec2Config.builder().vpcNetworksFallbackPrefixLength(20).build();
        assertThat(config.getVpcNetworksFallbackPrefixLength()).isEqualTo(20);
        assertThat(config.toBuilder().build().getVpcNetworksFallbackPrefixLength()).isEqualTo(20);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH", "20");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH", "16");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH");
    }

    @Test
    void shouldApplyVpcNetworksReconcileOnStartup() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.isVpcNetworksReconcileOnStartup()).isEqualTo(true);

        Ec2Config config = Ec2Config.builder().vpcNetworksReconcileOnStartup(false).build();
        assertThat(config.isVpcNetworksReconcileOnStartup()).isEqualTo(false);
        assertThat(config.toBuilder().build().isVpcNetworksReconcileOnStartup()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP");
    }

    @Test
    void shouldApplyVpcNetworksDriver() {
        Ec2Config defaults = Ec2Config.builder().build();
        assertThat(defaults.getVpcNetworksDriver()).isEqualTo("bridge");

        Ec2Config config = Ec2Config.builder().vpcNetworksDriver("macvlan").build();
        assertThat(config.getVpcNetworksDriver()).isEqualTo("macvlan");
        assertThat(config.toBuilder().build().getVpcNetworksDriver()).isEqualTo("macvlan");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER", "macvlan");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER", "bridge");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER");
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
