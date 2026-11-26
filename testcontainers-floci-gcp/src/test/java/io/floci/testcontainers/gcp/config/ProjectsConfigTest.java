package io.floci.testcontainers.gcp.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ProjectsConfigTest {

    @Test
    void shouldApplyDefaultProjectsConfig() {
        ProjectsConfig config = ProjectsConfig.builder().build();
        assertThat(config.isStrict()).isFalse();
    }

    @Test
    void shouldApplyCustomProjectsConfig() {
        ProjectsConfig config = ProjectsConfig.builder()
                .strict(true)
                .build();
        assertThat(config.isStrict()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProjectsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_PROJECTS_STRICT", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ProjectsConfig.builder()
                .strict(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_PROJECTS_STRICT", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ProjectsConfig config = ProjectsConfig.builder()
                .strict(true)
                .build();

        ProjectsConfig copy = config.toBuilder().build();

        assertThat(copy.isStrict()).isTrue();
    }
}
