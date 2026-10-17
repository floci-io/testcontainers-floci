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
        assertThat(config.getScimBearerToken()).isEqualTo("floci-scim-token");
    }

    @Test
    void shouldApplyCustomIdentityStoreConfig() {
        IdentityStoreConfig config = IdentityStoreConfig.builder()
                .enabled(false)
                .scimBearerToken("my-scim-token")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getScimBearerToken()).isEqualTo("my-scim-token");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityStoreConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IDENTITYSTORE_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN", "floci-scim-token");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityStoreConfig.builder()
                .scimBearerToken("my-scim-token")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN", "my-scim-token");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityStoreConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IDENTITYSTORE_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IdentityStoreConfig config = IdentityStoreConfig.builder()
                .enabled(false)
                .scimBearerToken("my-scim-token")
                .build();
        IdentityStoreConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getScimBearerToken()).isEqualTo("my-scim-token");
    }

}
