package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudcontrol.CloudControlClient;
import software.amazon.awssdk.services.cloudcontrol.model.ResourceDescription;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CloudControlServiceTest extends AbstractServiceTest {

    private static final String BUCKET_NAME = "cloud-control-bucket";

    static CloudControlClient cloudControl;

    @BeforeAll
    static void setUp() {
        cloudControl = client(CloudControlClient.builder());

        client(S3Client.builder().forcePathStyle(true)).createBucket(b -> b.bucket(BUCKET_NAME));
    }

    @Test
    void shouldListResources() {
        List<ResourceDescription> resources = cloudControl
                .listResources(b -> b.typeName("AWS::S3::Bucket"))
                .resourceDescriptions();

        assertThat(resources).anyMatch(r -> r.identifier().equals(BUCKET_NAME));
    }
}
