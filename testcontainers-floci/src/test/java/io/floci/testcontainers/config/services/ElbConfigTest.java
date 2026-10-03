package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ElbConfigTest {

    @Test
    void shouldApplyDefaultElbConfig() {
        ElbConfig config = ElbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
    }

    @Test
    void shouldApplyCustomElbConfig() {
        ElbConfig config = ElbConfig.builder()
                .enabled(false)
                .mock(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELB_MOCK", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbConfig.builder()
                .enabled(true)
                .mock(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ELB_MOCK", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ElbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ELB_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ELB_MOCK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ElbConfig config = ElbConfig.builder()
                .enabled(false)
                .mock(true)
                .build();
        ElbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
    }

}
