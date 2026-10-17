package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class DuckDbConfigTest {

    @Test
    void shouldApplyDefaultDuckDbConfig() {
        DuckDbConfig config = DuckDbConfig.builder().build();
        assertThat(config.getUrl()).isNull();
        assertThat(config.getDefaultImage()).isEqualTo("floci/floci-duck:latest");
    }

    @Test
    void shouldApplyCustomDuckDbConfig() {
        DuckDbConfig config = DuckDbConfig.builder()
                .url("http://custom-duckdb:8080")
                .defaultImage("floci/floci-duck:1.5.18")
                .build();
        assertThat(config.getUrl()).isEqualTo("http://custom-duckdb:8080");
        assertThat(config.getDefaultImage()).isEqualTo("floci/floci-duck:1.5.18");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DuckDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DUCK_DEFAULT_IMAGE", "floci/floci-duck:latest")
                .doesNotContainKey("FLOCI_SERVICES_DUCK_URL");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        DuckDbConfig.builder()
                .url("http://custom-duckdb:8080")
                .defaultImage("floci/floci-duck:1.5.18")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_DUCK_DEFAULT_IMAGE", "floci/floci-duck:1.5.18")
                .containsEntry("FLOCI_SERVICES_DUCK_URL", "http://custom-duckdb:8080");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        DuckDbConfig config = DuckDbConfig.builder()
                .url("http://custom-duckdb:8080")
                .defaultImage("floci/floci-duck:1.5.18")
                .build();

        DuckDbConfig copy = config.toBuilder().build();

        assertThat(copy.getUrl()).isEqualTo("http://custom-duckdb:8080");
        assertThat(copy.getDefaultImage()).isEqualTo("floci/floci-duck:1.5.18");
    }
}
