package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceGroupsTaggingConfigTest {

    @Test
    void shouldApplyDefaultResourceGroupsTaggingConfig() {
        ResourceGroupsTaggingConfig config = ResourceGroupsTaggingConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomResourceGroupsTaggingConfig() {
        ResourceGroupsTaggingConfig config = ResourceGroupsTaggingConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGroupsTaggingConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TAGGING_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ResourceGroupsTaggingConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_TAGGING_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ResourceGroupsTaggingConfig config = ResourceGroupsTaggingConfig.builder()
                .enabled(false)
                .build();
        ResourceGroupsTaggingConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
