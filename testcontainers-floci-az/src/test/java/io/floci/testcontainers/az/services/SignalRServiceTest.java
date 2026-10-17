package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class SignalRServiceTest extends AbstractServiceTest {

    private static final String ACCESS_KEY = "bXktc2lnbmFsci1rZXk=";
    private static final String DEFAULT_ACCESS_KEY = "bG9jYWwtc2lnbmFsci1kZXZlbG9wbWVudC1rZXk=";

    private static FlociAzContainer signalRFloci;

    @BeforeAll
    static void startContainer() {
        signalRFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withSignalRConfig(c -> c.enabled(true).accessKey(ACCESS_KEY));
        signalRFloci.start();
    }

    @AfterAll
    static void stopContainer() {
        signalRFloci.stop();
    }

    @Test
    void shouldAcceptTokenSignedWithConfiguredAccessKey() throws GeneralSecurityException {
        RestResponse response = negotiate(ACCESS_KEY);

        // the token is valid, but no application server is connected to the hub yet
        assertThat(response.status()).as(response.toString()).isEqualTo(503);
        assertThat(response.json().path("error").path("code").asText()).isEqualTo("NoServerConnection");
    }

    @Test
    void shouldRejectTokenSignedWithOtherAccessKey() throws GeneralSecurityException {
        RestResponse response = negotiate(DEFAULT_ACCESS_KEY);

        assertThat(response.status()).as(response.toString()).isEqualTo(401);
    }

    private static RestResponse negotiate(String signingKey) throws GeneralSecurityException {
        String path = "/" + signalRFloci.getAccountName() + "-signalr/client/negotiate?hub=chat";
        // the Azure SDK omits the port from token audiences
        String audience = "http://" + signalRFloci.getHost() + "/" + signalRFloci.getAccountName() + "-signalr/client/?hub=chat";
        return rest(signalRFloci, "POST", path, null, token(audience, signingKey));
    }

    private static String token(String audience, String signingKey) throws GeneralSecurityException {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String header = encoder.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = encoder.encodeToString(("{\"aud\":\"" + audience + "\",\"exp\":" + Instant.now().plusSeconds(300).getEpochSecond()
                + ",\"nameid\":\"user\"}").getBytes(StandardCharsets.UTF_8));
        // like the Azure SDK, sign with the UTF-8 bytes of the access key string
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = encoder.encodeToString(mac.doFinal((header + "." + payload).getBytes(StandardCharsets.UTF_8)));
        return header + "." + payload + "." + signature;
    }
}
