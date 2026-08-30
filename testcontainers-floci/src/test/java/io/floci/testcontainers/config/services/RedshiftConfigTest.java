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
}
