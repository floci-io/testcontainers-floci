package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class EfsServiceTest extends AbstractServiceTest {

    static EfsClient efs;

    static String fileSystemId;

    @BeforeAll
    static void setUp() {
        efs = client(EfsClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateFileSystem() {
        var response = efs.createFileSystem(b -> b
                .creationToken("floci-tc-" + System.currentTimeMillis())
                .encrypted(true));

        assertThat(response.fileSystemId()).startsWith("fs-");
        fileSystemId = response.fileSystemId();
    }

    @Test
    @Order(2)
    void shouldDescribeFileSystemById() {
        var response = efs.describeFileSystems(b -> b.fileSystemId(fileSystemId));

        assertThat(response.fileSystems())
                .extracting(FileSystemDescription::fileSystemId)
                .containsExactly(fileSystemId);
    }

    @Test
    @Order(3)
    void shouldDeleteFileSystem() {
        efs.deleteFileSystem(b -> b.fileSystemId(fileSystemId));

        var response = efs.describeFileSystems(b -> {});
        assertThat(response.fileSystems())
                .extracting(FileSystemDescription::fileSystemId)
                .doesNotContain(fileSystemId);
    }
}
