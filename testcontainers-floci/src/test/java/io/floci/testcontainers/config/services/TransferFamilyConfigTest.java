package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class TransferFamilyConfigTest {

    @Test
    void shouldApplyDefaultTransferFamilyConfig() {
        TransferFamilyConfig config = TransferFamilyConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomTransferFamilyConfig() {
        TransferFamilyConfig config = TransferFamilyConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        TransferFamilyConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSFER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        TransferFamilyConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TRANSFER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        TransferFamilyConfig config = TransferFamilyConfig.builder()
                .enabled(false)
                .build();
        TransferFamilyConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
