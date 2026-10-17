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
        assertThat(config.getListShardsNextTokenTtlMillis()).isEqualTo(300000L);
    }

    @Test
    void shouldApplyCustomKinesisConfig() {
        KinesisConfig config = KinesisConfig.builder()
                .enabled(false)
                .listShardsNextTokenTtlMillis(1000L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getListShardsNextTokenTtlMillis()).isEqualTo(1000L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_KINESIS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", "300000");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisConfig.builder()
                .listShardsNextTokenTtlMillis(1000L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", "1000");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_KINESIS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        KinesisConfig config = KinesisConfig.builder()
                .enabled(false)
                .listShardsNextTokenTtlMillis(1000L)
                .build();
        KinesisConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getListShardsNextTokenTtlMillis()).isEqualTo(1000L);
    }

}
