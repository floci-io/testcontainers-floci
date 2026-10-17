package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EmrServerlessConfigTest {

    @Test
    void shouldApplyDefaultEmrServerlessConfig() {
        EmrServerlessConfig config = EmrServerlessConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomEmrServerlessConfig() {
        EmrServerlessConfig config = EmrServerlessConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrServerlessConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrServerlessConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EmrServerlessConfig config = EmrServerlessConfig.builder()
                .enabled(false)
                .build();
        EmrServerlessConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
