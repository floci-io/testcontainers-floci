package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.datasync.DataSyncClient;
import software.amazon.awssdk.services.datasync.model.LocationListEntry;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class DataSyncServiceTest extends AbstractServiceTest {

    private static final String ROLE_ARN = "arn:aws:iam::000000000000:role/datasync";

    static DataSyncClient dataSync;

    static String locationArn;

    @BeforeAll
    static void setUp() {
        dataSync = client(DataSyncClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateS3Location() {
        locationArn = dataSync.createLocationS3(b -> b
                .s3BucketArn("arn:aws:s3:::floci-tc-datasync")
                .subdirectory("/backups")
                .s3Config(c -> c.bucketAccessRoleArn(ROLE_ARN))).locationArn();

        assertThat(locationArn).contains(":location/loc-");
    }

    @Test
    @Order(2)
    void shouldDescribeAndListLocation() {
        assertThat(dataSync.describeLocationS3(b -> b.locationArn(locationArn)).s3Config().bucketAccessRoleArn())
                .isEqualTo(ROLE_ARN);
        assertThat(dataSync.listLocations(b -> {}).locations())
                .extracting(LocationListEntry::locationArn)
                .contains(locationArn);
    }

    @Test
    @Order(3)
    void shouldDeleteLocation() {
        dataSync.deleteLocation(b -> b.locationArn(locationArn));

        assertThat(dataSync.listLocations(b -> {}).locations())
                .extracting(LocationListEntry::locationArn)
                .doesNotContain(locationArn);
    }
}
