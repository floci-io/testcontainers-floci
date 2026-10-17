package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RedisConfigTest {

    @Test
    void shouldApplyDefaultRedisConfig() {
        RedisConfig config = RedisConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("valkey/valkey:8-alpine");
        assertThat(config.getBasePort()).isEqualTo(6379);
        assertThat(config.getPortsCount()).isEqualTo(10);
        assertThat(config.getMaxPort()).isEqualTo(6388);
        assertThat(config.getMaxMemory()).isEqualTo("256mb");
        assertThat(config.isTlsEnabled()).isFalse();
    }

    @Test
    void shouldApplyCustomRedisConfig() {
        RedisConfig config = RedisConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("redis:7-alpine")
                .portRange(16379, 5)
                .maxMemory("1gb")
                .tlsEnabled(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("redis:7-alpine");
        assertThat(config.getBasePort()).isEqualTo(16379);
        assertThat(config.getPortsCount()).isEqualTo(5);
        assertThat(config.getMaxPort()).isEqualTo(16383);
        assertThat(config.getMaxMemory()).isEqualTo("1gb");
        assertThat(config.isTlsEnabled()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedisConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_DEFAULT_IMAGE", "valkey/valkey:8-alpine")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_BASE_PORT", "6379")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MAX_PORT", "6388")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MAX_MEMORY", "256mb")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_TLS_ENABLED", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RedisConfig.builder()
                .mocked(true)
                .defaultImage("redis:7-alpine")
                .portRange(16379, 5)
                .maxMemory("1gb")
                .tlsEnabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_DEFAULT_IMAGE", "redis:7-alpine")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_BASE_PORT", "16379")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MAX_PORT", "16383")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_MAX_MEMORY", "1gb")
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_TLS_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RedisConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_REDIS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_BASE_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_MAX_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_MAX_MEMORY")
                .doesNotContainKey("FLOCI_AZ_SERVICES_REDIS_TLS_ENABLED");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RedisConfig config = RedisConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("redis:7-alpine")
                .portRange(16379, 5)
                .maxMemory("1gb")
                .tlsEnabled(true)
                .build();
        RedisConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("redis:7-alpine");
        assertThat(copy.getBasePort()).isEqualTo(16379);
        assertThat(copy.getPortsCount()).isEqualTo(5);
        assertThat(copy.getMaxPort()).isEqualTo(16383);
        assertThat(copy.getMaxMemory()).isEqualTo("1gb");
        assertThat(copy.isTlsEnabled()).isTrue();
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(RedisConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(RedisConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(RedisConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
