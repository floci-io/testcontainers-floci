package io.floci.testcontainers.gcp.services;

import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FirestoreServiceTest extends AbstractServiceTest {

    @Test
    void shouldWriteAndReadDocument() throws Exception {
        try (Firestore firestore = FirestoreOptions.newBuilder()
                .setProjectId(projectId())
                // the Firestore SDK uses a plaintext channel for hosts containing "localhost"
                .setHost(floci.getEmulatorHost())
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService()) {
            DocumentReference document = firestore.collection(uniqueName("users")).document("alice");

            document.set(Map.of("name", "Alice", "age", 30L)).get();
            DocumentSnapshot snapshot = document.get().get();

            assertThat(snapshot.exists()).isTrue();
            assertThat(snapshot.getString("name")).isEqualTo("Alice");
            assertThat(snapshot.getLong("age")).isEqualTo(30L);
        }
    }
}
