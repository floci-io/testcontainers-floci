package io.floci.testcontainers.oci.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AuthConfigTest {

    @Test
    void shouldApplyDefaultAuthConfig() {
        AuthConfig config = AuthConfig.builder().build();
        assertThat(config.isRequireSignature()).isFalse();
    }

    @Test
    void shouldApplyCustomAuthConfig() {
        AuthConfig config = AuthConfig.builder()
                .requireSignature(true)
                .build();
        assertThat(config.isRequireSignature()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_AUTH_REQUIRE_SIGNATURE", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder()
                .requireSignature(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_AUTH_REQUIRE_SIGNATURE", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AuthConfig config = AuthConfig.builder()
                .requireSignature(true)
                .build();

        AuthConfig copy = config.toBuilder().build();

        assertThat(copy.isRequireSignature()).isTrue();
    }
}
