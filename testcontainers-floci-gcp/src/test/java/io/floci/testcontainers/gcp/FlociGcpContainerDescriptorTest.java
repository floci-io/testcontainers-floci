package io.floci.testcontainers.gcp;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the {@link FlociGcpContainer#DESCRIPTOR} and the Docker socket services against the reference of the
 * specification, which the other language ports check their tables against.
 */
class FlociGcpContainerDescriptorTest {

    @Test
    void shouldDescribeFlociGcp() {
        assertThat(FlociGcpContainer.DESCRIPTOR.getName()).isEqualTo("gcp");
        assertThat(FlociGcpContainer.DESCRIPTOR.getImage()).isEqualTo("floci/floci-gcp");
        assertThat(FlociGcpContainer.DESCRIPTOR.getPort()).isEqualTo(4588);
        assertThat(FlociGcpContainer.DESCRIPTOR.getEnvPrefix()).isEqualTo("FLOCI_GCP_");
        assertThat(FlociGcpContainer.DESCRIPTOR.getHealthPath()).isEqualTo("/_floci-gcp/health");
        assertThat(FlociGcpContainer.DESCRIPTOR.getResetPath()).isEqualTo("/_floci-gcp/state/reset");
        assertThat(FlociGcpContainer.DESCRIPTOR.getLogLevelEnvVar()).isEqualTo("QUARKUS_LOG_CATEGORY__IO_FLOCI_GCP__LEVEL");
        assertThat(FlociGcpContainer.DESCRIPTOR.getNetworkEnvVar()).isEqualTo("FLOCI_GCP_SERVICES_DOCKER_NETWORK");
        assertThat(FlociGcpContainer.DESCRIPTOR.getResourceNamespaceEnvVar()).isEqualTo("FLOCI_GCP_DOCKER_RESOURCE_NAMESPACE");
        assertThat(FlociGcpContainer.DESCRIPTOR.getTlsHttpsPortEnvVar()).isEqualTo("FLOCI_GCP_TLS_HTTPS_PORT");
    }

    @Test
    void shouldApplyDefaultEnv() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getEnvMap()).containsEntry("FLOCI_GCP_TLS_HTTPS_PORT", "0");
        }
    }

    @Test
    void shouldRequireDockerSocketForReferenceServicesByDefault() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getServiceConfigs())
                    .filteredOn(AbstractServiceConfig::requiresDockerSocket)
                    .map(config -> config.getClass().getSimpleName())
                    .containsExactlyInAnyOrder(
                            "BigQueryConfig", "CloudRunConfig", "CloudSqlConfig", "GkeConfig", "KafkaConfig");
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenAllServicesAreDisabled() {
        try (FlociGcpContainer container = new FlociGcpContainer().disableAllServices()) {
            assertThat(container.getServiceConfigs()).isNotEmpty();
            assertThat(container.getServiceConfigs())
                    .filteredOn(AbstractServiceConfig::requiresDockerSocket)
                    .map(config -> config.getClass().getSimpleName())
                    .isEmpty();
        }
    }
}
