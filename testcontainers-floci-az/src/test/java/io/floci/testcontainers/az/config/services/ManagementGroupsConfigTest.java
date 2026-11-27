package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ManagementGroupsConfigTest {

    @Test
    void shouldApplyDefaultManagementGroupsConfig() {
        ManagementGroupsConfig config = ManagementGroupsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomManagementGroupsConfig() {
        ManagementGroupsConfig config = ManagementGroupsConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagementGroupsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGEMENT_GROUPS_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagementGroupsConfig.builder()
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGEMENT_GROUPS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagementGroupsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGEMENT_GROUPS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ManagementGroupsConfig config = ManagementGroupsConfig.builder()
                .enabled(false)
                .build();
        ManagementGroupsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
