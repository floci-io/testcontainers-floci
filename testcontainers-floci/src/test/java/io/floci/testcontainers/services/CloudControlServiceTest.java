package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudcontrol.CloudControlClient;
import software.amazon.awssdk.services.cloudcontrol.model.ResourceDescription;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CloudControlServiceTest extends AbstractServiceTest {

    static CloudControlClient cloudControl;

    @BeforeAll
    static void setUp() {
        cloudControl = client(CloudControlClient.builder());
    }

    @Test
    void shouldListResources() {
        List<ResourceDescription> resources = cloudControl
                .listResources(b -> b.typeName("AWS::SQS::Queue"))
                .resourceDescriptions();

        assertThat(resources).isNotNull();
    }
}
