package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AthenaConfigTest {

    @Test
    void shouldApplyDefaultAthenaConfig() {
        AthenaConfig config = AthenaConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
    }

    @Test
    void shouldApplyCustomAthenaConfig() {
        AthenaConfig config = AthenaConfig.builder()
                .enabled(false)
                .mock(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AthenaConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ATHENA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ATHENA_MOCK", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AthenaConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ATHENA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ATHENA_MOCK");
    }

    @Test
    void shouldApplyMockEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AthenaConfig.builder()
                .mock(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ATHENA_MOCK", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AthenaConfig config = AthenaConfig.builder()
                .enabled(false)
                .mock(true)
                .build();
        AthenaConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
    }

}
