package io.floci.testcontainers.gcp;

import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlociGcpContainerTlsTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociGcpContainer.DESCRIPTOR);

    @Test
    void shouldStoreTlsConfigOnContainer() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            container.withTlsConfig(c -> c.enabled(true));

            assertThat(container.getTlsConfig().isEnabled()).isTrue();
        }
    }

    @Test
    void shouldKeepTlsConfigWhenAllServicesAreDisabled() {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            container.withTlsConfig(c -> c.enabled(true)).disableAllServices();

            assertThat(container.getEnvMap()).containsEntry("FLOCI_GCP_TLS_ENABLED", "true");
        }
    }

    @Test
    @Tag("integration")
    void shouldServeHttpsWithTrustedCertificate() throws Exception {
        try (FlociGcpContainer container = new FlociGcpContainer(IMAGE)
                .withTlsConfig(c -> c.enabled(true))) {
            container.start();

            HttpClient httpsClient = HttpClient.newBuilder()
                    .sslContext(sslContextTrusting(container.getTlsCertificate()))
                    .build();
            HttpResponse<String> response = httpsClient.send(
                    HttpRequest.newBuilder(URI.create(container.getHttpsEndpoint() + "/_floci-gcp/health")).build(),
                    HttpResponse.BodyHandlers.ofString());

            assertThat(container.getHttpsEndpoint()).startsWith("https://");
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Test
    @Tag("integration")
    void shouldFailToFetchCertificateWhenTlsIsDisabled() {
        try (FlociGcpContainer container = new FlociGcpContainer(IMAGE).disableAllServices()) {
            container.start();

            assertThatThrownBy(container::getTlsCertificate)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("TLS is not enabled");
        }
    }

    private static SSLContext sslContextTrusting(String pem) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        int index = 0;
        for (Certificate certificate : CertificateFactory.getInstance("X.509")
                .generateCertificates(new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)))) {
            trustStore.setCertificateEntry("floci-gcp-" + index++, certificate);
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
