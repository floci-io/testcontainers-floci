package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class KinesisConfigTest {

    @Test
    void shouldApplyDefaultKinesisConfig() {
        KinesisConfig config = KinesisConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomKinesisConfig() {
        KinesisConfig config = KinesisConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_KINESIS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_KINESIS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        KinesisConfig config = KinesisConfig.builder()
                .enabled(false)
                .build();
        KinesisConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
