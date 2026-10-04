package io.floci.testcontainers.gcp.services;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KmsServiceTest extends AbstractServiceTest {

    @Test
    void shouldEncryptAndDecrypt() throws Exception {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create(KeyManagementServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            KeyRing keyRing = client.createKeyRing(LocationName.of(projectId(), "global"), uniqueName("ring"),
                    KeyRing.getDefaultInstance());
            CryptoKey key = client.createCryptoKey(keyRing.getName(), uniqueName("key"), CryptoKey.newBuilder()
                    .setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT)
                    .build());

            ByteString ciphertext = client.encrypt(key.getName(), ByteString.copyFromUtf8("hello")).getCiphertext();
            ByteString plaintext = client.decrypt(key.getName(), ciphertext).getPlaintext();

            assertThat(ciphertext.toStringUtf8()).isNotEqualTo("hello");
            assertThat(plaintext.toStringUtf8()).isEqualTo("hello");
        }
    }
}
