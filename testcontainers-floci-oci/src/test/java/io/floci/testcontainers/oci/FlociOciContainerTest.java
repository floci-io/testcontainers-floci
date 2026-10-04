package io.floci.testcontainers.oci;

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

class FlociOciContainerTest {

    private static final String NIGHTLY_IMAGE = "floci/floci-oci:nightly";

    @Test
    void shouldCreateContainerWithDefaultImage() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(container.getDockerImageName()).isEqualTo("floci/floci-oci:latest");
        }
    }

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new FlociOciContainer(DockerImageName.parse("floci/floci:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldExposeFlociOciPort() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(container.getPort()).isEqualTo(FlociOciContainer.PORT);
            assertThat(container.getExposedPorts()).contains(FlociOciContainer.PORT);
        }
    }

    @Test
    void shouldApplyDefaultRootProperties() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(container.getRegion()).isEqualTo("us-ashburn-1");
            assertThat(container.getRealm()).isEqualTo("oc1");
            assertThat(container.getTenancyId())
                    .isEqualTo("ocid1.tenancy.oc1..flocilocaltenancy0000000000000000000000000000000000000000");
            assertThat(container.getNamespace()).isEqualTo("floci-local");
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_OCI_DEFAULT_REGION", "us-ashburn-1")
                    .containsEntry("FLOCI_OCI_DEFAULT_REALM", "oc1")
                    .containsEntry("FLOCI_OCI_DEFAULT_TENANCY_ID",
                            "ocid1.tenancy.oc1..flocilocaltenancy0000000000000000000000000000000000000000")
                    .containsEntry("FLOCI_OCI_DEFAULT_NAMESPACE", "floci-local");
        }
    }

    @Test
    void shouldOverrideRootProperties() {
        try (FlociOciContainer container = new FlociOciContainer()
                .withRegion("eu-frankfurt-1")
                .withRealm("oc2")
                .withTenancyId("ocid1.tenancy.oc2..mytenancy")
                .withNamespace("my-namespace")) {
            assertThat(container.getRegion()).isEqualTo("eu-frankfurt-1");
            assertThat(container.getRealm()).isEqualTo("oc2");
            assertThat(container.getTenancyId()).isEqualTo("ocid1.tenancy.oc2..mytenancy");
            assertThat(container.getNamespace()).isEqualTo("my-namespace");
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_OCI_DEFAULT_REGION", "eu-frankfurt-1")
                    .containsEntry("FLOCI_OCI_DEFAULT_REALM", "oc2")
                    .containsEntry("FLOCI_OCI_DEFAULT_TENANCY_ID", "ocid1.tenancy.oc2..mytenancy")
                    .containsEntry("FLOCI_OCI_DEFAULT_NAMESPACE", "my-namespace");
        }
    }

    @Test
    void shouldDisableAllServices() {
        try (FlociOciContainer container = new FlociOciContainer().disableAllServices()) {
            assertThat(List.<AbstractServiceConfig<?>>of(
                    container.getIdentityConfig(),
                    container.getObjectStorageConfig(),
                    container.getQueueConfig(),
                    container.getKmsConfig(),
                    container.getVaultConfig(),
                    container.getStreamingConfig(),
                    container.getFunctionsConfig()
            )).noneMatch(AbstractServiceConfig::isEnabled);
        }
    }

    @Test
    void shouldConfigureLogLevel() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);

            container.withLogLevel(Level.DEBUG);

            assertThat(container.getLogLevel()).isEqualTo(Level.DEBUG);
            assertThat(container.getEnvMap()).containsEntry("QUARKUS_LOG_CATEGORY__IO_FLOCI_OCI__LEVEL", "DEBUG");
        }
    }

    @Test
    void shouldConfigureDedicatedNetwork() {
        try (FlociOciContainer container = new FlociOciContainer()) {
            container.withDedicatedNetwork();

            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_OCI_SERVICES_DOCKER_NETWORK", container.getDedicatedNetworkName());
        }
    }

    @Test
    void shouldStartContainerAndServeEndpoints() throws Exception {
        try (FlociOciContainer container = new FlociOciContainer(NIGHTLY_IMAGE).withLogLevel(Level.DEBUG)) {
            container.start();

            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(container.getEndpoint() + "/_floci-oci/health")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("\"services\"");
            assertThat(container.getLogs()).contains("DEBUG");
            assertThat(container.getEndpoint())
                    .isEqualTo("http://" + container.getHost() + ":" + container.getMappedPort(FlociOciContainer.PORT));
        }
    }

    @Test
    void shouldServeConfiguredNamespace() throws Exception {
        try (FlociOciContainer container = new FlociOciContainer(NIGHTLY_IMAGE).withNamespace("my-namespace")) {
            container.start();

            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(container.getEndpoint() + "/n/")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("my-namespace");
        }
    }
}
