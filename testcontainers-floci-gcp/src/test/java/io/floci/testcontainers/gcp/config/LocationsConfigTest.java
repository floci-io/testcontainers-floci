package io.floci.testcontainers.gcp.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class LocationsConfigTest {

    @Test
    void shouldApplyDefaultLocationsConfig() {
        LocationsConfig config = LocationsConfig.builder().build();
        assertThat(config.isStrict()).isFalse();
    }

    @Test
    void shouldApplyCustomLocationsConfig() {
        LocationsConfig config = LocationsConfig.builder()
                .strict(true)
                .build();
        assertThat(config.isStrict()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LocationsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_LOCATIONS_STRICT", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        LocationsConfig.builder()
                .strict(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_LOCATIONS_STRICT", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        LocationsConfig config = LocationsConfig.builder()
                .strict(true)
                .build();

        LocationsConfig copy = config.toBuilder().build();

        assertThat(copy.isStrict()).isTrue();
    }
}
