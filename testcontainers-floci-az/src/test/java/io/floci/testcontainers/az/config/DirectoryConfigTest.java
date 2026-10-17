package io.floci.testcontainers.az.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DirectoryConfigTest {

    private static final String TENANT_ID = "11111111-0000-0000-0000-000000000000";
    private static final String OTHER_TENANT_ID = "33333333-0000-0000-0000-000000000000";
    private static final String SUBSCRIPTION_ID = "22222222-0000-0000-0000-000000000000";
    private static final String OTHER_SUBSCRIPTION_ID = "44444444-0000-0000-0000-000000000000";

    @Test
    void shouldApplyDefaultDirectoryConfig() {
        DirectoryConfig config = DirectoryConfig.builder().build();
        assertThat(config.getDefaultCloud()).isEqualTo("AzureCloud");
        assertThat(config.getTenants()).isEmpty();
        assertThat(config.getSubscriptions()).isEmpty();
    }

    @Test
    void shouldApplyCustomDirectoryConfig() {
        DirectoryConfig config = customConfig();
        assertThat(config.getDefaultCloud()).isEqualTo("AzureUSGovernment");

        assertThat(config.getTenants()).hasSize(2);
        assertThat(config.getTenants().get(0).getId()).isEqualTo(TENANT_ID);
        assertThat(config.getTenants().get(0).getDisplayName()).contains("Contoso Gov");
        assertThat(config.getTenants().get(0).getDefaultDomain()).contains("contoso.example");
        assertThat(config.getTenants().get(0).getCloud()).contains("AzureChinaCloud");
        assertThat(config.getTenants().get(1).getId()).isEqualTo(OTHER_TENANT_ID);
        assertThat(config.getTenants().get(1).getDisplayName()).isEmpty();
        assertThat(config.getTenants().get(1).getDefaultDomain()).isEmpty();
        assertThat(config.getTenants().get(1).getCloud()).isEmpty();

        assertThat(config.getSubscriptions()).hasSize(2);
        assertThat(config.getSubscriptions().get(0).getId()).isEqualTo(SUBSCRIPTION_ID);
        assertThat(config.getSubscriptions().get(0).getTenantId()).contains(TENANT_ID);
        assertThat(config.getSubscriptions().get(0).getDisplayName()).contains("contoso-dev");
        assertThat(config.getSubscriptions().get(0).getState()).contains("Disabled");
        assertThat(config.getSubscriptions().get(1).getId()).isEqualTo(OTHER_SUBSCRIPTION_ID);
        assertThat(config.getSubscriptions().get(1).getTenantId()).isEmpty();
        assertThat(config.getSubscriptions().get(1).getDisplayName()).isEmpty();
        assertThat(config.getSubscriptions().get(1).getState()).isEmpty();
    }

    @Test
    void shouldReplaceEntriesWithSameIdInPlace() {
        DirectoryConfig config = DirectoryConfig.builder()
                .tenant(TENANT_ID, t -> t.displayName("first"))
                .tenant(OTHER_TENANT_ID)
                .tenant(TENANT_ID, t -> t.displayName("second"))
                .subscription(SUBSCRIPTION_ID, s -> s.state("Warned"))
                .subscription(OTHER_SUBSCRIPTION_ID)
                .subscription(SUBSCRIPTION_ID, s -> s.state("PastDue"))
                .build();

        assertThat(config.getTenants()).extracting(TenantConfig::getId).containsExactly(TENANT_ID, OTHER_TENANT_ID);
        assertThat(config.getTenants().get(0).getDisplayName()).contains("second");
        assertThat(config.getSubscriptions()).extracting(SubscriptionConfig::getId)
                .containsExactly(SUBSCRIPTION_ID, OTHER_SUBSCRIPTION_ID);
        assertThat(config.getSubscriptions().get(0).getState()).contains("PastDue");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DirectoryConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_CLOUD_DEFAULT", "AzureCloud")
                .noneSatisfy((key, value) -> assertThat(key).startsWith("FLOCI_AZ_TENANTS_"))
                .noneSatisfy((key, value) -> assertThat(key).startsWith("FLOCI_AZ_SUBSCRIPTIONS_"));
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_CLOUD_DEFAULT", "AzureUSGovernment")
                .containsEntry("FLOCI_AZ_TENANTS_0__ID", TENANT_ID)
                .containsEntry("FLOCI_AZ_TENANTS_0__DISPLAY_NAME", "Contoso Gov")
                .containsEntry("FLOCI_AZ_TENANTS_0__DEFAULT_DOMAIN", "contoso.example")
                .containsEntry("FLOCI_AZ_TENANTS_0__CLOUD", "AzureChinaCloud")
                .containsEntry("FLOCI_AZ_TENANTS_1__ID", OTHER_TENANT_ID)
                .doesNotContainKey("FLOCI_AZ_TENANTS_1__DISPLAY_NAME")
                .doesNotContainKey("FLOCI_AZ_TENANTS_1__DEFAULT_DOMAIN")
                .doesNotContainKey("FLOCI_AZ_TENANTS_1__CLOUD")
                .containsEntry("FLOCI_AZ_SUBSCRIPTIONS_0__ID", SUBSCRIPTION_ID)
                .containsEntry("FLOCI_AZ_SUBSCRIPTIONS_0__TENANT_ID", TENANT_ID)
                .containsEntry("FLOCI_AZ_SUBSCRIPTIONS_0__DISPLAY_NAME", "contoso-dev")
                .containsEntry("FLOCI_AZ_SUBSCRIPTIONS_0__STATE", "Disabled")
                .containsEntry("FLOCI_AZ_SUBSCRIPTIONS_1__ID", OTHER_SUBSCRIPTION_ID)
                .doesNotContainKey("FLOCI_AZ_SUBSCRIPTIONS_1__TENANT_ID")
                .doesNotContainKey("FLOCI_AZ_SUBSCRIPTIONS_1__DISPLAY_NAME")
                .doesNotContainKey("FLOCI_AZ_SUBSCRIPTIONS_1__STATE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DirectoryConfig copy = customConfig().toBuilder().build();

        assertThat(copy.getDefaultCloud()).isEqualTo("AzureUSGovernment");
        assertThat(copy.getTenants()).extracting(TenantConfig::getId).containsExactly(TENANT_ID, OTHER_TENANT_ID);
        assertThat(copy.getTenants().get(0).getDisplayName()).contains("Contoso Gov");
        assertThat(copy.getTenants().get(0).getDefaultDomain()).contains("contoso.example");
        assertThat(copy.getTenants().get(0).getCloud()).contains("AzureChinaCloud");
        assertThat(copy.getSubscriptions()).extracting(SubscriptionConfig::getId)
                .containsExactly(SUBSCRIPTION_ID, OTHER_SUBSCRIPTION_ID);
        assertThat(copy.getSubscriptions().get(0).getTenantId()).contains(TENANT_ID);
        assertThat(copy.getSubscriptions().get(0).getDisplayName()).contains("contoso-dev");
        assertThat(copy.getSubscriptions().get(0).getState()).contains("Disabled");
    }

    @Test
    void shouldNotShareEntriesWithBuilder() {
        DirectoryConfig.Builder builder = DirectoryConfig.builder().tenant(TENANT_ID).subscription(SUBSCRIPTION_ID);
        DirectoryConfig config = builder.build();

        builder.tenant(OTHER_TENANT_ID).subscription(OTHER_SUBSCRIPTION_ID);

        assertThat(config.getTenants()).extracting(TenantConfig::getId).containsExactly(TENANT_ID);
        assertThat(config.getSubscriptions()).extracting(SubscriptionConfig::getId).containsExactly(SUBSCRIPTION_ID);
    }

    private static DirectoryConfig customConfig() {
        return DirectoryConfig.builder()
                .defaultCloud("AzureUSGovernment")
                .tenant(TENANT_ID, t -> t
                        .displayName("Contoso Gov")
                        .defaultDomain("contoso.example")
                        .cloud("AzureChinaCloud"))
                .tenant(OTHER_TENANT_ID)
                .subscription(SUBSCRIPTION_ID, s -> s
                        .tenantId(TENANT_ID)
                        .displayName("contoso-dev")
                        .state("Disabled"))
                .subscription(OTHER_SUBSCRIPTION_ID)
                .build();
    }
}
