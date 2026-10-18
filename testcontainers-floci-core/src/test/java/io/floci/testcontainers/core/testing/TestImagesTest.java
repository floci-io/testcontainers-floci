package io.floci.testcontainers.core.testing;

import io.floci.testcontainers.core.CloudDescriptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestImagesTest {

    private String callerOverride;

    @BeforeEach
    void clearOverride() {
        callerOverride = System.clearProperty(TestImages.TAG_OVERRIDE_PROPERTY);
    }

    @AfterEach
    void restoreOverride() {
        if (callerOverride == null) {
            System.clearProperty(TestImages.TAG_OVERRIDE_PROPERTY);
        } else {
            System.setProperty(TestImages.TAG_OVERRIDE_PROPERTY, callerOverride);
        }
    }

    @Test
    void shouldReturnPinnedImageOfEveryCloud() {
        for (String image : new String[]{"floci/floci", "floci/floci-az", "floci/floci-gcp", "floci/floci-oci"}) {
            assertThat(TestImages.pinnedImage(descriptor(image)))
                    .startsWith(image + ":")
                    .doesNotEndWith(":latest")
                    .doesNotEndWith(":nightly");
        }
    }

    @Test
    void shouldReplaceTagWithOverride() {
        System.setProperty(TestImages.TAG_OVERRIDE_PROPERTY, "nightly");

        assertThat(TestImages.pinnedImage(descriptor("floci/floci-az"))).isEqualTo("floci/floci-az:nightly");
    }

    @Test
    void shouldRejectUnlistedImage() {
        assertThatThrownBy(() -> TestImages.pinnedImage(descriptor("floci/floci-unknown")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("floci/floci-unknown");
    }

    private static CloudDescriptor descriptor(String image) {
        return CloudDescriptor.builder()
                .name("test")
                .image(image)
                .port(1)
                .envPrefix("FLOCI_TEST_")
                .healthPath("/health")
                .resetPath("/reset")
                .logLevelEnvVar("LOG")
                .networkEnvVar("NET")
                .resourceNamespaceEnvVar("NS")
                .tlsHttpsPortEnvVar("HTTPS")
                .build();
    }
}
