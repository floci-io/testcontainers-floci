package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmulatorEnvServiceTest extends AbstractServiceTest {

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
                Map.entry("GOOGLE_CLOUD_PROJECT", projectId()));
    }

    @Test
    void shouldServeStorageOnStorageEmulatorHost() throws Exception {
        String storageHost = floci.getEmulatorEnv().get("STORAGE_EMULATOR_HOST");

        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(storageHost + "/storage/v1/b?project=" + projectId())).build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    }
}
