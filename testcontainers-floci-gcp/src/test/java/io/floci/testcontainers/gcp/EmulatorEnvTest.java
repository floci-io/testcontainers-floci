package io.floci.testcontainers.gcp;

import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link FlociGcpContainer#getEmulatorEnv()} against a running container.
 */
@Tag("integration")
class EmulatorEnvTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociGcpContainer.DESCRIPTOR);

    private static FlociGcpContainer floci;

    @BeforeAll
    static void startContainer() {
        floci = new FlociGcpContainer(IMAGE);
        floci.start();
    }

    @AfterAll
    static void stopContainer() {
        if (floci != null) {
            floci.stop();
        }
    }

    @Test
    void shouldPointClientLibrariesAtContainer() {
        String emulatorHost = floci.getEmulatorHost();

        assertThat(floci.getEmulatorEnv()).containsExactly(
                Map.entry("PUBSUB_EMULATOR_HOST", emulatorHost),
                Map.entry("FIRESTORE_EMULATOR_HOST", emulatorHost),
                Map.entry("DATASTORE_EMULATOR_HOST", emulatorHost),
                Map.entry("SECRET_MANAGER_EMULATOR_HOST", emulatorHost),
                Map.entry("FIREBASE_AUTH_EMULATOR_HOST", emulatorHost),
                Map.entry("LOGGING_EMULATOR_HOST", emulatorHost),
                Map.entry("KMS_EMULATOR_HOST", emulatorHost),
                Map.entry("STORAGE_EMULATOR_HOST", floci.getEndpoint()),
                Map.entry("STORAGE_EMULATOR_HOST_GRPC", emulatorHost),
                Map.entry("GOOGLE_CLOUD_PROJECT", floci.getProjectId()));
    }

    @Test
    void shouldServeStorageOnStorageEmulatorHost() throws Exception {
        String storageHost = floci.getEmulatorEnv().get("STORAGE_EMULATOR_HOST");

        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(storageHost + "/storage/v1/b?project=" + floci.getProjectId()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    }
}
