package io.floci.testcontainers.az;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlociAzContainerTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociAzContainer.DESCRIPTOR);

    @Test
    void shouldCreateContainerWithDefaultImage() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getDockerImageName()).isEqualTo("floci/floci-az:latest");
        }
    }

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new FlociAzContainer(DockerImageName.parse("floci/floci:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldExposeFlociAzPort() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getPort()).isEqualTo(FlociAzContainer.PORT);
            assertThat(container.getExposedPorts()).contains(FlociAzContainer.PORT);
        }
    }

    @Test
    void shouldReturnDefaultStorageAccount() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getAccountName()).isEqualTo("devstoreaccount1");
            assertThat(container.getAccountKey())
                    .isEqualTo("Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==");
        }
    }

    @Test
    void shouldReturnConfiguredKeyOfDefaultStorageAccount() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            container.withAuthConfig(c -> c.storageAccountKey("devstoreaccount1", "bXkta2V5"));

            assertThat(container.getAccountKey()).isEqualTo("bXkta2V5");
        }
    }

    @Test
    void shouldReturnSubscriptionIdOfArmConfig() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getSubscriptionId()).isEqualTo("00000000-0000-0000-0000-000000000001");

            container.withArmConfig(c -> c.defaultSubscriptionId("22222222-2222-2222-2222-222222222222"));

            assertThat(container.getSubscriptionId()).isEqualTo("22222222-2222-2222-2222-222222222222");
        }
    }

    @Test
    void shouldReturnTenantIdOfEntraConfig() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getTenantId()).isEqualTo("00000000-0000-0000-0000-000000000002");

            container.withEntraConfig(c -> c.defaultTenantId("11111111-1111-1111-1111-111111111111"));

            assertThat(container.getTenantId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        }
    }

    @Test
    void shouldDisableAllServices() {
        try (FlociAzContainer container = new FlociAzContainer().disableAllServices()) {
            assertThat(List.<AbstractServiceConfig<?>>of(
                    container.getBlobConfig(),
                    container.getQueueConfig(),
                    container.getTableConfig(),
                    container.getFunctionsConfig(),
                    container.getAppConfigConfig(),
                    container.getSignalRConfig(),
                    container.getCosmosConfig(),
                    container.getKeyVaultConfig(),
                    container.getEventHubConfig(),
                    container.getSqlConfig(),
                    container.getPostgresConfig(),
                    container.getMySqlConfig(),
                    container.getMariaDbConfig(),
                    container.getServiceBusConfig(),
                    container.getAksConfig(),
                    container.getAciConfig(),
                    container.getVmConfig(),
                    container.getApimConfig(),
                    container.getRedisConfig(),
                    container.getAcrConfig(),
                    container.getMonitorConfig(),
                    container.getEntraConfig(),
                    container.getGraphConfig(),
                    container.getArmConfig(),
                    container.getNetworkConfig(),
                    container.getEventGridConfig(),
                    container.getManagedIdentityConfig(),
                    container.getContainerAppsConfig(),
                    container.getPolicyConfig(),
                    container.getEmailConfig()
            )).noneMatch(AbstractServiceConfig::isEnabled);
        }
    }

    @Test
    void shouldConfigureLogLevel() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);

            container.withLogLevel(Level.DEBUG);

            assertThat(container.getLogLevel()).isEqualTo(Level.DEBUG);
            assertThat(container.getEnvMap()).containsEntry("QUARKUS_LOG_CATEGORY__IO_FLOCI_AZ__LEVEL", "DEBUG");
        }
    }

    @Test
    void shouldConfigureDedicatedNetwork() {
        try (FlociAzContainer container = new FlociAzContainer()) {
            container.withDedicatedNetwork();

            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_AZ_SERVICES_DOCKER_NETWORK", container.getDedicatedNetworkName());
        }
    }

    @Test
    @Tag("integration")
    void shouldStartContainerAndServeEndpoints() throws Exception {
        try (FlociAzContainer container = new FlociAzContainer(IMAGE).withLogLevel(Level.DEBUG)) {
            container.start();

            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(container.getEndpoint() + "/_floci/health")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("\"status\":\"UP\"");
            assertThat(container.getLogs()).contains("DEBUG");

            String endpoint = "http://" + container.getHost() + ":" + container.getMappedPort(FlociAzContainer.PORT);
            assertThat(container.getEndpoint()).isEqualTo(endpoint);
            assertThat(container.getBlobEndpoint()).isEqualTo(endpoint + "/devstoreaccount1");
            assertThat(container.getQueueEndpoint()).isEqualTo(endpoint + "/devstoreaccount1-queue");
            assertThat(container.getTableEndpoint()).isEqualTo(endpoint + "/devstoreaccount1-table");
            assertThat(container.getStorageConnectionString()).isEqualTo(
                    "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=" + container.getAccountKey()
                            + ";BlobEndpoint=" + endpoint + "/devstoreaccount1"
                            + ";QueueEndpoint=" + endpoint + "/devstoreaccount1-queue"
                            + ";TableEndpoint=" + endpoint + "/devstoreaccount1-table;");
        }
    }
}
