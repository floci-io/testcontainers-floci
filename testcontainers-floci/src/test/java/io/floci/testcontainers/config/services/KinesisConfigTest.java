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

    @Test
    void shouldApplyListShardsNextTokenTtlMillis() {
        KinesisConfig defaults = KinesisConfig.builder().build();
        assertThat(defaults.getListShardsNextTokenTtlMillis()).isEqualTo(300000L);

        KinesisConfig config = KinesisConfig.builder().listShardsNextTokenTtlMillis(1000L).build();
        assertThat(config.getListShardsNextTokenTtlMillis()).isEqualTo(1000L);
        assertThat(config.toBuilder().build().getListShardsNextTokenTtlMillis()).isEqualTo(1000L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", "1000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", "300000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS");
    }

}
