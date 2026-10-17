package io.floci.testcontainers.oci;

import org.junit.jupiter.api.Test;

import static io.floci.testcontainers.core.testing.DockerSocketInspector.configsRequiringDockerSocket;
import static io.floci.testcontainers.core.testing.DockerSocketInspector.serviceConfigs;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the {@link FlociOciContainer#DESCRIPTOR} and the Docker socket services against the reference of the
 * specification, which the other language ports check their tables against.
 */
class FlociOciContainerDescriptorTest {

    @Test
    void shouldDescribeFlociOci() {
        assertThat(FlociOciContainer.DESCRIPTOR.getName()).isEqualTo("oci");
        assertThat(FlociOciContainer.DESCRIPTOR.getImage()).isEqualTo("floci/floci-oci");
        assertThat(FlociOciContainer.DESCRIPTOR.getPort()).isEqualTo(4599);
        assertThat(FlociOciContainer.DESCRIPTOR.getEnvPrefix()).isEqualTo("FLOCI_OCI_");
        assertThat(FlociOciContainer.DESCRIPTOR.getHealthPath()).isEqualTo("/_floci-oci/health");
        assertThat(FlociOciContainer.DESCRIPTOR.getResetPath()).isEqualTo("/_floci-oci/state/reset");
        assertThat(FlociOciContainer.DESCRIPTOR.getLogLevelEnvVar()).isEqualTo("QUARKUS_LOG_CATEGORY__IO_FLOCI_OCI__LEVEL");
        assertThat(FlociOciContainer.DESCRIPTOR.getNetworkEnvVar()).isEqualTo("FLOCI_OCI_SERVICES_DOCKER_NETWORK");
        assertThat(FlociOciContainer.DESCRIPTOR.getResourceNamespaceEnvVar()).isEqualTo("FLOCI_OCI_DOCKER_RESOURCE_NAMESPACE");
        assertThat(FlociOciContainer.DESCRIPTOR.getTlsHttpsPortEnvVar()).isEqualTo("FLOCI_OCI_TLS_HTTPS_PORT");
    }

    @Test
    void shouldApplyDefaultEnv() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(container.getEnvMap()).containsEntry("FLOCI_OCI_TLS_HTTPS_PORT", "0");
        }
    }

    @Test
    void shouldRequireDockerSocketForReferenceServicesByDefault() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(configsRequiringDockerSocket(container)).containsExactlyInAnyOrder(
                    "FunctionsConfig", "OkeConfig");
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenAllServicesAreDisabled() {
        try (FlociOciContainer container = new FlociOciContainer().disableAllServices()) {
            assertThat(serviceConfigs(container)).isNotEmpty();
            assertThat(configsRequiringDockerSocket(container)).isEmpty();
        }
    }
}
