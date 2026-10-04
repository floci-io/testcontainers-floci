package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FirebaseAuthConfigTest {

    @Test
    void shouldApplyDefaultFirebaseAuthConfig() {
        FirebaseAuthConfig config = FirebaseAuthConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomFirebaseAuthConfig() {
        FirebaseAuthConfig config = FirebaseAuthConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirebaseAuthConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIREBASEAUTH_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirebaseAuthConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIREBASEAUTH_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FirebaseAuthConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIREBASEAUTH_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FirebaseAuthConfig config = FirebaseAuthConfig.builder()
                .enabled(false)
                .build();
        FirebaseAuthConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
