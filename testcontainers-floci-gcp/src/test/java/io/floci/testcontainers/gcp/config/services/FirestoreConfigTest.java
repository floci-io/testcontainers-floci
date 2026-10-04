package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FirestoreConfigTest {

    @Test
    void shouldApplyDefaultFirestoreConfig() {
        FirestoreConfig config = FirestoreConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomFirestoreConfig() {
        FirestoreConfig config = FirestoreConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirestoreConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIRESTORE_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FirestoreConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIRESTORE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FirestoreConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_FIRESTORE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FirestoreConfig config = FirestoreConfig.builder()
                .enabled(false)
                .build();
        FirestoreConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
