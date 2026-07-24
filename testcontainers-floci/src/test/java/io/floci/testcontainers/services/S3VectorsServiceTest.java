package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.s3vectors.S3VectorsClient;
import software.amazon.awssdk.services.s3vectors.model.VectorBucketSummary;
import software.amazon.awssdk.utils.builder.SdkBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class S3VectorsServiceTest extends AbstractServiceTest {

    static S3VectorsClient s3Vectors;

    private static final String VECTOR_BUCKET_NAME = "test-vector-bucket-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        s3Vectors = client(S3VectorsClient.builder());
    }

    @Test
    @Order(1)
    void shouldListVectorBuckets() {
        List<VectorBucketSummary> buckets = s3Vectors.listVectorBuckets(SdkBuilder::build).vectorBuckets();

        assertThat(buckets).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateVectorBucket() {
        s3Vectors.createVectorBucket(b -> b.vectorBucketName(VECTOR_BUCKET_NAME));

        var response = s3Vectors.getVectorBucket(b -> b.vectorBucketName(VECTOR_BUCKET_NAME));
        assertThat(response.vectorBucket().vectorBucketName()).isEqualTo(VECTOR_BUCKET_NAME);
    }

    @Test
    @Order(3)
    void shouldListVectorBucketsContainsCreatedBucket() {
        List<VectorBucketSummary> buckets = s3Vectors.listVectorBuckets(SdkBuilder::build).vectorBuckets();

        assertThat(buckets).anyMatch(b -> b.vectorBucketName().equals(VECTOR_BUCKET_NAME));
    }

    @Test
    @Order(4)
    void shouldDeleteVectorBucket() {
        s3Vectors.deleteVectorBucket(b -> b.vectorBucketName(VECTOR_BUCKET_NAME));

        List<VectorBucketSummary> buckets = s3Vectors.listVectorBuckets(SdkBuilder::build).vectorBuckets();
        assertThat(buckets).noneMatch(b -> b.vectorBucketName().equals(VECTOR_BUCKET_NAME));
    }
}
