package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class LakeFormationConfigTest {

    @Test
    void shouldApplyDefaultLakeFormationConfig() {
        LakeFormationConfig config = LakeFormationConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomLakeFormationConfig() {
        LakeFormationConfig config = LakeFormationConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LakeFormationConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LAKEFORMATION_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        LakeFormationConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_LAKEFORMATION_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        LakeFormationConfig config = LakeFormationConfig.builder()
                .enabled(false)
                .build();
        LakeFormationConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
