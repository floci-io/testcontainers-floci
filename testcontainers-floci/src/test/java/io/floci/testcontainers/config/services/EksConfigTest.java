package io.floci.testcontainers.config.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EksConfigTest {

    @Test
    void shouldApplyDefaultEksConfig() {
        EksConfig config = EksConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getProvider()).isEqualTo("k3s");
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:latest");
        assertThat(config.getApiServerBasePort()).isEqualTo(6500);
        assertThat(config.getApiServerPortsCount()).isEqualTo(10);
        assertThat(config.getApiServerMaxPort()).isEqualTo(6509);
        assertThat(config.getDockerNetwork()).isNull();
        assertThat(config.getEndpointMode()).isEqualTo("host");
        assertThat(config.isIamAuthWebhook()).isTrue();
        assertThat(config.isEcrRegistryMirror()).isTrue();
        assertThat(config.isDisableCni()).isFalse();
    }

    @Test
    void shouldApplyCustomEksConfig() {
        EksConfig config = EksConfig.builder()
                .enabled(false)
                .mock(true)
                .provider("kind")
                .defaultImage("kindest/node:v1.30")
                .apiServerPortRange(8000, 50)
                .dockerNetwork("my-eks-network")
                .endpointMode("network")
                .iamAuthWebhook(false)
                .ecrRegistryMirror(false)
                .disableCni(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getProvider()).isEqualTo("kind");
        assertThat(config.getDefaultImage()).isEqualTo("kindest/node:v1.30");
        assertThat(config.getApiServerBasePort()).isEqualTo(8000);
        assertThat(config.getApiServerPortsCount()).isEqualTo(50);
        assertThat(config.getApiServerMaxPort()).isEqualTo(8049);
        assertThat(config.getDockerNetwork()).isEqualTo("my-eks-network");
        assertThat(config.getEndpointMode()).isEqualTo("network");
        assertThat(config.isIamAuthWebhook()).isFalse();
        assertThat(config.isEcrRegistryMirror()).isFalse();
        assertThat(config.isDisableCni()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EksConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EKS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EKS_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_EKS_PROVIDER", "k3s")
                .containsEntry("FLOCI_SERVICES_EKS_DEFAULT_IMAGE", "rancher/k3s:latest")
                .containsEntry("FLOCI_SERVICES_EKS_API_SERVER_BASE_PORT", "6500")
                .containsEntry("FLOCI_SERVICES_EKS_API_SERVER_MAX_PORT", "6509")
                .containsEntry("FLOCI_SERVICES_EKS_ENDPOINT_MODE", "host")
                .containsEntry("FLOCI_SERVICES_EKS_IAM_AUTH_WEBHOOK", "true")
                .containsEntry("FLOCI_SERVICES_EKS_ECR_REGISTRY_MIRROR", "true")
                .containsEntry("FLOCI_SERVICES_EKS_DISABLE_CNI", "false")
                .doesNotContainKey("FLOCI_SERVICES_EKS_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EksConfig.builder()
                .enabled(true)
                .mock(true)
                .provider("kind")
                .defaultImage("kindest/node:v1.30")
                .apiServerPortRange(8000, 50)
                .dockerNetwork("my-eks-network")
                .endpointMode("network")
                .iamAuthWebhook(false)
                .ecrRegistryMirror(false)
                .disableCni(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EKS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EKS_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_EKS_PROVIDER", "kind")
                .containsEntry("FLOCI_SERVICES_EKS_DEFAULT_IMAGE", "kindest/node:v1.30")
                .containsEntry("FLOCI_SERVICES_EKS_API_SERVER_BASE_PORT", "8000")
                .containsEntry("FLOCI_SERVICES_EKS_API_SERVER_MAX_PORT", "8049")
                .containsEntry("FLOCI_SERVICES_EKS_DOCKER_NETWORK", "my-eks-network")
                .containsEntry("FLOCI_SERVICES_EKS_ENDPOINT_MODE", "network")
                .containsEntry("FLOCI_SERVICES_EKS_IAM_AUTH_WEBHOOK", "false")
                .containsEntry("FLOCI_SERVICES_EKS_ECR_REGISTRY_MIRROR", "false")
                .containsEntry("FLOCI_SERVICES_EKS_DISABLE_CNI", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EksConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EKS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EKS_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_EKS_PROVIDER")
                .doesNotContainKey("FLOCI_SERVICES_EKS_DISABLE_CNI");
    }

    @Test
    void shouldNotExposeEksPortsWhenDisabled() {
        try (FlociContainer container = new FlociContainer()) {
            container.withEksConfig(c -> c.enabled(false).apiServerPortRange(8000, 50));

            var env = container.getEnvMap();
            assertThat(env).containsEntry("FLOCI_SERVICES_EKS_ENABLED", "false");
            assertThat(container.getExposedPorts()).doesNotContain(8000);
        }
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EksConfig config = EksConfig.builder()
                .enabled(false)
                .mock(true)
                .provider("kind")
                .defaultImage("test-image")
                .apiServerPortRange(6600, 5)
                .dockerNetwork("test-network")
                .endpointMode("container")
                .iamAuthWebhook(false)
                .ecrRegistryMirror(false)
                .disableCni(true)
                .build();
        EksConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getProvider()).isEqualTo("kind");
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getApiServerBasePort()).isEqualTo(6600);
        assertThat(copy.getApiServerPortsCount()).isEqualTo(5);
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
        assertThat(copy.getEndpointMode()).isEqualTo("container");
        assertThat(copy.isIamAuthWebhook()).isFalse();
        assertThat(copy.isEcrRegistryMirror()).isFalse();
        assertThat(copy.isDisableCni()).isTrue();
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(EksConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(EksConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(EksConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldApplyMaxMemoryMib() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.getMaxMemoryMib()).isEqualTo(0);

        EksConfig config = EksConfig.builder().maxMemoryMib(2048).build();
        assertThat(config.getMaxMemoryMib()).isEqualTo(2048);
        assertThat(config.toBuilder().build().getMaxMemoryMib()).isEqualTo(2048);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB", "2048");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB");
    }

    @Test
    void shouldApplyMaxVcpus() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.getMaxVcpus()).isEqualTo(0);

        EksConfig config = EksConfig.builder().maxVcpus(2).build();
        assertThat(config.getMaxVcpus()).isEqualTo(2);
        assertThat(config.toBuilder().build().getMaxVcpus()).isEqualTo(2);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_MAX_VCPUS", "2");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_MAX_VCPUS", "0");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_MAX_VCPUS");
    }

    @Test
    void shouldApplyImageTemplate() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.getImageTemplate()).isEmpty();

        EksConfig config = EksConfig.builder().imageTemplate("custom-registry.internal/k3s:v%s").build();
        assertThat(config.getImageTemplate()).contains("custom-registry.internal/k3s:v%s");
        assertThat(config.toBuilder().build().getImageTemplate()).contains("custom-registry.internal/k3s:v%s");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE", "custom-registry.internal/k3s:v%s");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE");
    }

    @Test
    void shouldApplyImds() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isImds()).isEqualTo(false);

        EksConfig config = EksConfig.builder().imds(true).build();
        assertThat(config.isImds()).isEqualTo(true);
        assertThat(config.toBuilder().build().isImds()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IMDS", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IMDS", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_IMDS");
    }

    @Test
    void shouldApplyImdsPodNetwork() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isImdsPodNetwork()).isEqualTo(false);

        EksConfig config = EksConfig.builder().imdsPodNetwork(true).build();
        assertThat(config.isImdsPodNetwork()).isEqualTo(true);
        assertThat(config.toBuilder().build().isImdsPodNetwork()).isEqualTo(true);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK", "true");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK", "false");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK");
    }

    @Test
    void shouldApplyIrsaSigningKey() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isIrsaSigningKey()).isEqualTo(true);

        EksConfig config = EksConfig.builder().irsaSigningKey(false).build();
        assertThat(config.isIrsaSigningKey()).isEqualTo(false);
        assertThat(config.toBuilder().build().isIrsaSigningKey()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY");
    }

    @Test
    void shouldApplyPodIdentityWebhook() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isPodIdentityWebhook()).isEqualTo(true);

        EksConfig config = EksConfig.builder().podIdentityWebhook(false).build();
        assertThat(config.isPodIdentityWebhook()).isEqualTo(false);
        assertThat(config.toBuilder().build().isPodIdentityWebhook()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK");
    }

    @Test
    void shouldApplyEmbeddedDns() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isEmbeddedDns()).isEqualTo(true);

        EksConfig config = EksConfig.builder().embeddedDns(false).build();
        assertThat(config.isEmbeddedDns()).isEqualTo(false);
        assertThat(config.toBuilder().build().isEmbeddedDns()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_EMBEDDED_DNS", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_EMBEDDED_DNS", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_EMBEDDED_DNS");
    }

    @Test
    void shouldApplyVpcRouteProgramming() {
        EksConfig defaults = EksConfig.builder().build();
        assertThat(defaults.isVpcRouteProgramming()).isEqualTo(true);

        EksConfig config = EksConfig.builder().vpcRouteProgramming(false).build();
        assertThat(config.isVpcRouteProgramming()).isEqualTo(false);
        assertThat(config.toBuilder().build().isVpcRouteProgramming()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING");
    }

}
