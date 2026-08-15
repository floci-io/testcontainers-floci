package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudTrailConfigTest {

    @Test
    void shouldApplyDefaultCloudTrailConfig() {
        CloudTrailConfig config = CloudTrailConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getFlushIntervalSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyCustomCloudTrailConfig() {
        CloudTrailConfig config = CloudTrailConfig.builder()
                .enabled(false)
                .flushIntervalSeconds(10)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getFlushIntervalSeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudTrailConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDTRAIL_FLUSH_INTERVAL_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudTrailConfig.builder()
                .flushIntervalSeconds(10)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDTRAIL_FLUSH_INTERVAL_SECONDS", "10");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudTrailConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDTRAIL_FLUSH_INTERVAL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudTrailConfig config = CloudTrailConfig.builder()
                .enabled(false)
                .flushIntervalSeconds(10)
                .build();
        CloudTrailConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getFlushIntervalSeconds()).isEqualTo(10);
    }

}
