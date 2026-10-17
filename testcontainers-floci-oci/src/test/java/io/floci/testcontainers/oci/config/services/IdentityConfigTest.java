package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IdentityConfigTest {

    @Test
    void shouldApplyDefaultIdentityConfig() {
        IdentityConfig config = IdentityConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isStrictCompartments()).isFalse();
    }

    @Test
    void shouldApplyCustomIdentityConfig() {
        IdentityConfig config = IdentityConfig.builder()
                .enabled(false)
                .strictCompartments(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isStrictCompartments()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_IDENTITY_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_IDENTITY_STRICT_COMPARTMENTS", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityConfig.builder()
                .strictCompartments(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_IDENTITY_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_IDENTITY_STRICT_COMPARTMENTS", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IdentityConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_IDENTITY_ENABLED", "false")
                .doesNotContainKey("FLOCI_OCI_SERVICES_IDENTITY_STRICT_COMPARTMENTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IdentityConfig config = IdentityConfig.builder()
                .enabled(false)
                .strictCompartments(true)
                .build();
        IdentityConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isStrictCompartments()).isTrue();
    }
}
