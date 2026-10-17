package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TlsConfigTest {

    @Test
    void shouldApplyDefaultTlsConfig() {
        TlsConfig config = TlsConfig.builder().build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isSelfSigned()).isTrue();
        assertThat(config.getCertPath()).isEmpty();
        assertThat(config.getKeyPath()).isEmpty();
        assertThat(config.getAwsHttpsPort()).isEqualTo(443);
    }

    @Test
    void shouldApplyCustomTlsConfig() {
        TlsConfig config = TlsConfig.builder()
                .enabled(true)
                .selfSigned(false)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .awsHttpsPort(8443)
                .build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isSelfSigned()).isFalse();
        assertThat(config.getCertPath()).contains("/certs/server.crt");
        assertThat(config.getKeyPath()).contains("/certs/server.key");
        assertThat(config.getAwsHttpsPort()).isEqualTo(8443);
    }

    @Test
    void shouldAllowDisablingAwsHttpsPort() {
        TlsConfig config = TlsConfig.builder()
                .awsHttpsPort(0)
                .build();
        assertThat(config.getAwsHttpsPort()).isZero();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_TLS_ENABLED", "false")
                .doesNotContainKey("FLOCI_TLS_SELF_SIGNED")
                .doesNotContainKey("FLOCI_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_TLS_KEY_PATH");
    }

    @Test
    void shouldApplyEnabledEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_TLS_ENABLED", "true")
                .containsEntry("FLOCI_TLS_SELF_SIGNED", "true")
                .containsEntry("FLOCI_TLS_AWS_HTTPS_PORT", "443")
                .doesNotContainKey("FLOCI_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_TLS_KEY_PATH");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder()
                .enabled(true)
                .selfSigned(false)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .awsHttpsPort(8443)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_TLS_ENABLED", "true")
                .containsEntry("FLOCI_TLS_SELF_SIGNED", "false")
                .containsEntry("FLOCI_TLS_CERT_PATH", "/certs/server.crt")
                .containsEntry("FLOCI_TLS_KEY_PATH", "/certs/server.key")
                .containsEntry("FLOCI_TLS_AWS_HTTPS_PORT", "8443");
    }

    @Test
    void shouldApplyDisabledAwsHttpsPortEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder()
                .enabled(true)
                .awsHttpsPort(0)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_TLS_AWS_HTTPS_PORT", "0");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TlsConfig config = TlsConfig.builder()
                .enabled(true)
                .selfSigned(false)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .awsHttpsPort(8443)
                .build();

        TlsConfig copy = config.toBuilder().build();

        assertThat(copy.isEnabled()).isTrue();
        assertThat(copy.isSelfSigned()).isFalse();
        assertThat(copy.getCertPath()).contains("/certs/server.crt");
        assertThat(copy.getKeyPath()).contains("/certs/server.key");
        assertThat(copy.getAwsHttpsPort()).isEqualTo(8443);
    }
}
