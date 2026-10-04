package io.floci.testcontainers.gcp.services;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class GcsServiceTest extends AbstractServiceTest {

    @Test
    void shouldUploadAndDownloadObject() throws Exception {
        try (Storage storage = StorageOptions.newBuilder()
                .setHost(floci.getEndpoint())
                .setProjectId(projectId())
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService()) {
            String bucket = storage.create(BucketInfo.of(uniqueName("bucket"))).getName();
            BlobId blobId = BlobId.of(bucket, "hello.txt");

            storage.create(BlobInfo.newBuilder(blobId).setContentType("text/plain").build(),
                    "Hello, Floci GCP!".getBytes(StandardCharsets.UTF_8));
            byte[] content = storage.readAllBytes(blobId);

            assertThat(new String(content, StandardCharsets.UTF_8)).isEqualTo("Hello, Floci GCP!");
        }
    }
}
