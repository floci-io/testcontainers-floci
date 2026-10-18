package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseAuthServiceTest extends AbstractServiceTest {

    private static final String ACCOUNTS = "/identitytoolkit.googleapis.com/v1/accounts";

    private static final HttpClient REST_CLIENT = HttpClient.newHttpClient();

    @Test
    void shouldSignUpAndSignInWithPassword() {
        String credentials = """
                {"email": "%s@example.com", "password": "secret123", "returnSecureToken": true}
                """.formatted(uniqueName("user"));

        HttpResponse<String> signUp = post(ACCOUNTS + ":signUp?key=fake-api-key", credentials);
        HttpResponse<String> signIn = post(ACCOUNTS + ":signInWithPassword?key=fake-api-key", credentials);

        assertThat(signUp.statusCode()).as(signUp.body()).isEqualTo(200);
        assertThat(signUp.body()).contains("\"localId\"", "\"idToken\"");
        assertThat(signIn.statusCode()).as(signIn.body()).isEqualTo(200);
        assertThat(signIn.body()).contains("\"idToken\"");
    }

    /**
     * Sends a POST request with a JSON body to the shared container. Signing up and signing in with a password are
     * client-side Identity Toolkit calls that the Firebase Admin SDK does not offer.
     */
    private static HttpResponse<String> post(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(floci.getEndpoint() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            return REST_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("REST call POST " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("REST call POST " + path + " interrupted", e);
        }
    }
}
