package io.floci.testcontainers.oci.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PsqlConfigTest {

    @Test
    void shouldApplyDefaultPsqlConfig() {
        PsqlConfig config = PsqlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getImage()).isEqualTo("postgres:{major}-alpine");
        assertThat(config.getBasePort()).isEqualTo(15432);
        assertThat(config.getPortsCount()).isEqualTo(10);
        assertThat(config.getMaxPort()).isEqualTo(15441);
        assertThat(config.getReadyTimeoutSeconds()).isEqualTo(120);
    }

    @Test
    void shouldApplyCustomPsqlConfig() {
        PsqlConfig config = PsqlConfig.builder()
                .enabled(false)
                .mock(true)
                .image("postgres:{major}")
                .portRange(25432, 5)
                .readyTimeoutSeconds(60)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getImage()).isEqualTo("postgres:{major}");
        assertThat(config.getBasePort()).isEqualTo(25432);
        assertThat(config.getPortsCount()).isEqualTo(5);
        assertThat(config.getMaxPort()).isEqualTo(25436);
        assertThat(config.getReadyTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PsqlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_MOCK", "false")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_IMAGE", "postgres:{major}-alpine")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_BASE_PORT", "15432")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_MAX_PORT", "15441")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_READY_TIMEOUT_SECONDS", "120");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PsqlConfig.builder()
                .mock(true)
                .image("postgres:{major}")
                .portRange(25432, 5)
                .readyTimeoutSeconds(60)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_ENABLED", "true")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_MOCK", "true")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_IMAGE", "postgres:{major}")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_BASE_PORT", "25432")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_MAX_PORT", "25436")
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_READY_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PsqlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_OCI_SERVICES_PSQL_ENABLED", "false")
                .doesNotContainKey("FLOCI_OCI_SERVICES_PSQL_MOCK")
                .doesNotContainKey("FLOCI_OCI_SERVICES_PSQL_IMAGE")
                .doesNotContainKey("FLOCI_OCI_SERVICES_PSQL_BASE_PORT")
                .doesNotContainKey("FLOCI_OCI_SERVICES_PSQL_MAX_PORT")
                .doesNotContainKey("FLOCI_OCI_SERVICES_PSQL_READY_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PsqlConfig config = PsqlConfig.builder()
                .enabled(false)
                .mock(true)
                .image("postgres:{major}")
                .portRange(25432, 5)
                .readyTimeoutSeconds(60)
                .build();
        PsqlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getImage()).isEqualTo("postgres:{major}");
        assertThat(copy.getBasePort()).isEqualTo(25432);
        assertThat(copy.getPortsCount()).isEqualTo(5);
        assertThat(copy.getMaxPort()).isEqualTo(25436);
        assertThat(copy.getReadyTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(PsqlConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(PsqlConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(PsqlConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
