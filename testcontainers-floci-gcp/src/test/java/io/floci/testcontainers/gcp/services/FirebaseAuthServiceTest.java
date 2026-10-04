package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseAuthServiceTest extends AbstractServiceTest {

    private static final String ACCOUNTS = "/identitytoolkit.googleapis.com/v1/accounts";

    @Test
    void shouldSignUpAndSignInWithPassword() {
        String credentials = """
                {"email": "%s@example.com", "password": "secret123", "returnSecureToken": true}
                """.formatted(uniqueName("user"));

        RestResponse signUp = rest("POST", ACCOUNTS + ":signUp?key=fake-api-key", "application/json", credentials);
        RestResponse signIn = rest("POST", ACCOUNTS + ":signInWithPassword?key=fake-api-key", "application/json",
                credentials);

        assertThat(signUp.status()).as(signUp.body()).isEqualTo(200);
        assertThat(signUp.body()).contains("\"localId\"", "\"idToken\"");
        assertThat(signIn.status()).as(signIn.body()).isEqualTo(200);
        assertThat(signIn.body()).contains("\"idToken\"");
    }
}
