package io.floci.testcontainers.az.config;

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
        assertThat(config.getCaCertPath()).isEmpty();
        assertThat(config.getCaKeyPath()).isEmpty();
        assertThat(config.isSelfSigned()).isTrue();
        assertThat(config.isPublicHosts()).isFalse();
        assertThat(config.getHttpsPort()).isZero();
    }

    @Test
    void shouldApplyCustomTlsConfig() {
        TlsConfig config = customConfig();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getCertPath()).contains("/certs/server.crt");
        assertThat(config.getKeyPath()).contains("/certs/server.key");
        assertThat(config.getCaCertPath()).contains("/certs/rootCA.pem");
        assertThat(config.getCaKeyPath()).contains("/certs/rootCA-key.pem");
        assertThat(config.isSelfSigned()).isFalse();
        assertThat(config.isPublicHosts()).isTrue();
        assertThat(config.getHttpsPort()).isEqualTo(8443);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_TLS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_SELF_SIGNED")
                .doesNotContainKey("FLOCI_AZ_TLS_PUBLIC_HOSTS")
                .doesNotContainKey("FLOCI_AZ_TLS_HTTPS_PORT");
    }

    @Test
    void shouldApplyEnabledDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TlsConfig.builder().enabled(true).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_TLS_ENABLED", "true")
                .doesNotContainKey("FLOCI_AZ_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_KEY_PATH")
                .containsEntry("FLOCI_AZ_TLS_SELF_SIGNED", "true")
                .containsEntry("FLOCI_AZ_TLS_PUBLIC_HOSTS", "false")
                .containsEntry("FLOCI_AZ_TLS_HTTPS_PORT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_TLS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_TLS_CERT_PATH", "/certs/server.crt")
                .containsEntry("FLOCI_AZ_TLS_KEY_PATH", "/certs/server.key")
                .containsEntry("FLOCI_AZ_TLS_CA_CERT_PATH", "/certs/rootCA.pem")
                .containsEntry("FLOCI_AZ_TLS_CA_KEY_PATH", "/certs/rootCA-key.pem")
                .containsEntry("FLOCI_AZ_TLS_SELF_SIGNED", "false")
                .containsEntry("FLOCI_AZ_TLS_PUBLIC_HOSTS", "true")
                .containsEntry("FLOCI_AZ_TLS_HTTPS_PORT", "8443");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().toBuilder()
                .enabled(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_TLS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_TLS_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_CERT_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_CA_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_TLS_SELF_SIGNED")
                .doesNotContainKey("FLOCI_AZ_TLS_PUBLIC_HOSTS")
                .doesNotContainKey("FLOCI_AZ_TLS_HTTPS_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TlsConfig copy = customConfig().toBuilder().build();

        assertThat(copy.isEnabled()).isTrue();
        assertThat(copy.getCertPath()).contains("/certs/server.crt");
        assertThat(copy.getKeyPath()).contains("/certs/server.key");
        assertThat(copy.getCaCertPath()).contains("/certs/rootCA.pem");
        assertThat(copy.getCaKeyPath()).contains("/certs/rootCA-key.pem");
        assertThat(copy.isSelfSigned()).isFalse();
        assertThat(copy.isPublicHosts()).isTrue();
        assertThat(copy.getHttpsPort()).isEqualTo(8443);
    }

    private static TlsConfig customConfig() {
        return TlsConfig.builder()
                .enabled(true)
                .certPath("/certs/server.crt")
                .keyPath("/certs/server.key")
                .caCertPath("/certs/rootCA.pem")
                .caKeyPath("/certs/rootCA-key.pem")
                .selfSigned(false)
                .publicHosts(true)
                .httpsPort(8443)
                .build();
    }
}
