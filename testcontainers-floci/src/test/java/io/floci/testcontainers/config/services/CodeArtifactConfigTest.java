package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CodeArtifactConfigTest {

    @Test
    void shouldApplyDefaultCodeArtifactConfig() {
        CodeArtifactConfig config = CodeArtifactConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomCodeArtifactConfig() {
        CodeArtifactConfig config = CodeArtifactConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeArtifactConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeArtifactConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodeArtifactConfig config = CodeArtifactConfig.builder()
                .enabled(false)
                .build();
        CodeArtifactConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyMavenUrl() {
        CodeArtifactConfig defaults = CodeArtifactConfig.builder().build();
        assertThat(defaults.getMavenUrl()).isEmpty();

        CodeArtifactConfig config = CodeArtifactConfig.builder().mavenUrl("http://reposilite:8080").build();
        assertThat(config.getMavenUrl()).contains("http://reposilite:8080");
        assertThat(config.toBuilder().build().getMavenUrl()).contains("http://reposilite:8080");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL", "http://reposilite:8080");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL");
    }

    @Test
    void shouldApplyMavenToken() {
        CodeArtifactConfig defaults = CodeArtifactConfig.builder().build();
        assertThat(defaults.getMavenToken()).isEmpty();

        CodeArtifactConfig config = CodeArtifactConfig.builder().mavenToken("admin:secret").build();
        assertThat(config.getMavenToken()).contains("admin:secret");
        assertThat(config.toBuilder().build().getMavenToken()).contains("admin:secret");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN", "admin:secret");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN");
    }

    @Test
    void shouldApplyMavenImage() {
        CodeArtifactConfig defaults = CodeArtifactConfig.builder().build();
        assertThat(defaults.getMavenImage()).isEqualTo("dzikoysk/reposilite:3.6.3");

        CodeArtifactConfig config = CodeArtifactConfig.builder().mavenImage("dzikoysk/reposilite:3.7.0").build();
        assertThat(config.getMavenImage()).isEqualTo("dzikoysk/reposilite:3.7.0");
        assertThat(config.toBuilder().build().getMavenImage()).isEqualTo("dzikoysk/reposilite:3.7.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE", "dzikoysk/reposilite:3.7.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE", "dzikoysk/reposilite:3.6.3");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE");
    }

    @Test
    void shouldApplyNpmImage() {
        CodeArtifactConfig defaults = CodeArtifactConfig.builder().build();
        assertThat(defaults.getNpmImage()).isEqualTo("verdaccio/verdaccio:6.10.4");

        CodeArtifactConfig config = CodeArtifactConfig.builder().npmImage("verdaccio/verdaccio:6.11.0").build();
        assertThat(config.getNpmImage()).isEqualTo("verdaccio/verdaccio:6.11.0");
        assertThat(config.toBuilder().build().getNpmImage()).isEqualTo("verdaccio/verdaccio:6.11.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE", "verdaccio/verdaccio:6.11.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE", "verdaccio/verdaccio:6.10.4");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE");
    }

    @Test
    void shouldApplyPypiImage() {
        CodeArtifactConfig defaults = CodeArtifactConfig.builder().build();
        assertThat(defaults.getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.4.2");

        CodeArtifactConfig config = CodeArtifactConfig.builder().pypiImage("pypiserver/pypiserver:v2.5.0").build();
        assertThat(config.getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.5.0");
        assertThat(config.toBuilder().build().getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.5.0");

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE", "pypiserver/pypiserver:v2.5.0");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE", "pypiserver/pypiserver:v2.4.2");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(CodeArtifactConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(CodeArtifactConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
