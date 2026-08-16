package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class InitHooksConfigTest {

    @Test
    void shouldApplyDefaultInitHooksConfig() {
        InitHooksConfig config = InitHooksConfig.builder().build();
        assertThat(config.getShellExecutable()).isEqualTo("/bin/sh");
        assertThat(config.getShutdownGracePeriodSeconds()).isEqualTo(2);
        assertThat(config.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyCustomInitHooksConfig() {
        InitHooksConfig config = InitHooksConfig.builder()
                .shellExecutable("/bin/bash")
                .shutdownGracePeriodSeconds(5)
                .timeoutSeconds(60)
                .build();
        assertThat(config.getShellExecutable()).isEqualTo("/bin/bash");
        assertThat(config.getShutdownGracePeriodSeconds()).isEqualTo(5);
        assertThat(config.getTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        InitHooksConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_INIT_HOOKS_SHELL_EXECUTABLE", "/bin/sh")
                .containsEntry("FLOCI_INIT_HOOKS_SHUTDOWN_GRACE_PERIOD_SECONDS", "2")
                .containsEntry("FLOCI_INIT_HOOKS_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        InitHooksConfig.builder()
                .shellExecutable("/bin/bash")
                .shutdownGracePeriodSeconds(5)
                .timeoutSeconds(60)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_INIT_HOOKS_SHELL_EXECUTABLE", "/bin/bash")
                .containsEntry("FLOCI_INIT_HOOKS_SHUTDOWN_GRACE_PERIOD_SECONDS", "5")
                .containsEntry("FLOCI_INIT_HOOKS_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        InitHooksConfig config = InitHooksConfig.builder()
                .shellExecutable("/bin/bash")
                .shutdownGracePeriodSeconds(5)
                .timeoutSeconds(60)
                .build();

        InitHooksConfig copy = config.toBuilder().build();

        assertThat(copy.getShellExecutable()).isEqualTo("/bin/bash");
        assertThat(copy.getShutdownGracePeriodSeconds()).isEqualTo(5);
        assertThat(copy.getTimeoutSeconds()).isEqualTo(60);
    }
}
