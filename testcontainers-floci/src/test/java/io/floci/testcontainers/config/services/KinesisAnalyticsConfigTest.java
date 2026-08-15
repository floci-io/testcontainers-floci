package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class KinesisAnalyticsConfigTest {

    @Test
    void shouldApplyDefaultKinesisAnalyticsConfig() {
        KinesisAnalyticsConfig config = KinesisAnalyticsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEmpty();
    }

    @Test
    void shouldApplyCustomKinesisAnalyticsConfig() {
        KinesisAnalyticsConfig config = KinesisAnalyticsConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("apache/flink:1.20")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).contains("apache/flink:1.20");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisAnalyticsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_KINESIS_ANALYTICS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_KINESIS_ANALYTICS_MOCK", "false")
                .doesNotContainKey("FLOCI_SERVICES_KINESIS_ANALYTICS_DEFAULT_IMAGE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KinesisAnalyticsConfig.builder()
                .mock(true)
                .defaultImage("apache/flink:1.20")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_KINESIS_ANALYTICS_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_KINESIS_ANALYTICS_DEFAULT_IMAGE", "apache/flink:1.20");
    }

    @Test
    void shouldNotApplyServiceEnvVarsWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        KinesisAnalyticsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_KINESIS_ANALYTICS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_KINESIS_ANALYTICS_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_KINESIS_ANALYTICS_DEFAULT_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        KinesisAnalyticsConfig config = KinesisAnalyticsConfig.builder()
                .mock(true)
                .defaultImage("apache/flink:1.20")
                .build();

        KinesisAnalyticsConfig copy = config.toBuilder().build();

        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).contains("apache/flink:1.20");
    }

}
