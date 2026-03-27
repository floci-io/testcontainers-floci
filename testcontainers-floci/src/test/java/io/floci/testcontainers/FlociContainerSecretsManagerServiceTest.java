package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerSecretsManagerServiceTest extends AbstractFlociContainerServiceTest {

    static SecretsManagerClient secretsManager;

    @BeforeAll
    static void setUp() {
        secretsManager = client(SecretsManagerClient.builder());
    }

    @Test
    void shouldCreateAndGetSecret() {
        String secretName = "test/secret-" + System.currentTimeMillis();
        String secretValue = "super-secret-value";

        secretsManager.createSecret(b -> b.name(secretName).secretString(secretValue));

        String retrieved = secretsManager.getSecretValue(b -> b.secretId(secretName))
                .secretString();

        assertThat(retrieved).isEqualTo(secretValue);
    }

    @Test
    void shouldListSecrets() {
        String secretName = "test/list-" + System.currentTimeMillis();
        secretsManager.createSecret(b -> b.name(secretName).secretString("value"));

        List<String> secretNames = secretsManager.listSecrets().secretList().stream()
                .map(SecretListEntry::name)
                .toList();

        assertThat(secretNames).contains(secretName);
    }

}
