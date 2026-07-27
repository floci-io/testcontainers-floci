package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PricingConfigTest {

    @Test
    void shouldApplyDefaultPricingConfig() {
        PricingConfig config = PricingConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getSnapshotPath()).isEmpty();
    }

    @Test
    void shouldApplyCustomPricingConfig() {
        PricingConfig config = PricingConfig.builder()
                .enabled(false)
                .snapshotPath("/data/pricing")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSnapshotPath()).contains("/data/pricing");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PricingConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PRICING_ENABLED", "true");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_PRICING_SNAPSHOT_PATH");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PricingConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PRICING_ENABLED", "false");
    }

    @Test
    void shouldApplySnapshotPathEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PricingConfig.builder().snapshotPath("/data/pricing").build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PRICING_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PRICING_SNAPSHOT_PATH", "/data/pricing");
    }

    @Test
    void shouldNotApplySnapshotPathWhenNotSet() {
        GenericContainer<?> container = genericContainer();
        PricingConfig.builder().snapshotPath(null).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_PRICING_SNAPSHOT_PATH");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PricingConfig config = PricingConfig.builder()
                .enabled(false)
                .snapshotPath("test-path")
                .build();
        PricingConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSnapshotPath()).contains("test-path");
    }

}
