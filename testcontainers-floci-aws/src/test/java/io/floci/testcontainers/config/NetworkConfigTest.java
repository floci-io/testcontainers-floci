package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class NetworkConfigTest {

    @Test
    void shouldApplyDefaultNetworkConfig() {
        NetworkConfig config = NetworkConfig.builder().build();
        assertThat(config.isSecurityGroupEnforcementEnabled()).isFalse();
        assertThat(config.getSecurityGroupEnforcementHelperImage()).isEqualTo("floci/network-helper:local");
    }

    @Test
    void shouldApplyCustomNetworkConfig() {
        NetworkConfig config = NetworkConfig.builder()
                .securityGroupEnforcementEnabled(true)
                .securityGroupEnforcementHelperImage("registry.example.com/network-helper:1.0")
                .build();
        assertThat(config.isSecurityGroupEnforcementEnabled()).isTrue();
        assertThat(config.getSecurityGroupEnforcementHelperImage()).isEqualTo("registry.example.com/network-helper:1.0");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        NetworkConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_ENABLED", "false")
                .containsEntry("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE", "floci/network-helper:local");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        NetworkConfig.builder()
                .securityGroupEnforcementEnabled(true)
                .securityGroupEnforcementHelperImage("registry.example.com/network-helper:1.0")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_ENABLED", "true")
                .containsEntry("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE", "registry.example.com/network-helper:1.0");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        NetworkConfig config = NetworkConfig.builder()
                .securityGroupEnforcementEnabled(true)
                .securityGroupEnforcementHelperImage("registry.example.com/network-helper:1.0")
                .build();

        NetworkConfig copy = config.toBuilder().build();

        assertThat(copy.isSecurityGroupEnforcementEnabled()).isTrue();
        assertThat(copy.getSecurityGroupEnforcementHelperImage()).isEqualTo("registry.example.com/network-helper:1.0");
    }
}
