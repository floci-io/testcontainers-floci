package io.floci.testcontainers.core;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.testcontainers.containers.TransferableCopyInspector;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractFlociContainerTest {

    private static final String DOCKER_SOCKET_PATH = "/var/run/docker.sock";

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new TestFlociContainer(DockerImageName.parse("other/image:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldAcceptCompatibleImage() {
        DockerImageName imageName = DockerImageName.parse("my-registry/floci-test:1.0")
                .asCompatibleSubstituteFor("floci/floci-test");

        assertThatCode(() -> new TestFlociContainer(imageName).close()).doesNotThrowAnyException();
    }

    @Test
    void shouldExposeEmulatorPort() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getPort()).isEqualTo(TestFlociContainer.PORT);
            assertThat(container.getExposedPorts()).contains(TestFlociContainer.PORT);
        }
    }

    @Test
    void shouldExposeDescriptor() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getDescriptor()).isSameAs(TestFlociContainer.DESCRIPTOR);
        }
    }

    @Test
    void shouldApplyDefaultEnvOfDescriptor() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getEnvMap()).containsEntry("FLOCI_TEST_TLS_HTTPS_PORT", "0");
        }
    }

    @Test
    void shouldLetCallerOverrideDefaultEnv() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withEnv("FLOCI_TEST_TLS_HTTPS_PORT", "8443");

            assertThat(container.getEnvMap()).containsEntry("FLOCI_TEST_TLS_HTTPS_PORT", "8443");
        }
    }

    @Test
    void shouldWaitForHealthPath() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.waitStrategy()).isInstanceOf(HttpWaitStrategy.class);
            assertThat(container.waitStrategy()).extracting("path").isEqualTo(TestFlociContainer.HEALTH_PATH);
        }
    }

    @Test
    void shouldHaveNoResourceNamespaceBeforeStart() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getResourceNamespace()).isNull();
        }
    }

    @Test
    void shouldReturnCallerSetResourceNamespace() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withEnv(TestFlociContainer.RESOURCE_NAMESPACE_ENV_VAR, "my-namespace");

            assertThat(container.getResourceNamespace()).isEqualTo("my-namespace");
        }
    }

    @Test
    void shouldRejectResetWhenNotRunning() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThatThrownBy(container::reset)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not running");
        }
    }

    @Test
    void shouldApplyAllConfigsOnConstruction() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_TEST_GLOBAL_SETTING", "default")
                    .containsEntry("FLOCI_TEST_SERVICES_SIMPLE_ENABLED", "true")
                    .containsEntry("FLOCI_TEST_SERVICES_SIMPLE_VALUE", "default")
                    .containsEntry("FLOCI_TEST_SERVICES_DOCKER_BACKED_ENABLED", "true")
                    .containsEntry("FLOCI_TEST_SERVICES_DOCKER_BACKED_MOCK", "false");
            assertThat(container.getExposedPorts())
                    .containsExactlyInAnyOrder(TestFlociContainer.PORT, DockerBackedServiceConfig.SERVICE_PORT);
        }
    }

    @Test
    void shouldApplyUpdatedServiceConfig() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withSimpleConfig(c -> c.value("custom"));

            assertThat(container.getSimpleConfig().getValue()).isEqualTo("custom");
            assertThat(container.getEnvMap()).containsEntry("FLOCI_TEST_SERVICES_SIMPLE_VALUE", "custom");
        }
    }

    @Test
    void shouldPreserveOtherValuesOnServiceConfigUpdate() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withSimpleConfig(c -> c.value("custom"))
                    .withSimpleConfig(c -> c.enabled(true));

            assertThat(container.getSimpleConfig().getValue()).isEqualTo("custom");
        }
    }

    @Test
    void shouldRecalculateExposedPortsOnServiceConfigUpdate() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withDockerBackedConfig(c -> c.enabled(false));
            assertThat(container.getExposedPorts()).containsExactly(TestFlociContainer.PORT);

            container.withDockerBackedConfig(c -> c.enabled(true));
            assertThat(container.getExposedPorts())
                    .containsExactlyInAnyOrder(TestFlociContainer.PORT, DockerBackedServiceConfig.SERVICE_PORT);
        }
    }

    @Test
    void shouldMountFilesIntoContainer() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withDockerBackedConfig(c -> c.fileContent("{}"));

            assertThat(TransferableCopyInspector.contentCopiedTo(container, DockerBackedServiceConfig.FILE_PATH))
                    .contains("{}");

            // File mounts survive later, unrelated service-config changes.
            container.withSimpleConfig(c -> c.value("custom"));
            assertThat(TransferableCopyInspector.contentCopiedTo(container, DockerBackedServiceConfig.FILE_PATH))
                    .contains("{}");
        }
    }

    @Test
    void shouldReturnCurrentServiceConfigsInRegistrationOrder() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withSimpleConfig(c -> c.value("custom"));

            assertThat(container.getServiceConfigs())
                    .containsExactly(container.getSimpleConfig(), container.getDockerBackedConfig());
            assertThatThrownBy(() -> container.getServiceConfigs().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    void shouldDisableAllServices() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withSimpleConfig(c -> c.value("custom"))
                    .disableAllServices();

            assertThat(container.getSimpleConfig().isEnabled()).isFalse();
            assertThat(container.getSimpleConfig().getValue()).isEqualTo("custom");
            assertThat(container.getDockerBackedConfig().isEnabled()).isFalse();
            assertThat(container.getEnvMap())
                    .containsEntry("FLOCI_TEST_SERVICES_SIMPLE_ENABLED", "false")
                    .containsEntry("FLOCI_TEST_SERVICES_DOCKER_BACKED_ENABLED", "false")
                    .containsEntry("FLOCI_TEST_GLOBAL_SETTING", "default");
            assertThat(container.getExposedPorts()).containsExactly(TestFlociContainer.PORT);
        }
    }

    @Test
    void shouldKeepGlobalSettingsOnDisableAllServices() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withGlobalSetting("custom")
                    .disableAllServices();

            assertThat(container.getEnvMap()).containsEntry("FLOCI_TEST_GLOBAL_SETTING", "custom");
        }
    }

    @Test
    void shouldReturnDefaultLogLevel() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);
            assertThat(container.getEnvMap()).containsEntry(TestFlociContainer.LOG_LEVEL_ENV_VAR, "WARN");
        }
    }

    @Test
    void shouldReturnCustomLogLevel() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withLogLevel(Level.DEBUG);

            assertThat(container.getLogLevel()).isEqualTo(Level.DEBUG);
            assertThat(container.getEnvMap()).containsEntry(TestFlociContainer.LOG_LEVEL_ENV_VAR, "DEBUG");
        }
    }

    @Test
    void shouldFallbackToWarnForInvalidLogLevel() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withEnv(TestFlociContainer.LOG_LEVEL_ENV_VAR, "INVALID");

            assertThat(container.getLogLevel()).isEqualTo(Level.WARN);
        }
    }

    @Test
    void shouldConfigureDedicatedNetwork() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            assertThat(container.getDedicatedNetworkName()).isNull();

            container.withDedicatedNetwork();

            String networkName = container.getDedicatedNetworkName();
            assertThat(networkName).startsWith("floci-network-");
            assertThat(networkName).hasSize("floci-network-".length() + 8);
            assertThat(container.getEnvMap()).containsEntry(TestFlociContainer.DOCKER_NETWORK_ENV_VAR, networkName);
            assertThat(container.getNetwork()).isNotNull();
        }
    }

    @Test
    void shouldCreateUniqueNetworkPerCall() {
        try (TestFlociContainer container1 = new TestFlociContainer();
             TestFlociContainer container2 = new TestFlociContainer()) {
            container1.withDedicatedNetwork();
            container2.withDedicatedNetwork();

            assertThat(container1.getDedicatedNetworkName()).isNotEqualTo(container2.getDedicatedNetworkName());
        }
    }

    @Test
    void shouldBindDockerSocketWhenDockerBackedServiceIsEnabledAndNotMocked() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.configure();

            assertThat(container.getBinds()).anyMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldBindDockerSocketOnlyOnceWhenConfiguredRepeatedly() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.configure();
            container.configure();

            assertThat(container.getBinds())
                    .filteredOn(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()))
                    .hasSize(1);
        }
    }

    @Test
    void shouldNotBindDockerSocketWhenDockerBackedServiceIsMocked() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withDockerBackedConfig(c -> c.mock(true))
                    .configure();

            assertThat(container.getBinds()).noneMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldNotBindDockerSocketWhenNoDockerBackedServiceIsEnabled() {
        try (TestFlociContainer container = new TestFlociContainer().disableAllServices()) {
            container.withSimpleConfig(c -> c.enabled(true))
                    .configure();

            assertThat(container.getBinds()).noneMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldOverrideAutoDetectionWhenDockerSocketExplicitlyEnabled() {
        try (TestFlociContainer container = new TestFlociContainer().disableAllServices()) {
            container.withDockerSocket(true)
                    .configure();

            assertThat(container.getBinds()).anyMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldOverrideAutoDetectionWhenDockerSocketExplicitlyDisabled() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.withDockerSocket(false)
                    .configure();

            assertThat(container.getBinds()).noneMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }

    @Test
    void shouldUnbindDockerSocketWhenConfigChangesAfterRestart() {
        try (TestFlociContainer container = new TestFlociContainer()) {
            container.configure();
            assertThat(container.getBinds()).anyMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));

            container.disableAllServices().configure();
            assertThat(container.getBinds()).noneMatch(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath()));
        }
    }
}
