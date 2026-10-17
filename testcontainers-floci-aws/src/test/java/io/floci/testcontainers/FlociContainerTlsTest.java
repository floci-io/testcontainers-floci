package io.floci.testcontainers;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerTlsTest {

    @Test
    void shouldStoreTlsConfigOnContainer() {
        try (FlociContainer container = new FlociContainer()) {
            container.withTlsConfig(c -> c.enabled(true));

            assertThat(container.getTlsConfig().isEnabled()).isTrue();
        }
    }

    @Test
    void shouldReachHealthCheckViaHttpsWhenTlsEnabled() throws Exception {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build();

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();

        try (FlociContainer container = new FlociContainer();
             CloseableHttpClient httpClient = HttpClients.custom()
                     .setConnectionManager(connectionManager)
                     .build()) {

            container.withTlsConfig(c -> c.enabled(true));
            container.start();

            String httpsUrl = String.format("https://%s:%d/_floci/health",
                    container.getHost(), container.getMappedPort(FlociContainer.PORT));

            httpClient.execute(new HttpGet(httpsUrl), response ->
                    assertThat(response.getCode()).isEqualTo(200)
            );
        }
    }
}
