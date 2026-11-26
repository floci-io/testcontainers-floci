package io.floci.testcontainers.az.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ParityConfigTest {

    @Test
    void shouldApplyDefaultParityConfig() {
        ParityConfig config = ParityConfig.builder().build();
        assertThat(config.getLevel()).isEqualTo(ParityConfig.Level.COMPAT);
        assertThat(config.getStrictTokens()).isEmpty();
        assertThat(config.getSubscriptionListing()).isEmpty();
        assertThat(config.getStrictSubscriptions()).isEmpty();
        assertThat(config.getResourceGroupSemantics()).isEmpty();
        assertThat(config.getResourceGroupDelete()).isEmpty();
        assertThat(config.getStrictResourceGroups()).isEmpty();
        assertThat(config.getStrictProviders()).isEmpty();
        assertThat(config.getStrictNames()).isEmpty();
        assertThat(config.getLocationNormalize()).isEmpty();
        assertThat(config.getStrictLocations()).isEmpty();
        assertThat(config.getTenantScoping()).isEmpty();
    }

    @Test
    void shouldApplyCustomParityConfig() {
        ParityConfig config = customConfig();
        assertThat(config.getLevel()).isEqualTo(ParityConfig.Level.AZURE);
        assertThat(config.getStrictTokens()).contains(true);
        assertThat(config.getSubscriptionListing()).contains(false);
        assertThat(config.getStrictSubscriptions()).contains(true);
        assertThat(config.getResourceGroupSemantics()).contains(false);
        assertThat(config.getResourceGroupDelete()).contains(false);
        assertThat(config.getStrictResourceGroups()).contains(true);
        assertThat(config.getStrictProviders()).contains(true);
        assertThat(config.getStrictNames()).contains(true);
        assertThat(config.getLocationNormalize()).contains(false);
        assertThat(config.getStrictLocations()).contains(true);
        assertThat(config.getTenantScoping()).contains(false);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ParityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_PARITY", "compat")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_TOKENS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_SUBSCRIPTION_LISTING")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_SUBSCRIPTIONS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_SEMANTICS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_DELETE")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_RESOURCE_GROUPS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_PROVIDERS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_NAMES")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_LOCATION_NORMALIZE")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_STRICT_LOCATIONS")
                .doesNotContainKey("FLOCI_AZ_PARITY_OVERRIDES_TENANT_SCOPING");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_PARITY", "azure")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_TOKENS", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_SUBSCRIPTION_LISTING", "false")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_SUBSCRIPTIONS", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_SEMANTICS", "false")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_DELETE", "false")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_RESOURCE_GROUPS", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_PROVIDERS", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_NAMES", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_LOCATION_NORMALIZE", "false")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_STRICT_LOCATIONS", "true")
                .containsEntry("FLOCI_AZ_PARITY_OVERRIDES_TENANT_SCOPING", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ParityConfig copy = customConfig().toBuilder().build();

        assertThat(copy.getLevel()).isEqualTo(ParityConfig.Level.AZURE);
        assertThat(copy.getStrictTokens()).contains(true);
        assertThat(copy.getSubscriptionListing()).contains(false);
        assertThat(copy.getStrictSubscriptions()).contains(true);
        assertThat(copy.getResourceGroupSemantics()).contains(false);
        assertThat(copy.getResourceGroupDelete()).contains(false);
        assertThat(copy.getStrictResourceGroups()).contains(true);
        assertThat(copy.getStrictProviders()).contains(true);
        assertThat(copy.getStrictNames()).contains(true);
        assertThat(copy.getLocationNormalize()).contains(false);
        assertThat(copy.getStrictLocations()).contains(true);
        assertThat(copy.getTenantScoping()).contains(false);
    }

    private static ParityConfig customConfig() {
        return ParityConfig.builder()
                .level(ParityConfig.Level.AZURE)
                .strictTokens(true)
                .subscriptionListing(false)
                .strictSubscriptions(true)
                .resourceGroupSemantics(false)
                .resourceGroupDelete(false)
                .strictResourceGroups(true)
                .strictProviders(true)
                .strictNames(true)
                .locationNormalize(false)
                .strictLocations(true)
                .tenantScoping(false)
                .build();
    }
}
