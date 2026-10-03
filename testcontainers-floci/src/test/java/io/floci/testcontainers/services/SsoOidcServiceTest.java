package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ssooidc.SsoOidcClient;
import software.amazon.awssdk.services.ssooidc.model.RegisterClientResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SsoOidcServiceTest extends AbstractServiceTest {

    static SsoOidcClient ssoOidc;

    @BeforeAll
    static void setUp() {
        ssoOidc = client(SsoOidcClient.builder());
    }

    @Test
    void shouldRegisterClient() {
        RegisterClientResponse response = ssoOidc.registerClient(b -> b
                .clientName("floci-tc-client")
                .clientType("public")
                .grantTypes("authorization_code", "refresh_token")
                .redirectUris("http://127.0.0.1:8400/callback")
                .scopes("sso:account:access"));

        assertThat(response.clientId()).isNotBlank();
        assertThat(response.clientSecret()).isNotBlank();
        assertThat(response.authorizationEndpoint()).endsWith("/authorize");
        assertThat(response.tokenEndpoint()).endsWith("/token");
    }

    @Test
    void shouldStartDeviceAuthorization() {
        RegisterClientResponse registeredClient = ssoOidc.registerClient(b -> b
                .clientName("floci-tc-device-client")
                .clientType("public")
                .grantTypes("urn:ietf:params:oauth:grant-type:device_code", "refresh_token"));

        var response = ssoOidc.startDeviceAuthorization(b -> b
                .clientId(registeredClient.clientId())
                .clientSecret(registeredClient.clientSecret())
                .startUrl("https://example.awsapps.com/start"));

        assertThat(response.deviceCode()).isNotBlank();
        assertThat(response.userCode()).isNotBlank();
        assertThat(response.verificationUriComplete()).contains("user_code=" + response.userCode());
    }
}
