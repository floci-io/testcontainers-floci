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
        assertThat(config.getMavenUrl()).isEmpty();
        assertThat(config.getMavenToken()).isEmpty();
        assertThat(config.getMavenImage()).isEqualTo("dzikoysk/reposilite:3.6.3");
        assertThat(config.getNpmImage()).isEqualTo("verdaccio/verdaccio:6.10.4");
        assertThat(config.getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.4.2");
    }

    @Test
    void shouldApplyCustomCodeArtifactConfig() {
        CodeArtifactConfig config = CodeArtifactConfig.builder()
                .enabled(false)
                .mavenUrl("http://reposilite:8080")
                .mavenToken("admin:secret")
                .mavenImage("dzikoysk/reposilite:3.7.0")
                .npmImage("verdaccio/verdaccio:6.11.0")
                .pypiImage("pypiserver/pypiserver:v2.5.0")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getMavenUrl()).contains("http://reposilite:8080");
        assertThat(config.getMavenToken()).contains("admin:secret");
        assertThat(config.getMavenImage()).isEqualTo("dzikoysk/reposilite:3.7.0");
        assertThat(config.getNpmImage()).isEqualTo("verdaccio/verdaccio:6.11.0");
        assertThat(config.getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.5.0");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeArtifactConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE", "dzikoysk/reposilite:3.6.3")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE", "verdaccio/verdaccio:6.10.4")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE", "pypiserver/pypiserver:v2.4.2");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeArtifactConfig.builder()
                .mavenUrl("http://reposilite:8080")
                .mavenToken("admin:secret")
                .mavenImage("dzikoysk/reposilite:3.7.0")
                .npmImage("verdaccio/verdaccio:6.11.0")
                .pypiImage("pypiserver/pypiserver:v2.5.0")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL", "http://reposilite:8080")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN", "admin:secret")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE", "dzikoysk/reposilite:3.7.0")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE", "verdaccio/verdaccio:6.11.0")
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE", "pypiserver/pypiserver:v2.5.0");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeArtifactConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEARTIFACT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE")
                .doesNotContainKey("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodeArtifactConfig config = CodeArtifactConfig.builder()
                .enabled(false)
                .mavenUrl("http://reposilite:8080")
                .mavenToken("admin:secret")
                .mavenImage("dzikoysk/reposilite:3.7.0")
                .npmImage("verdaccio/verdaccio:6.11.0")
                .pypiImage("pypiserver/pypiserver:v2.5.0")
                .build();
        CodeArtifactConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getMavenUrl()).contains("http://reposilite:8080");
        assertThat(copy.getMavenToken()).contains("admin:secret");
        assertThat(copy.getMavenImage()).isEqualTo("dzikoysk/reposilite:3.7.0");
        assertThat(copy.getNpmImage()).isEqualTo("verdaccio/verdaccio:6.11.0");
        assertThat(copy.getPypiImage()).isEqualTo("pypiserver/pypiserver:v2.5.0");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabled() {
        assertThat(CodeArtifactConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(CodeArtifactConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
