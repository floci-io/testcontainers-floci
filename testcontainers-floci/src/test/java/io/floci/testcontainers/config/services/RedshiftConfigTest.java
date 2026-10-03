package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedshiftConfigTest {

    @Test
    void shouldApplyDefaultRedshiftConfig() {
        RedshiftConfig config = RedshiftConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultPort()).isEqualTo(5439);
        assertThat(config.getImageVersion()).isEqualTo("postgres:15-alpine");
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomRedshiftConfig() {
        RedshiftConfig config = RedshiftConfig.builder()
                .enabled(false)
                .defaultPort(5000)
                .imageVersion("postgres:16-alpine")
                .dockerNetwork("my-redshift-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultPort()).isEqualTo(5000);
        assertThat(config.getImageVersion()).isEqualTo("postgres:16-alpine");
        assertThat(config.getDockerNetwork()).isEqualTo("my-redshift-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", "5439")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", "postgres:15-alpine")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder()
                .defaultPort(5000)
                .imageVersion("postgres:16-alpine")
                .dockerNetwork("my-redshift-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", "5000")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", "postgres:16-alpine")
                .containsEntry("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK", "my-redshift-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedshiftConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_REDSHIFT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT")
                .doesNotContainKey("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedshiftConfig config = RedshiftConfig.builder()
                .enabled(false)
                .defaultPort(5000)
                .imageVersion("test-image")
                .dockerNetwork("test-network")
                .build();
        RedshiftConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultPort()).isEqualTo(5000);
        assertThat(copy.getImageVersion()).isEqualTo("test-image");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
    }

    @Test
    void shouldRequireDockerSocketOnlyWhenEnabled() {
        assertThat(RedshiftConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(RedshiftConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldApplyPollIntervalMs() {
        RedshiftConfig defaults = RedshiftConfig.builder().build();
        assertThat(defaults.getPollIntervalMs()).isEqualTo(1000L);

        RedshiftConfig config = RedshiftConfig.builder().pollIntervalMs(250L).build();
        assertThat(config.getPollIntervalMs()).isEqualTo(250L);
        assertThat(config.toBuilder().build().getPollIntervalMs()).isEqualTo(250L);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS", "250");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS", "1000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS");
    }

    @Test
    void shouldApplyDefaultCredentialDurationSeconds() {
        RedshiftConfig defaults = RedshiftConfig.builder().build();
        assertThat(defaults.getDefaultCredentialDurationSeconds()).isEqualTo(900);

        RedshiftConfig config = RedshiftConfig.builder().defaultCredentialDurationSeconds(3600).build();
        assertThat(config.getDefaultCredentialDurationSeconds()).isEqualTo(3600);
        assertThat(config.toBuilder().build().getDefaultCredentialDurationSeconds()).isEqualTo(3600);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS", "3600");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS", "900");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS");
    }

    @Test
    void shouldApplyProxyHandshakeTimeoutMillis() {
        RedshiftConfig defaults = RedshiftConfig.builder().build();
        assertThat(defaults.getProxyHandshakeTimeoutMillis()).isEqualTo(10000);

        RedshiftConfig config = RedshiftConfig.builder().proxyHandshakeTimeoutMillis(2000).build();
        assertThat(config.getProxyHandshakeTimeoutMillis()).isEqualTo(2000);
        assertThat(config.toBuilder().build().getProxyHandshakeTimeoutMillis()).isEqualTo(2000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "2000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS", "10000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS");
    }

    @Test
    void shouldApplyProxyBackendConnectTimeoutMillis() {
        RedshiftConfig defaults = RedshiftConfig.builder().build();
        assertThat(defaults.getProxyBackendConnectTimeoutMillis()).isEqualTo(5000);

        RedshiftConfig config = RedshiftConfig.builder().proxyBackendConnectTimeoutMillis(1000).build();
        assertThat(config.getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);
        assertThat(config.toBuilder().build().getProxyBackendConnectTimeoutMillis()).isEqualTo(1000);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "1000");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", "5000");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS");
    }

    @Test
    void shouldApplyProxyMaxConnections() {
        RedshiftConfig defaults = RedshiftConfig.builder().build();
        assertThat(defaults.getProxyMaxConnections()).isEqualTo(100);

        RedshiftConfig config = RedshiftConfig.builder().proxyMaxConnections(20).build();
        assertThat(config.getProxyMaxConnections()).isEqualTo(20);
        assertThat(config.toBuilder().build().getProxyMaxConnections()).isEqualTo(20);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS", "20");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS", "100");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS");
    }

}
