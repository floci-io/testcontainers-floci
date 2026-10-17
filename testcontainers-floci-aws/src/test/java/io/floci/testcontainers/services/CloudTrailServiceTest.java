package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.DeleteTrailResponse;
import software.amazon.awssdk.services.cloudtrail.model.Trail;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
class CloudTrailServiceTest extends AbstractServiceTest {

    static CloudTrailClient cloudTrail;
    static S3Client s3;

    static String trailArn;

    private static final String BUCKET_NAME = "cloudtrail-logs-" + System.currentTimeMillis();
    private static final String TRAIL_NAME = "test-trail-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        cloudTrail = client(CloudTrailClient.builder());
        s3 = client(S3Client.builder().forcePathStyle(true));
    }

    @Test
    @Order(2)
    void shouldCreateTrail() {
        s3.createBucket(b -> b.bucket(BUCKET_NAME));

        trailArn = cloudTrail.createTrail(b -> b
                        .name(TRAIL_NAME)
                        .s3BucketName(BUCKET_NAME))
                .trailARN();

        assertThat(trailArn).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldDescribeTrail() {
        List<Trail> trails = cloudTrail.describeTrails(b -> b.trailNameList(TRAIL_NAME)).trailList();

        assertThat(trails).hasSize(1);
        assertThat(trails.get(0).name()).isEqualTo(TRAIL_NAME);
        assertThat(trails.get(0).s3BucketName()).isEqualTo(BUCKET_NAME);
    }

    @Test
    @Order(5)
    void shouldGetTrailStatus() {
        var status = cloudTrail.getTrailStatus(b -> b.name(TRAIL_NAME));

        assertThat(status).isNotNull();
    }

    @Test
    @Order(6)
    void shouldDeleteTrail() {
        DeleteTrailResponse response = cloudTrail.deleteTrail(b -> b.name(TRAIL_NAME));
        assertTrue(response.sdkHttpResponse().isSuccessful());
    }
}
