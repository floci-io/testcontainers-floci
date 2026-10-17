package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GuardDutyConfigTest {

    @Test
    void shouldApplyDefaultGuardDutyConfig() {
        GuardDutyConfig config = GuardDutyConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomGuardDutyConfig() {
        GuardDutyConfig config = GuardDutyConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GuardDutyConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GUARDDUTY_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GuardDutyConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_GUARDDUTY_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GuardDutyConfig config = GuardDutyConfig.builder()
                .enabled(false)
                .build();
        GuardDutyConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
