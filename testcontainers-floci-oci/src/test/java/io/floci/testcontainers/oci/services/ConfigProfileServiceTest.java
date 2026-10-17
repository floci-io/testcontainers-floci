package io.floci.testcontainers.oci.services;

import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import io.floci.testcontainers.oci.OciConfigProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigProfileServiceTest extends AbstractServiceTest {

    @Test
    void shouldSignRequestsWithConfigFileAuthentication() throws Exception {
        OciConfigProfile profile = floci.getConfigProfile();
        ConfigFileAuthenticationDetailsProvider auth = new ConfigFileAuthenticationDetailsProvider(
                profile.writeConfigFile().toString(), profile.getName());

        try (ObjectStorageClient objectStorage = ObjectStorageClient.builder()
                .endpoint(floci.getEndpoint())
                .build(auth)) {
            String namespace = objectStorage.getNamespace(GetNamespaceRequest.builder().build()).getValue();

            assertThat(auth.getTenantId()).isEqualTo(floci.getTenancyId());
            assertThat(namespace).isEqualTo(floci.getNamespace());
        }
    }
}
