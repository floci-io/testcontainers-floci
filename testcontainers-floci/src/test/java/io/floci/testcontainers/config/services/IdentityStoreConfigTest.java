package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IdentityStoreConfigTest {

    @Test
    void shouldApplyDefaultIdentityStoreConfig() {
        IdentityStoreConfig config = IdentityStoreConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomIdentityStoreConfig() {
        IdentityStoreConfig config = IdentityStoreConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityStoreConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IDENTITYSTORE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityStoreConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IDENTITYSTORE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IdentityStoreConfig config = IdentityStoreConfig.builder()
                .enabled(false)
                .build();
        IdentityStoreConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyScimBearerToken() {
        IdentityStoreConfig defaults = IdentityStoreConfig.builder().build();
        assertThat(defaults.getScimBearerToken()).isEqualTo("floci-scim-token");

        IdentityStoreConfig config = IdentityStoreConfig.builder().scimBearerToken("my-scim-token").build();
        assertThat(config.getScimBearerToken()).isEqualTo("my-scim-token");
        assertThat(config.toBuilder().build().getScimBearerToken()).isEqualTo("my-scim-token");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN", "my-scim-token");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN", "floci-scim-token");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN");
    }

}
