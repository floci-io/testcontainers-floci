package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentDbConfigTest {

    @Test
    void shouldApplyDefaultDocumentDbConfig() {
        DocumentDbConfig config = DocumentDbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("mongo:7.0");
        assertThat(config.getDockerNetwork()).isNull();
    }

    @Test
    void shouldApplyCustomDocumentDbConfig() {
        DocumentDbConfig config = DocumentDbConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("mongo:8.0")
                .dockerNetwork("my-docdb-network")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("mongo:8.0");
        assertThat(config.getDockerNetwork()).isEqualTo("my-docdb-network");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DocumentDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DOCDB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_DOCDB_MOCK", "false")
                .containsEntry("FLOCI_SERVICES_DOCDB_DEFAULT_IMAGE", "mongo:7.0")
                .doesNotContainKey("FLOCI_SERVICES_DOCDB_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DocumentDbConfig.builder()
                .mock(true)
                .defaultImage("mongo:8.0")
                .dockerNetwork("my-docdb-network")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DOCDB_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_DOCDB_MOCK", "true")
                .containsEntry("FLOCI_SERVICES_DOCDB_DEFAULT_IMAGE", "mongo:8.0")
                .containsEntry("FLOCI_SERVICES_DOCDB_DOCKER_NETWORK", "my-docdb-network");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        DocumentDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DOCDB_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_DOCDB_MOCK")
                .doesNotContainKey("FLOCI_SERVICES_DOCDB_DEFAULT_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DocumentDbConfig config = DocumentDbConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("test-image")
                .dockerNetwork("test-network")
                .build();
        DocumentDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("test-image");
        assertThat(copy.getDockerNetwork()).isEqualTo("test-network");
    }

}
