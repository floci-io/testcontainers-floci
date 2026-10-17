package io.floci.testcontainers.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

class CloudDescriptorTest {

    @Test
    void shouldExposeAllProperties() {
        CloudDescriptor descriptor = TestFlociContainer.DESCRIPTOR;

        assertThat(descriptor.getName()).isEqualTo("test");
        assertThat(descriptor.getImage()).isEqualTo("floci/floci-test");
        assertThat(descriptor.getPort()).isEqualTo(TestFlociContainer.PORT);
        assertThat(descriptor.getEnvPrefix()).isEqualTo("FLOCI_TEST_");
        assertThat(descriptor.getHealthPath()).isEqualTo(TestFlociContainer.HEALTH_PATH);
        assertThat(descriptor.getResetPath()).isEqualTo("/_floci-test/state/reset");
        assertThat(descriptor.getLogLevelEnvVar()).isEqualTo(TestFlociContainer.LOG_LEVEL_ENV_VAR);
        assertThat(descriptor.getNetworkEnvVar()).isEqualTo(TestFlociContainer.DOCKER_NETWORK_ENV_VAR);
        assertThat(descriptor.getResourceNamespaceEnvVar()).isEqualTo(TestFlociContainer.RESOURCE_NAMESPACE_ENV_VAR);
        assertThat(descriptor.getTlsHttpsPortEnvVar()).isEqualTo("FLOCI_TEST_TLS_HTTPS_PORT");
        assertThat(descriptor.getDefaultEnv()).containsExactly(entry("FLOCI_TEST_TLS_HTTPS_PORT", "0"));
    }

    @Test
    void shouldDefaultToEmptyDefaultEnv() {
        CloudDescriptor descriptor = minimalBuilder().build();

        assertThat(descriptor.getDefaultEnv()).isEmpty();
    }

    @Test
    void shouldKeepDefaultEnvUnmodifiable() {
        CloudDescriptor descriptor = TestFlociContainer.DESCRIPTOR;

        assertThatThrownBy(() -> descriptor.getDefaultEnv().put("X", "Y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRequireAllProperties() {
        assertThatThrownBy(() -> minimalBuilder().resetPath(null).build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("resetPath");
        assertThatThrownBy(() -> minimalBuilder().port(0).build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static CloudDescriptor.Builder minimalBuilder() {
        return CloudDescriptor.builder()
                .name("x")
                .image("floci/floci-x")
                .port(1)
                .envPrefix("FLOCI_X_")
                .healthPath("/health")
                .resetPath("/reset")
                .logLevelEnvVar("LOG")
                .networkEnvVar("NET")
                .resourceNamespaceEnvVar("NS")
                .tlsHttpsPortEnvVar("HTTPS");
    }
}
