package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IotDataConfigTest {

    @Test
    void shouldApplyDefaultIotDataConfig() {
        IotDataConfig config = IotDataConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomIotDataConfig() {
        IotDataConfig config = IotDataConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IotDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOTDATA_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IotDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IOTDATA_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IotDataConfig config = IotDataConfig.builder()
                .enabled(false)
                .build();
        IotDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
