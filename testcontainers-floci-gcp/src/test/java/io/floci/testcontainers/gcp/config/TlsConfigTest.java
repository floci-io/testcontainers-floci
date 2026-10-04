package io.floci.testcontainers.gcp.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TlsConfigTest {

    @Test
    void shouldApplyDefaultTlsConfig() {
        TlsConfig config = TlsConfig.builder().build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getCertPath()).isEmpty();
        assertThat(config.getKeyPath()).isEmpty();
        assertThat(config.isSelfSigned()).isTrue();
        assertThat(config.getHttpsPort()).isEqualTo(443);
    }

    @Test
    void shouldApplyCustomTlsConfig() {
        TlsConfig config = TlsConfig.builder()
                .enabled(true)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .selfSigned(false)
                .httpsPort(0)
                .build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getCertPath()).contains("/certs/server.crt");
        assertThat(config.getKeyPath()).contains("/certs/server.key");
        assertThat(config.isSelfSigned()).isFalse();
        assertThat(config.getHttpsPort()).isZero();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_TLS_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_GCP_TLS_KEY_PATH")
                .doesNotContainKey("FLOCI_GCP_TLS_SELF_SIGNED")
                .doesNotContainKey("FLOCI_GCP_TLS_HTTPS_PORT");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder()
                .enabled(true)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .selfSigned(false)
                .httpsPort(0)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_TLS_ENABLED", "true")
                .containsEntry("FLOCI_GCP_TLS_CERT_PATH", "/certs/server.crt")
                .containsEntry("FLOCI_GCP_TLS_KEY_PATH", "/certs/server.key")
                .containsEntry("FLOCI_GCP_TLS_SELF_SIGNED", "false")
                .containsEntry("FLOCI_GCP_TLS_HTTPS_PORT", "0");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder()
                .enabled(false)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_TLS_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_GCP_TLS_KEY_PATH")
                .doesNotContainKey("FLOCI_GCP_TLS_SELF_SIGNED")
                .doesNotContainKey("FLOCI_GCP_TLS_HTTPS_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TlsConfig config = TlsConfig.builder()
                .enabled(true)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .selfSigned(false)
                .httpsPort(8443)
                .build();

        TlsConfig copy = config.toBuilder().build();

        assertThat(copy.isEnabled()).isTrue();
        assertThat(copy.getCertPath()).contains("/certs/server.crt");
        assertThat(copy.getKeyPath()).contains("/certs/server.key");
        assertThat(copy.isSelfSigned()).isFalse();
        assertThat(copy.getHttpsPort()).isEqualTo(8443);
    }
}
