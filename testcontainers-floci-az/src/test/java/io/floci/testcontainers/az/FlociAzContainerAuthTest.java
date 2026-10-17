package io.floci.testcontainers.az;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class FlociAzContainerAuthTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociAzContainer.DESCRIPTOR);
    private static final String ACCOUNT = "myaccount";
    private static final String ACCOUNT_KEY = Base64.getEncoder().encodeToString("my-account-key".getBytes());
    private static final String OTHER_KEY = Base64.getEncoder().encodeToString("some-other-key".getBytes());

    private static FlociAzContainer floci;
    private static BlobClient blob;

    @BeforeAll
    static void startContainer() {
        floci = new FlociAzContainer(IMAGE)
                .withAuthConfig(c -> c.storageAccountKey(ACCOUNT, ACCOUNT_KEY));
        floci.start();

        blob = new BlobServiceClientBuilder()
                .endpoint(floci.getEndpoint() + "/" + ACCOUNT)
                .credential(new StorageSharedKeyCredential(ACCOUNT, ACCOUNT_KEY))
                .buildClient()
                .createBlobContainer("auth-" + UUID.randomUUID())
                .getBlobClient("hello.txt");
        blob.upload(BinaryData.fromString("hello"));
    }

    @AfterAll
    static void stopContainer() {
        floci.stop();
    }

    @Test
    void shouldAcceptSasSignedWithConfiguredAccountKey() {
        BlobClient sasClient = sasClient(ACCOUNT_KEY);

        assertThat(sasClient.downloadContent().toString()).isEqualTo("hello");
    }

    @Test
    void shouldRejectSasSignedWithOtherKey() {
        BlobClient sasClient = sasClient(OTHER_KEY);

        assertThatThrownBy(sasClient::downloadContent)
                .isInstanceOf(BlobStorageException.class)
                .satisfies(e -> assertThat(((BlobStorageException) e).getStatusCode()).isEqualTo(403));
    }

    private static BlobClient sasClient(String signingKey) {
        BlobClient signer = new BlobClientBuilder()
                .endpoint(blob.getBlobUrl())
                .credential(new StorageSharedKeyCredential(ACCOUNT, signingKey))
                .buildClient();
        String sas = signer.generateSas(new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusHours(1), new BlobSasPermission().setReadPermission(true)));

        return new BlobClientBuilder()
                .endpoint(blob.getBlobUrl() + "?" + sas)
                .buildClient();
    }
}
