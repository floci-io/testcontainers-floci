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
        assertThat(config.getResultTtlHours()).isEqualTo(24);
    }

    @Test
    void shouldApplyCustomRedshiftDataConfig() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .resultTtlHours(1)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getResultTtlHours()).isEqualTo(1);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "24");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder()
                .resultTtlHours(1)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", "1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftDataConfig config = RedshiftDataConfig.builder()
                .enabled(false)
                .resultTtlHours(1)
                .build();
        RedshiftDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getResultTtlHours()).isEqualTo(1);
    }

}
