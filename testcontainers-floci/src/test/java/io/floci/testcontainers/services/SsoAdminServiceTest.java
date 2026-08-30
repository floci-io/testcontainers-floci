package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ssoadmin.SsoAdminClient;
import software.amazon.awssdk.services.ssoadmin.model.InstanceMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class SsoAdminServiceTest extends AbstractServiceTest {

    static SsoAdminClient ssoAdmin;

    @BeforeAll
    static void setUp() {
        ssoAdmin = client(SsoAdminClient.builder());
    }

    @Test
    void shouldListExactlyOneSeededInstance() {
        var response = ssoAdmin.listInstances(b -> {});

        assertThat(response.instances()).hasSize(1);
        InstanceMetadata instance = response.instances().get(0);
        assertThat(instance.instanceArn()).contains(":instance/ssoins-");
        assertThat(instance.identityStoreId()).startsWith("d-");
    }

    @Test
    void shouldReturnStableInstanceArnAcrossCalls() {
        String first = ssoAdmin.listInstances(b -> {}).instances().get(0).instanceArn();
        String second = ssoAdmin.listInstances(b -> {}).instances().get(0).instanceArn();

        assertThat(first).isEqualTo(second);
    }
}
