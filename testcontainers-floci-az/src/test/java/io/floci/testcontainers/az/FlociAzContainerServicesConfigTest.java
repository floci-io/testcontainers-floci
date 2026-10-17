package io.floci.testcontainers.az;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every configuration exposed by {@link FlociAzContainer} is actually picked up by the
 * container: the changed value survives the {@code with*Config(...)} round-trip, the matching
 * {@code FLOCI_AZ_*} environment variable is applied, and the container's port mappings are wired.
 *
 * <p>There is exactly one test method per service config class in {@code config/services/} plus one
 * per cross-cutting config class in {@code config/}.
 */
class FlociAzContainerServicesConfigTest {

    /**
     * Applies {@code configurer} to a fresh {@link FlociAzContainer}, then asserts that:
     * <ol>
     *   <li>the changed property is readable again via {@code get*Config()}
     *       ({@code actualValue} equals {@code expectedValue}),</li>
     *   <li>the container has the environment variable {@code envKey=envValue}, and</li>
     *   <li>the container exposes the main Floci Azure port.</li>
     * </ol>
     */
    private static void assertConfigWired(
            Consumer<FlociAzContainer> configurer,
            Function<FlociAzContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue) {
        try (FlociAzContainer container = new FlociAzContainer()) {
            configurer.accept(container);

            assertThat(actualValue.apply(container))
                    .as("value retrieved via get*Config()")
                    .isEqualTo(expectedValue);
            assertThat(container.getEnvMap())
                    .as("environment variable applied to the Floci Azure container")
                    .containsEntry(envKey, envValue);
            assertThat(container.getExposedPorts())
                    .as("port mapping configured on the Floci Azure container")
                    .contains(FlociAzContainer.PORT);
        }
    }

    // --- Service configs (config/services/) -------------------------------------------------------

    @Test
    void shouldWireBlobConfigIntoContainer() {
        assertConfigWired(
                c -> c.withBlobConfig(cfg -> cfg.hierarchicalNamespaceAccounts(List.of("datalake1"))),
                c -> c.getBlobConfig().getHierarchicalNamespaceAccounts(), List.of("datalake1"),
                "FLOCI_AZ_SERVICES_BLOB_HIERARCHICAL_NAMESPACE_ACCOUNTS", "datalake1");
    }
    @Test
    void shouldWireQueueConfigIntoContainer() {
        assertConfigWired(
                c -> c.withQueueConfig(cfg -> cfg.enabled(false)),
                c -> c.getQueueConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_QUEUE_ENABLED", "false");
    }

    @Test
    void shouldWireTableConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTableConfig(cfg -> cfg.enabled(false)),
                c -> c.getTableConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_TABLE_ENABLED", "false");
    }

    @Test
    void shouldWireFunctionsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withFunctionsConfig(cfg -> cfg.mocked(true)),
                c -> c.getFunctionsConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_FUNCTIONS_MOCKED", "true");
    }
    @Test
    void shouldWireAppConfigConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAppConfigConfig(cfg -> cfg.enabled(false)),
                c -> c.getAppConfigConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_APP_CONFIG_ENABLED", "false");
    }

    @Test
    void shouldWireSignalRConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSignalRConfig(cfg -> cfg.accessKey("bXktc2lnbmFsci1rZXk=")),
                c -> c.getSignalRConfig().getAccessKey(), "bXktc2lnbmFsci1rZXk=",
                "FLOCI_AZ_SERVICES_SIGNALR_ACCESS_KEY", "bXktc2lnbmFsci1rZXk=");
    }
    @Test
    void shouldWireCosmosConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCosmosConfig(cfg -> cfg.mongodb(api -> api.enabled(true))),
                c -> c.getCosmosConfig().getMongodb().isEnabled(), true,
                "FLOCI_AZ_SERVICES_COSMOS_ENGINES_MONGODB_ENABLED", "true");
    }
    @Test
    void shouldWireKeyVaultConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKeyVaultConfig(cfg -> cfg.enabled(false)),
                c -> c.getKeyVaultConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", "false");
    }
    @Test
    void shouldWireEventHubConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEventHubConfig(cfg -> cfg.mocked(true)),
                c -> c.getEventHubConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_EVENT_HUB_MOCKED", "true");
    }
    @Test
    void shouldWireSqlConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSqlConfig(cfg -> cfg.acceptEula("Y")),
                c -> c.getSqlConfig().getAcceptEula(), "Y",
                "FLOCI_AZ_SERVICES_SQL_ACCEPT_EULA", "Y");
    }
    @Test
    void shouldWirePostgresConfigIntoContainer() {
        assertConfigWired(
                c -> c.withPostgresConfig(cfg -> cfg.mocked(true)),
                c -> c.getPostgresConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_POSTGRES_MOCKED", "true");
    }
    @Test
    void shouldWireMySqlConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMySqlConfig(cfg -> cfg.mocked(true)),
                c -> c.getMySqlConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_MYSQL_MOCKED", "true");
    }
    @Test
    void shouldWireMariaDbConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMariaDbConfig(cfg -> cfg.mocked(true)),
                c -> c.getMariaDbConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_MARIA_DB_MOCKED", "true");
    }
    @Test
    void shouldWireServiceBusConfigIntoContainer() {
        assertConfigWired(
                c -> c.withServiceBusConfig(cfg -> cfg.mocked(false)),
                c -> c.getServiceBusConfig().isMocked(), false,
                "FLOCI_AZ_SERVICES_SERVICE_BUS_MOCKED", "false");
    }
    @Test
    void shouldWireAksConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAksConfig(cfg -> cfg.mocked(true)),
                c -> c.getAksConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_AKS_MOCKED", "true");
    }
    @Test
    void shouldWireAciConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAciConfig(cfg -> cfg.mocked(false)),
                c -> c.getAciConfig().isMocked(), false,
                "FLOCI_AZ_SERVICES_ACI_MOCKED", "false");
    }
    @Test
    void shouldWireVmConfigIntoContainer() {
        assertConfigWired(
                c -> c.withVmConfig(cfg -> cfg.mocked(false)),
                c -> c.getVmConfig().isMocked(), false,
                "FLOCI_AZ_SERVICES_VM_MOCKED", "false");
    }
    @Test
    void shouldWireApimConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApimConfig(cfg -> cfg.enabled(false)),
                c -> c.getApimConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_APIM_ENABLED", "false");
    }
    @Test
    void shouldWireRedisConfigIntoContainer() {
        assertConfigWired(
                c -> c.withRedisConfig(cfg -> cfg.mocked(true)),
                c -> c.getRedisConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_REDIS_MOCKED", "true");
    }
    @Test
    void shouldWireAcrConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAcrConfig(cfg -> cfg.mocked(true)),
                c -> c.getAcrConfig().isMocked(), true,
                "FLOCI_AZ_SERVICES_ACR_MOCKED", "true");
    }
    @Test
    void shouldWireMonitorConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMonitorConfig(cfg -> cfg.enabled(false)),
                c -> c.getMonitorConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_MONITOR_ENABLED", "false");
    }
    @Test
    void shouldWireEntraConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEntraConfig(cfg -> cfg.validateTokens(true)),
                c -> c.getEntraConfig().isValidateTokens(), true,
                "FLOCI_AZ_SERVICES_ENTRA_VALIDATE_TOKENS", "true");
    }
    @Test
    void shouldWireGraphConfigIntoContainer() {
        assertConfigWired(
                c -> c.withGraphConfig(cfg -> cfg.enabled(false)),
                c -> c.getGraphConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_GRAPH_ENABLED", "false");
    }
    @Test
    void shouldWireArmConfigIntoContainer() {
        assertConfigWired(
                c -> c.withArmConfig(cfg -> cfg.defaultSubscriptionId("22222222-2222-2222-2222-222222222222")),
                c -> c.getArmConfig().getDefaultSubscriptionId(), "22222222-2222-2222-2222-222222222222",
                "FLOCI_AZ_SERVICES_ARM_DEFAULT_SUBSCRIPTION_ID", "22222222-2222-2222-2222-222222222222");
    }
    @Test
    void shouldWireNetworkConfigIntoContainer() {
        assertConfigWired(
                c -> c.withNetworkConfig(cfg -> cfg.enabled(false)),
                c -> c.getNetworkConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_NETWORK_ENABLED", "false");
    }
    @Test
    void shouldWireEventGridConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEventGridConfig(cfg -> cfg.maxDeliveryAttempts(5)),
                c -> c.getEventGridConfig().getMaxDeliveryAttempts(), 5,
                "FLOCI_AZ_SERVICES_EVENT_GRID_MAX_DELIVERY_ATTEMPTS", "5");
    }
    @Test
    void shouldWireManagedIdentityConfigIntoContainer() {
        assertConfigWired(
                c -> c.withManagedIdentityConfig(cfg -> cfg.systemAssignedScope("subscriptions/sub/resourceGroups/rg")),
                c -> c.getManagedIdentityConfig().getSystemAssignedScope(), Optional.of("subscriptions/sub/resourceGroups/rg"),
                "FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE", "subscriptions/sub/resourceGroups/rg");
    }
    @Test
    void shouldWireContainerAppsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withContainerAppsConfig(cfg -> cfg.mocked(false)),
                c -> c.getContainerAppsConfig().isMocked(), false,
                "FLOCI_AZ_SERVICES_CONTAINER_APPS_MOCKED", "false");
    }
    @Test
    void shouldWirePolicyConfigIntoContainer() {
        assertConfigWired(
                c -> c.withPolicyConfig(cfg -> cfg.enabled(false)),
                c -> c.getPolicyConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_POLICY_ENABLED", "false");
    }
    @Test
    void shouldWireEmailConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEmailConfig(cfg -> cfg.enabled(false)),
                c -> c.getEmailConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_EMAIL_ENABLED", "false");
    }

    // --- Cross-cutting configs (config/) ----------------------------------------------------------

    @Test
    void shouldWireAuthConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAuthConfig(cfg -> cfg.mode("strict")),
                c -> c.getAuthConfig().getMode(), "strict",
                "FLOCI_AZ_AUTH_MODE", "strict");
    }

    @Test
    void shouldWireTlsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTlsConfig(cfg -> cfg.enabled(true)),
                c -> c.getTlsConfig().isEnabled(), true,
                "FLOCI_AZ_TLS_ENABLED", "true");
    }
}
