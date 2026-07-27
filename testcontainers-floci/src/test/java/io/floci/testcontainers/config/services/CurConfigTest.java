package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CurConfigTest {

    @Test
    void shouldApplyDefaultCurConfig() {
        CurConfig config = CurConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getEmitMode()).isEqualTo("synchronous");
        assertThat(config.getStagingBucket()).isEqualTo("floci-cur-staging");
    }

    @Test
    void shouldApplyCustomCurConfig() {
        CurConfig config = CurConfig.builder()
                .enabled(false)
                .emitMode("daily")
                .stagingBucket("my-staging-bucket")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getEmitMode()).isEqualTo("daily");
        assertThat(config.getStagingBucket()).isEqualTo("my-staging-bucket");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CurConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CUR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CUR_EMIT_MODE", "synchronous")
                .containsEntry("FLOCI_SERVICES_CUR_STAGING_BUCKET", "floci-cur-staging");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CurConfig.builder()
                .enabled(true)
                .emitMode("off")
                .stagingBucket("custom-staging")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CUR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CUR_EMIT_MODE", "off")
                .containsEntry("FLOCI_SERVICES_CUR_STAGING_BUCKET", "custom-staging");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CurConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CUR_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CurConfig config = CurConfig.builder()
                .enabled(false)
                .emitMode("asynchronous")
                .stagingBucket("test-bucket")
                .build();
        CurConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getEmitMode()).isEqualTo("asynchronous");
        assertThat(copy.getStagingBucket()).isEqualTo("test-bucket");
    }

}
