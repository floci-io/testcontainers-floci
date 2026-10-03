package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftDataConfigTest {

    @Test
    void shouldApplyDefaultRedshiftDataConfig() {
        RedshiftDataConfig config = RedshiftDataConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomRedshiftDataConfig() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .build();
        RedshiftDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyResultTtlHours() {
        RedshiftDataConfig defaults = RedshiftDataConfig.builder().build();
        assertThat(defaults.getResultTtlHours()).isEqualTo(24);

        RedshiftDataConfig config = RedshiftDataConfig.builder().resultTtlHours(1).build();
        assertThat(config.getResultTtlHours()).isEqualTo(1);
        assertThat(config.toBuilder().build().getResultTtlHours()).isEqualTo(1);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "1");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "24");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS");
    }

}
