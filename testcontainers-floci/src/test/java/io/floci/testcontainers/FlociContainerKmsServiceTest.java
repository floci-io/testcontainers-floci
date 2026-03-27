package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerKmsServiceTest extends AbstractFlociContainerServiceTest {

    static KmsClient kms;

    @BeforeAll
    static void setUp() {
        kms = client(KmsClient.builder());
    }

    @Test
    void shouldCreateAndListKey() {
        String keyId = kms.createKey().keyMetadata().keyId();

        List<String> keyIds = kms.listKeys().keys().stream()
                .map(KeyListEntry::keyId)
                .toList();

        assertThat(keyIds).contains(keyId);
    }

    @Test
    void shouldEncryptAndDecrypt() {
        String keyId = kms.createKey().keyMetadata().keyId();
        String plaintext = "Hello from Floci KMS!";

        EncryptResponse encrypted = kms.encrypt(b -> b
                .keyId(keyId)
                .plaintext(SdkBytes.fromUtf8String(plaintext)));

        DecryptResponse decrypted = kms.decrypt(b -> b
                .keyId(keyId)
                .ciphertextBlob(encrypted.ciphertextBlob()));

        assertThat(decrypted.plaintext().asUtf8String()).isEqualTo(plaintext);
    }

}
