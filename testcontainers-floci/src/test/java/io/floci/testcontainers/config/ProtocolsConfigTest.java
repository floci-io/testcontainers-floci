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
        assertThat(config.isRejectUnknownServiceScope()).isTrue();
    }

    @Test
    void shouldApplyCustomProtocolsConfig() {
        ProtocolsConfig config = ProtocolsConfig.builder()
                .strictClaiming(true)
                .rejectUnknownServiceScope(false)
                .build();
        assertThat(config.isStrictClaiming()).isTrue();
        assertThat(config.isRejectUnknownServiceScope()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProtocolsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PROTOCOLS_STRICT_CLAIMING", "false")
                .containsEntry("FLOCI_PROTOCOLS_REJECT_UNKNOWN_SERVICE_SCOPE", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProtocolsConfig.builder()
                .strictClaiming(true)
                .rejectUnknownServiceScope(false)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PROTOCOLS_STRICT_CLAIMING", "true")
                .containsEntry("FLOCI_PROTOCOLS_REJECT_UNKNOWN_SERVICE_SCOPE", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ProtocolsConfig config = ProtocolsConfig.builder()
                .strictClaiming(true)
                .rejectUnknownServiceScope(false)
                .build();

        ProtocolsConfig copy = config.toBuilder().build();

        assertThat(copy.isStrictClaiming()).isTrue();
        assertThat(copy.isRejectUnknownServiceScope()).isFalse();
    }
}
