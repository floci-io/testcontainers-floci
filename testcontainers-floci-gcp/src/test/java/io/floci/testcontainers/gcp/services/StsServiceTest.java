package io.floci.testcontainers.gcp.services;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.CredentialAccessBoundary;
import com.google.auth.oauth2.DownscopedCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StsServiceTest extends AbstractServiceTest {

    @Test
    void shouldExchangeTokenForDownscopedToken() throws Exception {
        GoogleCredentials source = GoogleCredentials.create(
                new AccessToken("source-token", Date.from(Instant.now().plusSeconds(3600))));
        CredentialAccessBoundary accessBoundary = CredentialAccessBoundary.newBuilder()
                .addRule(CredentialAccessBoundary.AccessBoundaryRule.newBuilder()
                        .setAvailableResource("//storage.googleapis.com/projects/_/buckets/my-bucket")
                        .setAvailablePermissions(List.of("inRole:roles/storage.objectViewer"))
                        .build())
                .build();

        DownscopedCredentials credentials = DownscopedCredentials.newBuilder()
                .setSourceCredential(source)
                .setCredentialAccessBoundary(accessBoundary)
                // redirect the token exchange from sts.googleapis.com to Floci GCP
                .setHttpTransportFactory(() -> new NetHttpTransport.Builder()
                        .setConnectionFactory(url -> (HttpURLConnection) ("sts.googleapis.com".equals(url.getHost())
                                ? new URL(floci.getEndpoint() + url.getFile())
                                : url).openConnection())
                        .build())
                .build();
        AccessToken token = credentials.refreshAccessToken();

        assertThat(token.getTokenValue()).isNotBlank();
        assertThat(token.getExpirationTime()).isAfter(new Date());
    }
}
