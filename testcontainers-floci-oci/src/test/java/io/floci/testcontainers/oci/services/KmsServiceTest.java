package io.floci.testcontainers.oci.services;

import com.oracle.bmc.keymanagement.KmsCryptoClient;
import com.oracle.bmc.keymanagement.KmsManagementClient;
import com.oracle.bmc.keymanagement.KmsVaultClient;
import com.oracle.bmc.keymanagement.model.CreateKeyDetails;
import com.oracle.bmc.keymanagement.model.CreateVaultDetails;
import com.oracle.bmc.keymanagement.model.DecryptDataDetails;
import com.oracle.bmc.keymanagement.model.EncryptDataDetails;
import com.oracle.bmc.keymanagement.model.KeyShape;
import com.oracle.bmc.keymanagement.model.Vault;
import com.oracle.bmc.keymanagement.requests.CreateKeyRequest;
import com.oracle.bmc.keymanagement.requests.CreateVaultRequest;
import com.oracle.bmc.keymanagement.requests.DecryptRequest;
import com.oracle.bmc.keymanagement.requests.EncryptRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class KmsServiceTest extends AbstractServiceTest {

    private static KmsVaultClient vaults;
    private static KmsManagementClient management;
    private static KmsCryptoClient crypto;

    @BeforeAll
    static void createClients() {
        vaults = client(KmsVaultClient.builder());
        // the management and crypto endpoints Floci returns for a vault are its own base URL
        management = client(KmsManagementClient.builder());
        crypto = client(KmsCryptoClient.builder());
    }

    @AfterAll
    static void closeClients() {
        vaults.close();
        management.close();
        crypto.close();
    }

    @Test
    void shouldEncryptAndDecryptWithVaultKey() {
        Vault vault = vaults.createVault(CreateVaultRequest.builder()
                .createVaultDetails(CreateVaultDetails.builder()
                        .compartmentId(tenancyId())
                        .displayName("vault-" + System.nanoTime())
                        .vaultType(CreateVaultDetails.VaultType.Default)
                        .build())
                .build()).getVault();
        String keyId = management.createKey(CreateKeyRequest.builder()
                .createKeyDetails(CreateKeyDetails.builder()
                        .compartmentId(tenancyId())
                        .displayName("key")
                        .keyShape(KeyShape.builder().algorithm(KeyShape.Algorithm.Aes).length(32).build())
                        .build())
                .build()).getKey().getId();
        String plaintext = Base64.getEncoder().encodeToString("secret payload".getBytes(StandardCharsets.UTF_8));

        String ciphertext = crypto.encrypt(EncryptRequest.builder()
                .encryptDataDetails(EncryptDataDetails.builder().keyId(keyId).plaintext(plaintext).build())
                .build()).getEncryptedData().getCiphertext();
        String decrypted = crypto.decrypt(DecryptRequest.builder()
                .decryptDataDetails(DecryptDataDetails.builder().keyId(keyId).ciphertext(ciphertext).build())
                .build()).getDecryptedData().getPlaintext();

        assertThat(vault.getLifecycleState()).isEqualTo(Vault.LifecycleState.Active);
        assertThat(ciphertext).isNotEqualTo(plaintext);
        assertThat(decrypted).isEqualTo(plaintext);
    }
}
