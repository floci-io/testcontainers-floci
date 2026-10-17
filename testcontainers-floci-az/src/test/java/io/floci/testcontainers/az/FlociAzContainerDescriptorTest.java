package io.floci.testcontainers.az;

import org.junit.jupiter.api.Test;

import static io.floci.testcontainers.core.testing.DockerSocketInspector.configsRequiringDockerSocket;
import static io.floci.testcontainers.core.testing.DockerSocketInspector.serviceConfigs;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the {@link FlociAzContainer#DESCRIPTOR} and the Docker socket services against the reference of the
 * specification, which the other language ports check their tables against.
 */
class FlociAzContainerDescriptorTest {

    @Test
    void shouldDescribeFlociAzure() {
        assertThat(FlociAzContainer.DESCRIPTOR.getName()).isEqualTo("az");
        assertThat(FlociAzContainer.DESCRIPTOR.getImage()).isEqualTo("floci/floci-az");
        assertThat(FlociAzContainer.DESCRIPTOR.getPort()).isEqualTo(4577);
        assertThat(FlociAzContainer.DESCRIPTOR.getEnvPrefix()).isEqualTo("FLOCI_AZ_");
        assertThat(FlociAzContainer.DESCRIPTOR.getHealthPath()).isEqualTo("/_floci/health");
        assertThat(FlociAzContainer.DESCRIPTOR.getResetPath()).isEqualTo("/_admin/reset");
        assertThat(FlociAzContainer.DESCRIPTOR.getLogLevelEnvVar()).isEqualTo("QUARKUS_LOG_CATEGORY__IO_FLOCI_AZ__LEVEL");
        assertThat(FlociAzContainer.DESCRIPTOR.getNetworkEnvVar()).isEqualTo("FLOCI_AZ_SERVICES_DOCKER_NETWORK");
        assertThat(FlociAzContainer.DESCRIPTOR.getResourceNamespaceEnvVar()).isEqualTo("FLOCI_AZ_DOCKER_RESOURCE_NAMESPACE");
        assertThat(FlociAzContainer.DESCRIPTOR.getTlsHttpsPortEnvVar()).isEqualTo("FLOCI_AZ_TLS_HTTPS_PORT");
    }

    @Test
    void shouldApplyDefaultEnv() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_AZ_DOCKER_ENDPOINT_MODE", "published")
                    .containsEntry("FLOCI_AZ_TLS_HTTPS_PORT", "0");
        }
    }

    @Test
    void shouldRequireDockerSocketForReferenceServicesByDefault() {
        // Service Bus, ACI, VM and Container Apps are mocked by default; Cosmos DB runs no engine sidecar and
        // Azure SQL has no data plane until the SQL Server EULA is accepted
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(configsRequiringDockerSocket(container)).containsExactlyInAnyOrder(
                    "AcrConfig", "AksConfig", "EventHubConfig", "FunctionsConfig", "MariaDbConfig",
                    "MySqlConfig", "PostgresConfig", "RedisConfig");
        }
    }

    @Test
    void shouldRequireDockerSocketForAllReferenceServicesWhenTheySpawnContainers() {
        try (FlociAzContainer container = new FlociAzContainer()
                .withServiceBusConfig(c -> c.mocked(false))
                .withAciConfig(c -> c.mocked(false))
                .withVmConfig(c -> c.mocked(false))
                .withContainerAppsConfig(c -> c.mocked(false))
                .withCosmosConfig(c -> c.mongodb(m -> m.enabled(true)))
                .withSqlConfig(c -> c.acceptEula("Y"))) {
            assertThat(configsRequiringDockerSocket(container)).containsExactlyInAnyOrder(
                    "AciConfig", "AcrConfig", "AksConfig", "ContainerAppsConfig", "CosmosConfig", "EventHubConfig",
                    "FunctionsConfig", "MariaDbConfig", "MySqlConfig", "PostgresConfig", "RedisConfig",
                    "ServiceBusConfig", "SqlConfig", "VmConfig");
        }
    }

    @Test
    void shouldNotRequireDockerSocketWhenAllServicesAreDisabled() {
        try (FlociAzContainer container = new FlociAzContainer().disableAllServices()) {
            assertThat(serviceConfigs(container)).isNotEmpty();
            assertThat(configsRequiringDockerSocket(container)).isEmpty();
        }
    }
}
