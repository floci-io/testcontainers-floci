package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PolicyConfigTest {

    @Test
    void shouldApplyDefaultPolicyConfig() {
        PolicyConfig config = PolicyConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isEnforcement()).isTrue();
        assertThat(config.isInsights()).isTrue();
    }

    @Test
    void shouldApplyCustomPolicyConfig() {
        PolicyConfig config = PolicyConfig.builder()
                .enabled(false)
                .enforcement(false)
                .insights(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isEnforcement()).isFalse();
        assertThat(config.isInsights()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PolicyConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_ENFORCEMENT", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_INSIGHTS", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PolicyConfig.builder()
                .enforcement(false)
                .insights(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_ENFORCEMENT", "false")
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_INSIGHTS", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PolicyConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POLICY_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POLICY_ENFORCEMENT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POLICY_INSIGHTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PolicyConfig config = PolicyConfig.builder()
                .enabled(false)
                .enforcement(false)
                .insights(false)
                .build();
        PolicyConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isEnforcement()).isFalse();
        assertThat(copy.isInsights()).isFalse();
    }
}
