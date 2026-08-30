package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class NetworkFirewallConfigTest {

    @Test
    void shouldApplyDefaultNetworkFirewallConfig() {
        NetworkFirewallConfig config = NetworkFirewallConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomNetworkFirewallConfig() {
        NetworkFirewallConfig config = NetworkFirewallConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        NetworkFirewallConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_NETWORKFIREWALL_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        NetworkFirewallConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_NETWORKFIREWALL_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        NetworkFirewallConfig config = NetworkFirewallConfig.builder()
                .enabled(false)
                .build();
        NetworkFirewallConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
