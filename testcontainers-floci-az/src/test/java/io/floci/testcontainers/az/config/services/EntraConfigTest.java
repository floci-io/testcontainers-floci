package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.TransferableCopyInspector.contentCopiedTo;
import static org.testcontainers.containers.TransferableCopyInspector.pendingCopies;

class EntraConfigTest {

    private static final String SEED_JSON = """
            {"users": [{"id": "u1", "userPrincipalName": "reader@example.test"}]}
            """;

    @Test
    void shouldApplyDefaultEntraConfig() {
        EntraConfig config = EntraConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultTenantId()).isEqualTo("00000000-0000-0000-0000-000000000002");
        assertThat(config.getIssuer()).isEmpty();
        assertThat(config.getTokenLifetimeSeconds()).isEqualTo(3599L);
        assertThat(config.isValidateTokens()).isFalse();
        assertThat(config.isValidateClientCredentials()).isFalse();
        assertThat(config.isValidateUserCredentials()).isFalse();
        assertThat(config.getSigningKeyPath()).isEmpty();
        assertThat(config.getSeedFile()).isEmpty();
        assertThat(config.getSeed()).isEmpty();
    }

    @Test
    void shouldApplyCustomEntraConfig() {
        EntraConfig config = EntraConfig.builder()
                .enabled(false)
                .defaultTenantId("11111111-1111-1111-1111-111111111111")
                .issuer("https://login.example.com/tenant/v2.0")
                .tokenLifetimeSeconds(600L)
                .validateTokens(true)
                .validateClientCredentials(true)
                .validateUserCredentials(true)
                .signingKeyPath("/app/data/my-entra-key")
                .seedFile("/app/data/entra-seed.json")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultTenantId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(config.getIssuer()).contains("https://login.example.com/tenant/v2.0");
        assertThat(config.getTokenLifetimeSeconds()).isEqualTo(600L);
        assertThat(config.isValidateTokens()).isTrue();
        assertThat(config.isValidateClientCredentials()).isTrue();
        assertThat(config.isValidateUserCredentials()).isTrue();
        assertThat(config.getSigningKeyPath()).contains("/app/data/my-entra-key");
        assertThat(config.getSeedFile()).contains("/app/data/entra-seed.json");
        assertThat(config.getSeed()).isEmpty();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EntraConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_DEFAULT_TENANT_ID", "00000000-0000-0000-0000-000000000002")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_ISSUER")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_TOKEN_LIFETIME_SECONDS", "3599")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS", "false")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_CLIENT_CREDENTIALS", "false")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_USER_CREDENTIALS", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_SIGNING_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_SEED_FILE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EntraConfig.builder()
                .defaultTenantId("11111111-1111-1111-1111-111111111111")
                .issuer("https://login.example.com/tenant/v2.0")
                .tokenLifetimeSeconds(600L)
                .validateTokens(true)
                .validateClientCredentials(true)
                .validateUserCredentials(true)
                .signingKeyPath("/app/data/my-entra-key")
                .seedFile("/app/data/entra-seed.json")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_DEFAULT_TENANT_ID", "11111111-1111-1111-1111-111111111111")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_ISSUER", "https://login.example.com/tenant/v2.0")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_TOKEN_LIFETIME_SECONDS", "600")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_CLIENT_CREDENTIALS", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_USER_CREDENTIALS", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_SIGNING_KEY_PATH", "/app/data/my-entra-key")
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_SEED_FILE", "/app/data/entra-seed.json");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EntraConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ENTRA_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_DEFAULT_TENANT_ID")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_ISSUER")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_TOKEN_LIFETIME_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_CLIENT_CREDENTIALS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_VALIDATE_USER_CREDENTIALS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_SIGNING_KEY_PATH")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ENTRA_SEED_FILE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EntraConfig config = EntraConfig.builder()
                .enabled(false)
                .defaultTenantId("11111111-1111-1111-1111-111111111111")
                .issuer("https://login.example.com/tenant/v2.0")
                .tokenLifetimeSeconds(600L)
                .validateTokens(true)
                .validateClientCredentials(true)
                .validateUserCredentials(true)
                .signingKeyPath("/app/data/my-entra-key")
                .seed(SEED_JSON)
                .build();
        EntraConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultTenantId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(copy.getIssuer()).contains("https://login.example.com/tenant/v2.0");
        assertThat(copy.getTokenLifetimeSeconds()).isEqualTo(600L);
        assertThat(copy.isValidateTokens()).isTrue();
        assertThat(copy.isValidateClientCredentials()).isTrue();
        assertThat(copy.isValidateUserCredentials()).isTrue();
        assertThat(copy.getSigningKeyPath()).contains("/app/data/my-entra-key");
        assertThat(copy.getSeedFile()).isEqualTo(config.getSeedFile());
        assertThat(copy.getSeed()).contains(SEED_JSON);
    }

    @Test
    void shouldCopySeedContentIntoContainer() {
        EntraConfig config = EntraConfig.builder().seed(SEED_JSON).build();
        String containerPath = config.getSeedFile().orElseThrow();

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);

        assertThat(containerPath).startsWith("/tmp/floci-az-entra-seed-").endsWith(".json");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_AZ_SERVICES_ENTRA_SEED_FILE", containerPath);
        assertThat(pendingCopies(container)).hasSize(1);
        assertThat(contentCopiedTo(container, containerPath)).contains(SEED_JSON);
    }

    @Test
    void shouldNotCopyFileForExplicitSeedFilePath() {
        GenericContainer<?> container = genericContainer();
        EntraConfig.builder().seed(SEED_JSON).seedFile("/app/data/entra-seed.json").build()
                .applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldNotCopySeedContentWhenServiceDisabled() {
        GenericContainer<?> container = genericContainer();
        EntraConfig.builder().enabled(false).seed(SEED_JSON).build().applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).isEmpty();
    }
}
