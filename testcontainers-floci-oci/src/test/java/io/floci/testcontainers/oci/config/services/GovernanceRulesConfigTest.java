package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class GovernanceRulesConfigTest {

    @Test
    void shouldApplyDefaultGovernanceRulesConfig() {
        GovernanceRulesConfig config = GovernanceRulesConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomGovernanceRulesConfig() {
        GovernanceRulesConfig config = GovernanceRulesConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GovernanceRulesConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_GOVERNANCERULES_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        GovernanceRulesConfig.builder()
                .enabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_GOVERNANCERULES_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        GovernanceRulesConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_GOVERNANCERULES_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        GovernanceRulesConfig config = GovernanceRulesConfig.builder()
                .enabled(false)
                .build();
        GovernanceRulesConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
