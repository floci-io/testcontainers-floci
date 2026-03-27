package io.floci.testcontainers.spring;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class FlociAutoConfigurationTest {

    @Container
    @ServiceConnection
    static FlociContainer flociContainer = new FlociContainer();

    @Autowired
    private S3Client s3Client;

    @Test
    void shouldAutoConfigureS3ClientWithFlociEndpoint() {
        String bucketName = "test-bucket";
        s3Client.createBucket(b -> b.bucket(bucketName));

        var buckets = s3Client.listBuckets().buckets();
        assertThat(buckets).anyMatch(b -> b.name().equals(bucketName));
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
