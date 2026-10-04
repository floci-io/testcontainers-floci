package io.floci.testcontainers.gcp;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
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

class FlociGcpContainerTest {

    private static final String NIGHTLY_IMAGE = "floci/floci-gcp:nightly";

    @Test
    void shouldCreateContainerWithDefaultImage() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getDockerImageName()).isEqualTo("floci/floci-gcp:latest");
        }
    }

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new FlociGcpContainer(DockerImageName.parse("floci/floci-az:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldExposeFlociGcpPort() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getPort()).isEqualTo(FlociGcpContainer.PORT);
            assertThat(container.getExposedPorts()).contains(FlociGcpContainer.PORT);
        }
    }

    @Test
    void shouldReturnDefaultProjectId() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getProjectId()).isEqualTo("floci-local");
            assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_GCP_DEFAULT_PROJECT_ID");
        }
    }

    @Test
    void shouldConfigureProjectId() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            container.withProjectId("my-project");

            assertThat(container.getProjectId()).isEqualTo("my-project");
            assertThat(container.getEnvMap()).containsEntry("FLOCI_GCP_DEFAULT_PROJECT_ID", "my-project");
        }
    }

    @Test
    void shouldDisableAllServices() {
        try (FlociGcpContainer container = new FlociGcpContainer().disableAllServices()) {
            assertThat(List.<AbstractServiceConfig<?>>of(
                    container.getComputeConfig(),
                    container.getGcsConfig(),
                    container.getPubSubConfig(),
                    container.getFirestoreConfig(),
                    container.getDatastoreConfig(),
                    container.getIamConfig(),
                    container.getIamCredentialsConfig(),
                    container.getStsConfig(),
                    container.getSecretManagerConfig(),
                    container.getLoggingConfig(),
                    container.getKmsConfig(),
                    container.getKafkaConfig(),
                    container.getCloudTasksConfig(),
                    container.getMonitoringConfig(),
                    container.getEventarcConfig(),
                    container.getServiceUsageConfig(),
                    container.getResourceManagerConfig(),
                    container.getFirebaseAuthConfig()
            )).noneMatch(AbstractServiceConfig::isEnabled);
        }
    }

    @Test
    void shouldConfigureLogLevel() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);

            container.withLogLevel(Level.DEBUG);

            assertThat(container.getLogLevel()).isEqualTo(Level.DEBUG);
            assertThat(container.getEnvMap()).containsEntry("QUARKUS_LOG_CATEGORY__IO_FLOCI_GCP__LEVEL", "DEBUG");
        }
    }

    @Test
    void shouldConfigureDedicatedNetwork() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            container.withDedicatedNetwork();

            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_GCP_SERVICES_DOCKER_NETWORK", container.getDedicatedNetworkName());
        }
    }

    @Test
    void shouldStartContainerAndServeEndpoints() throws Exception {
        try (FlociGcpContainer container = new FlociGcpContainer(NIGHTLY_IMAGE)
                .withLogLevel(Level.DEBUG)
                .withProjectId("my-project")) {
            container.start();

            HttpResponse<String> health = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(container.getEndpoint() + "/_floci-gcp/health")).build(),
                    HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> info = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(container.getEndpoint() + "/_floci-gcp/info")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(health.statusCode()).isEqualTo(200);
            assertThat(info.body()).contains("\"defaultProject\":\"my-project\"");
            // health checks are logged on DEBUG level by Floci GCP
            assertThat(container.getLogs()).contains("DEBUG");

            String emulatorHost = container.getHost() + ":" + container.getMappedPort(FlociGcpContainer.PORT);
            assertThat(container.getEmulatorHost()).isEqualTo(emulatorHost);
            assertThat(container.getEndpoint()).isEqualTo("http://" + emulatorHost);
        }
    }
}
