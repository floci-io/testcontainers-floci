package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerS3ServiceTest extends AbstractFlociContainerServiceTest {

    static S3Client s3;

    @BeforeAll
    static void setUp() {
        s3 = client(S3Client.builder().forcePathStyle(true));
    }

    @Test
    void shouldCreateAndListBucket() {
        String bucketName = "test-bucket-" + System.currentTimeMillis();

        s3.createBucket(b -> b.bucket(bucketName));

        List<String> bucketNames = s3.listBuckets().buckets().stream()
                .map(Bucket::name)
                .toList();

        assertThat(bucketNames).contains(bucketName);
    }

    @Test
    void shouldPutAndGetObject() {
        String bucketName = "test-objects-" + System.currentTimeMillis();
        s3.createBucket(b -> b.bucket(bucketName));

        String key = "hello.txt";
        String content = "Hello from Floci!";

        s3.putObject(
                b -> b.bucket(bucketName).key(key),
                RequestBody.fromString(content)
        );

        String retrieved = s3.getObjectAsBytes(b -> b.bucket(bucketName).key(key))
                .asUtf8String();

        assertThat(retrieved).isEqualTo(content);
    }

}
