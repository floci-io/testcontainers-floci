package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ManagedIdentityConfigTest {

    @Test
    void shouldApplyDefaultManagedIdentityConfig() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getSystemAssignedScope()).isEmpty();
        assertThat(config.isAppServiceEnabled()).isTrue();
        assertThat(config.getAppServicePath()).isEqualTo("msi/token");
        assertThat(config.getAppServiceHeaderSecret()).isEqualTo("floci-az-msi-secret");
    }

    @Test
    void shouldApplyCustomManagedIdentityConfig() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder()
                .enabled(false)
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .appServiceEnabled(false)
                .appServicePath("identity/token")
                .appServiceHeaderSecret("my-identity-header")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSystemAssignedScope()).contains("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app");
        assertThat(config.isAppServiceEnabled()).isFalse();
        assertThat(config.getAppServicePath()).isEqualTo("identity/token");
        assertThat(config.getAppServiceHeaderSecret()).isEqualTo("my-identity-header");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_PATH", "msi/token")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_HEADER_SECRET", "floci-az-msi-secret")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder()
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .appServiceEnabled(false)
                .appServicePath("identity/token")
                .appServiceHeaderSecret("my-identity-header")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE", "subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_ENABLED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_PATH", "identity/token")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_HEADER_SECRET", "my-identity-header");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_ENABLED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_PATH")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_HEADER_SECRET");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder()
                .enabled(false)
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .appServiceEnabled(false)
                .appServicePath("identity/token")
                .appServiceHeaderSecret("my-identity-header")
                .build();
        ManagedIdentityConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSystemAssignedScope()).contains("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app");
        assertThat(copy.isAppServiceEnabled()).isFalse();
        assertThat(copy.getAppServicePath()).isEqualTo("identity/token");
        assertThat(copy.getAppServiceHeaderSecret()).isEqualTo("my-identity-header");
    }
}
