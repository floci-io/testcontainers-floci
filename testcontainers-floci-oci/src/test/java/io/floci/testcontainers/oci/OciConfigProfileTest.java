package io.floci.testcontainers.oci;

import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class OciConfigProfileTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociOciContainer.DESCRIPTOR);

    @Test
    void shouldDescribeContainer() {
        try (FlociOciContainer container = new FlociOciContainer()
                .withTenancyId("ocid1.tenancy.oc1..custom")
                .withRegion("eu-frankfurt-1")) {
            OciConfigProfile profile = container.getConfigProfile();

            assertThat(profile.getName()).isEqualTo("DEFAULT");
            assertThat(profile.getUser()).startsWith("ocid1.user.oc1..");
            assertThat(profile.getTenancy()).isEqualTo("ocid1.tenancy.oc1..custom");
            assertThat(profile.getRegion()).isEqualTo("eu-frankfurt-1");
            assertThat(profile.getFingerprint()).matches("([0-9a-f]{2}:){15}[0-9a-f]{2}");
            assertThat(profile.getKeyFile()).content()
                    .startsWith("-----BEGIN PRIVATE KEY-----\n")
                    .endsWith("-----END PRIVATE KEY-----\n");
        }
    }

    @Test
    void shouldReuseKeyPerContainer() {
        try (FlociOciContainer container = new FlociOciContainer();
             FlociOciContainer other = new FlociOciContainer()) {
            OciConfigProfile first = container.getConfigProfile();
            OciConfigProfile second = container.withRegion("eu-frankfurt-1").getConfigProfile();

            assertThat(second.getKeyFile()).isEqualTo(first.getKeyFile());
            assertThat(second.getFingerprint()).isEqualTo(first.getFingerprint());
            assertThat(second.getRegion()).isEqualTo("eu-frankfurt-1");
            assertThat(other.getConfigProfile().getFingerprint()).isNotEqualTo(first.getFingerprint());
        }
    }

    @Test
    void shouldRenderConfigFile(@TempDir Path directory) throws Exception {
        try (FlociOciContainer container = new FlociOciContainer()) {
            OciConfigProfile profile = container.getConfigProfile();

            Path configFile = profile.writeConfigFile(directory.resolve("config"));

            assertThat(Files.readString(configFile)).isEqualTo("[DEFAULT]\n"
                    + "user=" + profile.getUser() + "\n"
                    + "fingerprint=" + profile.getFingerprint() + "\n"
                    + "tenancy=" + container.getTenancyId() + "\n"
                    + "region=us-ashburn-1\n"
                    + "key_file=" + profile.getKeyFile().toAbsolutePath() + "\n");
        }
    }

    @Test
    @Tag("integration")
    void shouldSignRequestsWithConfigFileAuthentication() throws Exception {
        try (FlociOciContainer container = new FlociOciContainer(IMAGE)) {
            container.start();

            OciConfigProfile profile = container.getConfigProfile();
            ConfigFileAuthenticationDetailsProvider auth = new ConfigFileAuthenticationDetailsProvider(
                    profile.writeConfigFile().toString(), profile.getName());

            try (ObjectStorageClient objectStorage = ObjectStorageClient.builder()
                    .endpoint(container.getEndpoint())
                    .build(auth)) {
                String namespace = objectStorage.getNamespace(GetNamespaceRequest.builder().build()).getValue();

                assertThat(auth.getTenantId()).isEqualTo(container.getTenancyId());
                assertThat(namespace).isEqualTo(container.getNamespace());
            }
        }
    }
}
