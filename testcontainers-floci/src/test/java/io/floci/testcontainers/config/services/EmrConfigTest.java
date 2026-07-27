package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EmrConfigTest {

    @Test
    void shouldApplyDefaultEmrConfig() {
        EmrConfig config = EmrConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultReleaseLabel()).isEqualTo("emr-7.5.0");
        assertThat(config.getClusterStartupDelaySeconds()).isZero();
    }

    @Test
    void shouldApplyCustomEmrConfig() {
        EmrConfig config = EmrConfig.builder()
                .enabled(false)
                .defaultReleaseLabel("emr-7.8.0")
                .clusterStartupDelaySeconds(10)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultReleaseLabel()).isEqualTo("emr-7.8.0");
        assertThat(config.getClusterStartupDelaySeconds()).isEqualTo(10);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", "emr-7.5.0")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder()
                .defaultReleaseLabel("emr-7.8.0")
                .clusterStartupDelaySeconds(10)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", "emr-7.8.0")
                .containsEntry("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS", "10");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EmrConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_EMR_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL")
                .doesNotContainKey("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EmrConfig config = EmrConfig.builder()
                .enabled(false)
                .defaultReleaseLabel("emr-6.0.0")
                .clusterStartupDelaySeconds(5)
                .build();
        EmrConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultReleaseLabel()).isEqualTo("emr-6.0.0");
        assertThat(copy.getClusterStartupDelaySeconds()).isEqualTo(5);
    }

}
