package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigServiceConfigTest {

    @Test
    void shouldApplyDefaultConfigServiceConfig() {
        ConfigServiceConfig config = ConfigServiceConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomConfigServiceConfig() {
        ConfigServiceConfig config = ConfigServiceConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ConfigServiceConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CONFIGSERVICE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ConfigServiceConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CONFIGSERVICE_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ConfigServiceConfig config = ConfigServiceConfig.builder()
                .enabled(false)
                .build();
        ConfigServiceConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
