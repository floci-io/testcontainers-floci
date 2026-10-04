package io.floci.testcontainers.oci.services;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectStorageServiceTest extends AbstractServiceTest {

    private static ObjectStorageClient objectStorage;

    @BeforeAll
    static void createClient() {
        objectStorage = client(ObjectStorageClient.builder());
    }

    @AfterAll
    static void closeClient() {
        objectStorage.close();
    }

    @Test
    void shouldReturnConfiguredNamespace() {
        String namespace = objectStorage.getNamespace(GetNamespaceRequest.builder().build()).getValue();

        assertThat(namespace).isEqualTo(floci.getNamespace());
    }

    @Test
    void shouldPutAndGetObject() throws Exception {
        String namespace = floci.getNamespace();
        String bucket = "bucket-" + System.nanoTime();
        byte[] payload = "hello from testcontainers-floci".getBytes(StandardCharsets.UTF_8);

        objectStorage.createBucket(CreateBucketRequest.builder()
                .namespaceName(namespace)
                .createBucketDetails(CreateBucketDetails.builder()
                        .name(bucket)
                        .compartmentId(tenancyId())
                        .build())
                .build());
        objectStorage.putObject(PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucket)
                .objectName("greeting.txt")
                .contentType("text/plain")
                .opcMeta(Map.of("owner", "testcontainers"))
                .putObjectBody(new ByteArrayInputStream(payload))
                .contentLength((long) payload.length)
                .build());

        var listing = objectStorage.listObjects(ListObjectsRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucket)
                .build()).getListObjects();
        var object = objectStorage.getObject(GetObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucket)
                .objectName("greeting.txt")
                .build());

        assertThat(listing.getObjects()).extracting(o -> o.getName()).containsExactly("greeting.txt");
        try (InputStream content = object.getInputStream()) {
            assertThat(content.readAllBytes()).isEqualTo(payload);
        }
        assertThat(object.getOpcMeta()).containsEntry("owner", "testcontainers");
    }
}
