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
        assertThat(config.getMaxMemoryMib()).isEqualTo(0);
        assertThat(config.getMaxVcpus()).isEqualTo(0);
        assertThat(config.getImageTemplate()).isEmpty();
        assertThat(config.isImds()).isFalse();
        assertThat(config.isImdsPodNetwork()).isFalse();
        assertThat(config.isIrsaSigningKey()).isTrue();
        assertThat(config.isPodIdentityWebhook()).isTrue();
        assertThat(config.isEmbeddedDns()).isTrue();
        assertThat(config.isVpcRouteProgramming()).isTrue();
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
                .maxMemoryMib(2048)
                .maxVcpus(2)
                .imageTemplate("custom-registry.internal/k3s:v%s")
                .imds(true)
                .imdsPodNetwork(true)
                .irsaSigningKey(false)
                .podIdentityWebhook(false)
                .embeddedDns(false)
                .vpcRouteProgramming(false)
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
        assertThat(config.getMaxMemoryMib()).isEqualTo(2048);
        assertThat(config.getMaxVcpus()).isEqualTo(2);
        assertThat(config.getImageTemplate()).contains("custom-registry.internal/k3s:v%s");
        assertThat(config.isImds()).isTrue();
        assertThat(config.isImdsPodNetwork()).isTrue();
        assertThat(config.isIrsaSigningKey()).isFalse();
        assertThat(config.isPodIdentityWebhook()).isFalse();
        assertThat(config.isEmbeddedDns()).isFalse();
        assertThat(config.isVpcRouteProgramming()).isFalse();
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
                .doesNotContainKey("FLOCI_SERVICES_EKS_DOCKER_NETWORK")
                .containsEntry("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB", "0")
                .containsEntry("FLOCI_SERVICES_EKS_MAX_VCPUS", "0")
                .doesNotContainKey("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE")
                .containsEntry("FLOCI_SERVICES_EKS_IMDS", "false")
                .containsEntry("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK", "false")
                .containsEntry("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY", "true")
                .containsEntry("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK", "true")
                .containsEntry("FLOCI_SERVICES_EKS_EMBEDDED_DNS", "true")
                .containsEntry("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING", "true");
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
                .maxMemoryMib(2048)
                .maxVcpus(2)
                .imageTemplate("custom-registry.internal/k3s:v%s")
                .imds(true)
                .imdsPodNetwork(true)
                .irsaSigningKey(false)
                .podIdentityWebhook(false)
                .embeddedDns(false)
                .vpcRouteProgramming(false)
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
                .containsEntry("FLOCI_SERVICES_EKS_DISABLE_CNI", "true")
                .containsEntry("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB", "2048")
                .containsEntry("FLOCI_SERVICES_EKS_MAX_VCPUS", "2")
                .containsEntry("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE", "custom-registry.internal/k3s:v%s")
                .containsEntry("FLOCI_SERVICES_EKS_IMDS", "true")
                .containsEntry("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK", "true")
                .containsEntry("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY", "false")
                .containsEntry("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK", "false")
                .containsEntry("FLOCI_SERVICES_EKS_EMBEDDED_DNS", "false")
                .containsEntry("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EksConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EKS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EKS_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_EKS_PROVIDER")
                .doesNotContainKey("FLOCI_SERVICES_EKS_DISABLE_CNI")
                .doesNotContainKey("FLOCI_SERVICES_EKS_MAX_MEMORY_MIB")
                .doesNotContainKey("FLOCI_SERVICES_EKS_MAX_VCPUS")
                .doesNotContainKey("FLOCI_SERVICES_EKS_IMAGE_TEMPLATE")
                .doesNotContainKey("FLOCI_SERVICES_EKS_IMDS")
                .doesNotContainKey("FLOCI_SERVICES_EKS_IMDS_POD_NETWORK")
                .doesNotContainKey("FLOCI_SERVICES_EKS_IRSA_SIGNING_KEY")
                .doesNotContainKey("FLOCI_SERVICES_EKS_POD_IDENTITY_WEBHOOK")
                .doesNotContainKey("FLOCI_SERVICES_EKS_EMBEDDED_DNS")
                .doesNotContainKey("FLOCI_SERVICES_EKS_VPC_ROUTE_PROGRAMMING");
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
                .maxMemoryMib(2048)
                .maxVcpus(2)
                .imageTemplate("custom-registry.internal/k3s:v%s")
                .imds(true)
                .imdsPodNetwork(true)
                .irsaSigningKey(false)
                .podIdentityWebhook(false)
                .embeddedDns(false)
                .vpcRouteProgramming(false)
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
        assertThat(copy.getMaxMemoryMib()).isEqualTo(2048);
        assertThat(copy.getMaxVcpus()).isEqualTo(2);
        assertThat(copy.getImageTemplate()).contains("custom-registry.internal/k3s:v%s");
        assertThat(copy.isImds()).isTrue();
        assertThat(copy.isImdsPodNetwork()).isTrue();
        assertThat(copy.isIrsaSigningKey()).isFalse();
        assertThat(copy.isPodIdentityWebhook()).isFalse();
        assertThat(copy.isEmbeddedDns()).isFalse();
        assertThat(copy.isVpcRouteProgramming()).isFalse();
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabledAndNotMocked() {
        assertThat(EksConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(EksConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
        assertThat(EksConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
    }

}
