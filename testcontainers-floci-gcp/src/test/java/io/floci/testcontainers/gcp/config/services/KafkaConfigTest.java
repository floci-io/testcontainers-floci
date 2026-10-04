package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void shouldApplyDefaultKafkaConfig() {
        KafkaConfig config = KafkaConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("redpandadata/redpanda:latest");
        assertThat(config.getConnectImage()).isEqualTo("apache/kafka:4.3.1");
        assertThat(config.getDockerNetwork()).isEmpty();
    }

    @Test
    void shouldApplyCustomKafkaConfig() {
        KafkaConfig config = KafkaConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("redpandadata/redpanda:v25.1.1")
                .connectImage("apache/kafka:4.0.0")
                .dockerNetwork("kafka-net")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("redpandadata/redpanda:v25.1.1");
        assertThat(config.getConnectImage()).isEqualTo("apache/kafka:4.0.0");
        assertThat(config.getDockerNetwork()).contains("kafka-net");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KafkaConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_DEFAULT_IMAGE", "redpandadata/redpanda:latest")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_CONNECT_IMAGE", "apache/kafka:4.3.1")
                .doesNotContainKey("FLOCI_GCP_SERVICES_KAFKA_DOCKER_NETWORK");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KafkaConfig.builder()
                .mock(true)
                .defaultImage("redpandadata/redpanda:v25.1.1")
                .connectImage("apache/kafka:4.0.0")
                .dockerNetwork("kafka-net")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_DEFAULT_IMAGE", "redpandadata/redpanda:v25.1.1")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_CONNECT_IMAGE", "apache/kafka:4.0.0")
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_DOCKER_NETWORK", "kafka-net");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        KafkaConfig.builder().enabled(false).dockerNetwork("kafka-net").build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_KAFKA_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_KAFKA_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_KAFKA_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_KAFKA_CONNECT_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_KAFKA_DOCKER_NETWORK");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        KafkaConfig config = KafkaConfig.builder()
                .enabled(false)
                .mock(true)
                .defaultImage("redpandadata/redpanda:v25.1.1")
                .connectImage("apache/kafka:4.0.0")
                .dockerNetwork("kafka-net")
                .build();
        KafkaConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMock()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("redpandadata/redpanda:v25.1.1");
        assertThat(copy.getConnectImage()).isEqualTo("apache/kafka:4.0.0");
        assertThat(copy.getDockerNetwork()).contains("kafka-net");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(KafkaConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(KafkaConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(KafkaConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }
}
