package io.floci.testcontainers.gcp.services;

import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersion;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecretManagerServiceTest extends AbstractServiceTest {

    @Test
    void shouldStoreAndAccessSecret() throws Exception {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create(SecretManagerServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            Secret secret = client.createSecret(ProjectName.of(projectId()), uniqueName("secret"), Secret.newBuilder()
                    .setReplication(Replication.newBuilder()
                            .setAutomatic(Replication.Automatic.getDefaultInstance()))
                    .build());
            SecretVersion version = client.addSecretVersion(secret.getName(), SecretPayload.newBuilder()
                    .setData(ByteString.copyFromUtf8("s3cr3t"))
                    .build());

            String value = client.accessSecretVersion(version.getName()).getPayload().getData().toStringUtf8();

            assertThat(value).isEqualTo("s3cr3t");
        }
    }
}
