package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BcmDataExportsConfigTest {

    @Test
    void shouldApplyDefaultBcmDataExportsConfig() {
        BcmDataExportsConfig config = BcmDataExportsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getEmitMode()).isEqualTo("synchronous");
    }

    @Test
    void shouldApplyCustomBcmDataExportsConfig() {
        BcmDataExportsConfig config = BcmDataExportsConfig.builder()
                .enabled(false)
                .emitMode("daily")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getEmitMode()).isEqualTo("daily");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BcmDataExportsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BCM_DATA_EXPORTS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_BCM_DATA_EXPORTS_EMIT_MODE", "synchronous");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BcmDataExportsConfig.builder()
                .enabled(true)
                .emitMode("off")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_BCM_DATA_EXPORTS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_BCM_DATA_EXPORTS_EMIT_MODE", "off");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BcmDataExportsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BCM_DATA_EXPORTS_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BcmDataExportsConfig config = BcmDataExportsConfig.builder()
                .enabled(false)
                .emitMode("asynchronous")
                .build();
        BcmDataExportsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getEmitMode()).isEqualTo("asynchronous");
    }

}
