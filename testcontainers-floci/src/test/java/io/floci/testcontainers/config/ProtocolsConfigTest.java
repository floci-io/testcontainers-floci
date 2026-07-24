package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ProtocolsConfigTest {

    @Test
    void shouldApplyDefaultProtocolsConfig() {
        ProtocolsConfig config = ProtocolsConfig.builder().build();
        assertThat(config.isStrictClaiming()).isFalse();
    }

    @Test
    void shouldApplyCustomProtocolsConfig() {
        ProtocolsConfig config = ProtocolsConfig.builder()
                .strictClaiming(true)
                .build();
        assertThat(config.isStrictClaiming()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProtocolsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PROTOCOLS_STRICT_CLAIMING", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProtocolsConfig.builder()
                .strictClaiming(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PROTOCOLS_STRICT_CLAIMING", "true");
    }
}
