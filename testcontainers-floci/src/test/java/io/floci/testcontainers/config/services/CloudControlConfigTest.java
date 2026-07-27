package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudControlConfigTest {

    @Test
    void shouldApplyDefaultCloudControlConfig() {
        CloudControlConfig config = CloudControlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCloudControlConfig() {
        CloudControlConfig config = CloudControlConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudControlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudControlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudControlConfig config = CloudControlConfig.builder()
                .enabled(false)
                .build();
        CloudControlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
