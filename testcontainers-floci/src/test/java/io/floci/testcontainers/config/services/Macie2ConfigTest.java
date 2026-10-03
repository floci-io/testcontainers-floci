package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Macie2ConfigTest {

    @Test
    void shouldApplyDefaultMacie2Config() {
        Macie2Config config = Macie2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomMacie2Config() {
        Macie2Config config = Macie2Config.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Macie2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_MACIE2_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Macie2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_MACIE2_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Macie2Config config = Macie2Config.builder()
                .enabled(false)
                .build();
        Macie2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
