package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PartitionsConfigTest {

    @Test
    void shouldApplyDefaultPartitionsConfig() {
        PartitionsConfig config = PartitionsConfig.builder().build();
        assertThat(config.getId()).isEmpty();
        assertThat(config.isStrict()).isFalse();
        assertThat(config.isAllowUnknownRegions()).isFalse();
    }

    @Test
    void shouldApplyCustomPartitionsConfig() {
        PartitionsConfig config = PartitionsConfig.builder()
                .id("aws-cn")
                .strict(true)
                .allowUnknownRegions(true)
                .build();
        assertThat(config.getId()).contains("aws-cn");
        assertThat(config.isStrict()).isTrue();
        assertThat(config.isAllowUnknownRegions()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PartitionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PARTITIONS_STRICT", "false")
                .containsEntry("FLOCI_PARTITIONS_ALLOW_UNKNOWN_REGIONS", "false")
                .doesNotContainKey("FLOCI_PARTITIONS_ID");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PartitionsConfig.builder()
                .id("aws-cn")
                .strict(true)
                .allowUnknownRegions(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_PARTITIONS_ID", "aws-cn")
                .containsEntry("FLOCI_PARTITIONS_STRICT", "true")
                .containsEntry("FLOCI_PARTITIONS_ALLOW_UNKNOWN_REGIONS", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PartitionsConfig config = PartitionsConfig.builder()
                .id("aws-cn")
                .strict(true)
                .allowUnknownRegions(true)
                .build();

        PartitionsConfig copy = config.toBuilder().build();

        assertThat(copy.getId()).contains("aws-cn");
        assertThat(copy.isStrict()).isTrue();
        assertThat(copy.isAllowUnknownRegions()).isTrue();
    }
}
