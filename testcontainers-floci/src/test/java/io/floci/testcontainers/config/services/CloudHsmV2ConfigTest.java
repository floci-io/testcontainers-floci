package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudHsmV2ConfigTest {

    @Test
    void shouldApplyDefaultCloudHsmV2Config() {
        CloudHsmV2Config config = CloudHsmV2Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCloudHsmV2Config() {
        CloudHsmV2Config config = CloudHsmV2Config.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudHsmV2Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDHSMV2_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudHsmV2Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDHSMV2_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudHsmV2Config config = CloudHsmV2Config.builder()
                .enabled(false)
                .build();
        CloudHsmV2Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
