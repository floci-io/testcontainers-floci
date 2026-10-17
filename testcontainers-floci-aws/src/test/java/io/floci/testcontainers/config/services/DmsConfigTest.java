package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DmsConfigTest {

    @Test
    void shouldApplyDefaultDmsConfig() {
        DmsConfig config = DmsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomDmsConfig() {
        DmsConfig config = DmsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DmsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DMS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DmsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_DMS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DmsConfig config = DmsConfig.builder()
                .enabled(false)
                .build();
        DmsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
