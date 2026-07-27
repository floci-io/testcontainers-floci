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
    }

    @Test
    void shouldApplyCustomCloudTrailConfig() {
        CloudTrailConfig config = CloudTrailConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudTrailConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudTrailConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDTRAIL_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudTrailConfig config = CloudTrailConfig.builder()
                .enabled(false)
                .build();
        CloudTrailConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
