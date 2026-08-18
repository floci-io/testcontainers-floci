package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AuthConfigTest {

    @Test
    void shouldApplyDefaultAuthConfig() {
        AuthConfig config = AuthConfig.builder().build();
        assertThat(config.isValidateSignatures()).isFalse();
        assertThat(config.getPresignSecret()).isEqualTo("local-emulator-secret");
    }

    @Test
    void shouldApplyCustomAuthConfig() {
        AuthConfig config = AuthConfig.builder()
                .validateSignatures(true)
                .presignSecret("custom-secret")
                .build();
        assertThat(config.isValidateSignatures()).isTrue();
        assertThat(config.getPresignSecret()).isEqualTo("custom-secret");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AUTH_VALIDATE_SIGNATURES", "false")
                .containsEntry("FLOCI_AUTH_PRESIGN_SECRET", "local-emulator-secret");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder()
                .validateSignatures(true)
                .presignSecret("custom-secret")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AUTH_VALIDATE_SIGNATURES", "true")
                .containsEntry("FLOCI_AUTH_PRESIGN_SECRET", "custom-secret");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AuthConfig config = AuthConfig.builder()
                .validateSignatures(true)
                .presignSecret("custom-secret")
                .build();

        AuthConfig copy = config.toBuilder().build();

        assertThat(copy.isValidateSignatures()).isTrue();
        assertThat(copy.getPresignSecret()).isEqualTo("custom-secret");
    }
}
