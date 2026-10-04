package io.floci.testcontainers.oci.services;

import com.oracle.bmc.keymanagement.KmsManagementClient;
import com.oracle.bmc.keymanagement.KmsVaultClient;
import com.oracle.bmc.keymanagement.model.CreateKeyDetails;
import com.oracle.bmc.keymanagement.model.CreateVaultDetails;
import com.oracle.bmc.keymanagement.model.KeyShape;
import com.oracle.bmc.keymanagement.requests.CreateKeyRequest;
import com.oracle.bmc.keymanagement.requests.CreateVaultRequest;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.vault.VaultsClient;
import com.oracle.bmc.vault.model.Base64SecretContentDetails;
import com.oracle.bmc.vault.model.CreateSecretDetails;
import com.oracle.bmc.vault.requests.CreateSecretRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class VaultServiceTest extends AbstractServiceTest {

    private static KmsVaultClient kmsVaults;
    private static KmsManagementClient kmsManagement;
    private static VaultsClient vaults;
    private static SecretsClient secrets;

    @BeforeAll
    static void createClients() {
        kmsVaults = client(KmsVaultClient.builder());
        kmsManagement = client(KmsManagementClient.builder());
        vaults = client(VaultsClient.builder());
        secrets = client(SecretsClient.builder());
    }

    @AfterAll
    static void closeClients() {
        kmsVaults.close();
        kmsManagement.close();
        vaults.close();
        secrets.close();
    }

    @Test
    void shouldCreateSecretAndReadItsBundle() {
        String vaultId = kmsVaults.createVault(CreateVaultRequest.builder()
                .createVaultDetails(CreateVaultDetails.builder()
                        .compartmentId(tenancyId())
                        .displayName("vault-" + System.nanoTime())
                        .vaultType(CreateVaultDetails.VaultType.Default)
                        .build())
                .build()).getVault().getId();
        String keyId = kmsManagement.createKey(CreateKeyRequest.builder()
                .createKeyDetails(CreateKeyDetails.builder()
                        .compartmentId(tenancyId())
                        .displayName("secret-key")
                        .keyShape(KeyShape.builder().algorithm(KeyShape.Algorithm.Aes).length(32).build())
                        .build())
                .build()).getKey().getId();
        String secretValue = Base64.getEncoder().encodeToString("s3cret".getBytes(StandardCharsets.UTF_8));

        String secretId = vaults.createSecret(CreateSecretRequest.builder()
                .createSecretDetails(CreateSecretDetails.builder()
                        .compartmentId(tenancyId())
                        .vaultId(vaultId)
                        .keyId(keyId)
                        .secretName("secret-" + System.nanoTime())
                        .secretContent(Base64SecretContentDetails.builder().content(secretValue).build())
                        .build())
                .build()).getSecret().getId();
        // the secret content is only returned by the separate Secrets (bundle) service
        var bundle = secrets.getSecretBundle(GetSecretBundleRequest.builder()
                .secretId(secretId)
                .build()).getSecretBundle();

        assertThat(bundle.getVersionNumber()).isEqualTo(1L);
        assertThat(bundle.getSecretBundleContent())
                .isInstanceOfSatisfying(Base64SecretBundleContentDetails.class,
                        content -> assertThat(content.getContent()).isEqualTo(secretValue));
    }
}
