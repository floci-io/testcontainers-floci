package io.floci.testcontainers.az.services;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpPipelineNextPolicy;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.jdk.httpclient.JdkHttpClientBuilder;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.floci.testcontainers.az.FlociAzContainer;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.Tag;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.UUID;

/**
 * Base class for Floci Azure service integration tests. Provides a shared {@link FlociAzContainer}
 * singleton (started once per JVM) with TLS enabled, since several Azure SDKs only talk HTTPS.
 */
@Tag("integration")
abstract class AbstractServiceTest {

    // The emulator image pinned in .github/docker-images.txt
    protected static final String IMAGE = TestImages.pinnedImage(FlociAzContainer.DESCRIPTOR);

    private static final boolean DEBUG_LOGGING = false;

    protected static final FlociAzContainer floci;

    protected static final String SUBSCRIPTION_ID = "00000000-0000-0000-0000-000000000001";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final java.net.http.HttpClient REST_CLIENT = java.net.http.HttpClient.newHttpClient();

    private static SSLContext sslContext;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociAzContainer(IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociAzContainer(IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.withTlsConfig(c -> c.enabled(true))
                .start();
    }

    /**
     * Returns an Azure SDK HTTP client that trusts the TLS certificate of the Floci Azure container, for SDKs that
     * require {@code https://} endpoints (see {@link FlociAzContainer#getHttpsEndpoint()}).
     */
    protected static HttpClient httpsClient() {
        return new JdkHttpClientBuilder(java.net.http.HttpClient.newBuilder().sslContext(sslContext())).build();
    }

    /**
     * Returns a pipeline policy that redirects requests to URLs Floci Azure generated from its own base URL
     * ({@code http://localhost:4577}, e.g. long-running-operation polling URLs) to the HTTPS endpoint of the
     * container, since the container port is mapped to a random host port.
     */
    protected static HttpPipelinePolicy redirectFlociBaseUrlPolicy() {
        return new RedirectFlociBaseUrlPolicy();
    }

    /**
     * Returns an SSL context that trusts the TLS certificate of the Floci Azure container.
     */
    protected static synchronized SSLContext sslContext() {
        if (sslContext == null) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                int index = 0;
                for (Certificate certificate : CertificateFactory.getInstance("X.509").generateCertificates(
                        new ByteArrayInputStream(floci.getTlsCertificate().getBytes(StandardCharsets.UTF_8)))) {
                    trustStore.setCertificateEntry("floci-az-" + index++, certificate);
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            } catch (GeneralSecurityException | IOException e) {
                throw new IllegalStateException("Failed to trust the TLS certificate of Floci Azure", e);
            }
        }
        return sslContext;
    }

    /**
     * Creates a new resource group in the default subscription and returns its ARM path
     * ({@code /subscriptions/{id}/resourceGroups/{name}}).
     */
    protected static String createResourceGroup() {
        String path = "/subscriptions/" + SUBSCRIPTION_ID + "/resourceGroups/rg-" + UUID.randomUUID().toString().substring(0, 8);
        RestResponse response = rest("PUT", path + "?api-version=2021-04-01", "{\"location\":\"eastus\"}");
        if (!response.isSuccessful()) {
            throw new IllegalStateException("Failed to create resource group: " + response);
        }
        return path;
    }

    /**
     * Sends a plain REST request (e.g. to the ARM management plane) to the Floci Azure container.
     *
     * @param method the HTTP method
     * @param path   the path including query string, relative to {@link FlociAzContainer#getEndpoint()}
     * @param body   the JSON request body, or {@code null} for none
     */
    protected static RestResponse rest(String method, String path, String body) {
        return rest(floci, method, path, body);
    }

    /**
     * Same as {@link #rest(String, String, String)}, but against the given container instead of the shared one.
     */
    protected static RestResponse rest(FlociAzContainer container, String method, String path, String body) {
        return rest(container, method, path, body, "test-token");
    }

    /**
     * Same as {@link #rest(FlociAzContainer, String, String, String)}, but with the given bearer token.
     */
    protected static RestResponse rest(FlociAzContainer container, String method, String path, String body, String bearerToken) {
        return rest(container, method, path, body, bearerToken, "application/json");
    }

    /**
     * Posts an {@code application/x-www-form-urlencoded} body (e.g. an OAuth2 token request) to the given container.
     */
    protected static RestResponse restForm(FlociAzContainer container, String path, String formBody) {
        return rest(container, "POST", path, formBody, "test-token", "application/x-www-form-urlencoded");
    }

    private static RestResponse rest(FlociAzContainer container, String method, String path, String body, String bearerToken,
                                     String contentType) {
        java.net.http.HttpRequest.Builder request = java.net.http.HttpRequest.newBuilder(URI.create(container.getEndpoint() + path))
                .header("Authorization", "Bearer " + bearerToken);
        if (body != null) {
            request.header("Content-Type", contentType)
                    .method(method, java.net.http.HttpRequest.BodyPublishers.ofString(body));
        } else {
            request.method(method, java.net.http.HttpRequest.BodyPublishers.noBody());
        }

        try {
            java.net.http.HttpResponse<String> response = REST_CLIENT.send(request.build(),
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            return new RestResponse(response.statusCode(), response.body());
        } catch (IOException e) {
            throw new IllegalStateException("REST call " + method + " " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("REST call " + method + " " + path + " interrupted", e);
        }
    }

    /**
     * Status and body of a {@link #rest(String, String, String)} call.
     */
    protected record RestResponse(int status, String body) {

        boolean isSuccessful() {
            return status >= 200 && status < 300;
        }

        JsonNode json() {
            try {
                return MAPPER.readTree(body);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Response is not JSON: " + body, e);
            }
        }
    }

    private static final class RedirectFlociBaseUrlPolicy implements HttpPipelinePolicy {

        @Override
        public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
            URL url = context.getHttpRequest().getUrl();
            if ("localhost".equals(url.getHost()) && url.getPort() == FlociAzContainer.PORT) {
                try {
                    URI endpoint = URI.create(floci.getHttpsEndpoint());
                    context.getHttpRequest().setUrl(
                            new URL(endpoint.getScheme(), endpoint.getHost(), endpoint.getPort(), url.getFile()));
                } catch (MalformedURLException e) {
                    return Mono.error(e);
                }
            }
            return next.process();
        }
    }
}
