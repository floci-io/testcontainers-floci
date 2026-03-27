package io.floci.testcontainers;

import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlociContainerTest {

    @Test
    void shouldCreateContainerWithDefaultImage() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getDockerImageName()).isEqualTo("hectorvent/floci:latest");
        }
    }

    @Test
    void shouldRejectIncompatibleImage() {
        assertThatThrownBy(() -> new FlociContainer(DockerImageName.parse("other/image:latest")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldReturnDefaultRegion() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getRegion()).isEqualTo("us-east-1");
        }
    }

    @Test
    void shouldReturnCustomRegion() {
        try (FlociContainer container = new FlociContainer()) {
            container.withRegion("eu-west-1");
            assertThat(container.getRegion()).isEqualTo("eu-west-1");
        }
    }

    @Test
    void shouldReturnDefaultCredentials() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getAccessKey()).isEqualTo("test");
            assertThat(container.getSecretKey()).isEqualTo("test");
        }
    }

    @Test
    void shouldExposeFlociPort() {
        try (FlociContainer container = new FlociContainer()) {
            assertThat(container.getExposedPorts()).containsOnly(FlociContainer.PORT);
        }
    }

}
