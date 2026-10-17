package io.floci.testcontainers.oci;

import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;
import io.floci.testcontainers.core.testing.TestImages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class FlociOciContainerAuthTest {

    private static final String IMAGE = TestImages.pinnedImage(FlociOciContainer.DESCRIPTOR);

    private static FlociOciContainer floci;

    @BeforeAll
    static void startContainer() {
        floci = new FlociOciContainer(IMAGE)
                .withAuthConfig(c -> c.requireSignature(true));
        floci.start();
    }

    @AfterAll
    static void stopContainer() {
        floci.stop();
    }

    @Test
    void shouldAcceptSignedRequest() throws Exception {
        URI uri = namespaceUri();
        RequestSigner signer = DefaultRequestSigner.createRequestSigner(TestAuthenticationProviders.forContainer(floci));
        Map<String, String> signedHeaders = signer.signRequest(uri, "GET", new HashMap<>(), null);

        HttpRequest.Builder request = HttpRequest.newBuilder(uri);
        // the host header is set by the HTTP client itself (and restricted), but it is part of the signature
        signedHeaders.forEach((name, value) -> {
            if (!"host".equalsIgnoreCase(name)) {
                request.header(name, value);
            }
        });

        HttpResponse<String> response = send(request.build());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(floci.getNamespace());
    }

    @Test
    void shouldRejectUnsignedRequest() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(namespaceUri()).build());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    private static URI namespaceUri() {
        return URI.create(floci.getEndpoint() + "/n/");
    }

    private static HttpResponse<String> send(HttpRequest request) throws Exception {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
