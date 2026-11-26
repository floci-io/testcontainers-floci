package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Ec2InstanceConnectConfigTest {

    @Test
    void shouldApplyDefaultEc2InstanceConnectConfig() {
        Ec2InstanceConnectConfig config = Ec2InstanceConnectConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomEc2InstanceConnectConfig() {
        Ec2InstanceConnectConfig config = Ec2InstanceConnectConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2InstanceConnectConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2INSTANCECONNECT_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Ec2InstanceConnectConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_EC2INSTANCECONNECT_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Ec2InstanceConnectConfig config = Ec2InstanceConnectConfig.builder()
                .enabled(false)
                .build();
        Ec2InstanceConnectConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}
