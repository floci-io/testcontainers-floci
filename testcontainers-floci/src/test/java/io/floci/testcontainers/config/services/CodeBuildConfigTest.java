package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CodeBuildConfigTest {

    @Test
    void shouldApplyDefaultCodeBuildConfig() {
        CodeBuildConfig config = CodeBuildConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomCodeBuildConfig() {
        CodeBuildConfig config = CodeBuildConfig.builder()
                .enabled(false)
                .dockerNetwork("my-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDockerNetwork()).isEqualTo("my-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeBuildConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEBUILD_ENABLED", "true")
                .doesNotContainKey("FLOCI_SERVICES_CODEBUILD_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeBuildConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CODEBUILD_ENABLED", "false");
    }

    @Test
    void shouldApplyDockerNetworkEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CodeBuildConfig.builder()
                .enabled(true)
                .dockerNetwork("my-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CODEBUILD_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CODEBUILD_DOCKER_NETWORK", "my-network");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CodeBuildConfig config = CodeBuildConfig.builder()
                .enabled(false)
                .dockerNetwork("test-network")
                .build();
        CodeBuildConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
    }

    @Test
    void shouldRequireDockerSocketWhenEnabled() {
        assertThat(CodeBuildConfig.builder().build().requiresDockerSocket()).isTrue();
        assertThat(CodeBuildConfig.builder().enabled(false).build().requiresDockerSocket()).isFalse();
    }

}
