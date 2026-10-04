package io.floci.testcontainers.gcp.services;

import com.google.cloud.iam.credentials.v1.GenerateAccessTokenResponse;
import com.google.cloud.iam.credentials.v1.IamCredentialsClient;
import com.google.cloud.iam.credentials.v1.IamCredentialsSettings;
import com.google.protobuf.Duration;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IamCredentialsServiceTest extends AbstractServiceTest {

    @Test
    void shouldGenerateAccessToken() throws Exception {
        String serviceAccount = "projects/-/serviceAccounts/test@" + projectId() + ".iam.gserviceaccount.com";

        try (IamCredentialsClient client = IamCredentialsClient.create(IamCredentialsSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            GenerateAccessTokenResponse response = client.generateAccessToken(serviceAccount, List.of(),
                    List.of("https://www.googleapis.com/auth/cloud-platform"),
                    Duration.newBuilder().setSeconds(3600).build());

            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getExpireTime().getSeconds()).isGreaterThan(Instant.now().getEpochSecond());
        }
    }
}
