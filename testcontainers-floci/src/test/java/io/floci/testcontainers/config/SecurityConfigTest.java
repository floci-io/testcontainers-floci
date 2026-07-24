package io.floci.testcontainers.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void shouldApplyDefaultSecurityConfig() {
        SecurityConfig config = SecurityConfig.builder().build();
        assertThat(config.getExtraCorsAllowedOrigins()).isEmpty();
        assertThat(config.getExtraCorsAllowedHeaders()).isEmpty();
        assertThat(config.getExtraCorsExposeHeaders()).isEmpty();
        assertThat(config.isDisableCorsHeaders()).isFalse();
        assertThat(config.isCorsAllowPrivateNetwork()).isFalse();
    }

    @Test
    void shouldApplyCustomSecurityConfig() {
        SecurityConfig config = SecurityConfig.builder()
                .extraCorsAllowedOrigins(List.of("https://example.com", "https://other.com"))
                .extraCorsAllowedHeaders(List.of("X-Custom-Header"))
                .extraCorsExposeHeaders(List.of("X-Expose-Header"))
                .disableCorsHeaders(true)
                .corsAllowPrivateNetwork(true)
                .build();
        assertThat(config.getExtraCorsAllowedOrigins()).contains(List.of("https://example.com", "https://other.com"));
        assertThat(config.getExtraCorsAllowedHeaders()).contains(List.of("X-Custom-Header"));
        assertThat(config.getExtraCorsExposeHeaders()).contains(List.of("X-Expose-Header"));
        assertThat(config.isDisableCorsHeaders()).isTrue();
        assertThat(config.isCorsAllowPrivateNetwork()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecurityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SECURITY_DISABLE_CORS_HEADERS", "false")
                .containsEntry("FLOCI_SECURITY_CORS_ALLOW_PRIVATE_NETWORK", "false")
                .doesNotContainKey("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_ORIGINS")
                .doesNotContainKey("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_HEADERS")
                .doesNotContainKey("FLOCI_SECURITY_EXTRA_CORS_EXPOSE_HEADERS");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SecurityConfig.builder()
                .extraCorsAllowedOrigins(List.of("https://example.com", "https://other.com"))
                .extraCorsAllowedHeaders(List.of("X-Custom-Header"))
                .extraCorsExposeHeaders(List.of("X-Expose-Header"))
                .disableCorsHeaders(true)
                .corsAllowPrivateNetwork(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SECURITY_DISABLE_CORS_HEADERS", "true")
                .containsEntry("FLOCI_SECURITY_CORS_ALLOW_PRIVATE_NETWORK", "true")
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_ORIGINS", "https://example.com,https://other.com")
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_HEADERS", "X-Custom-Header")
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_EXPOSE_HEADERS", "X-Expose-Header");
    }

    @Test
    void shouldJoinMultipleValuesWithComma() {
        GenericContainer<?> container = genericContainer();
        SecurityConfig.builder()
                .extraCorsAllowedOrigins(List.of("https://a.com", "https://b.com", "https://c.com"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_ORIGINS", "https://a.com,https://b.com,https://c.com");
    }

    @Test
    void shouldAcceptVarargsForCorsLists() {
        GenericContainer<?> container = genericContainer();
        SecurityConfig.builder()
                .extraCorsAllowedOrigins("https://a.com", "https://b.com")
                .extraCorsAllowedHeaders("X-Header-One", "X-Header-Two")
                .extraCorsExposeHeaders("X-Expose-One")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_ORIGINS", "https://a.com,https://b.com")
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_ALLOWED_HEADERS", "X-Header-One,X-Header-Two")
                .containsEntry("FLOCI_SECURITY_EXTRA_CORS_EXPOSE_HEADERS", "X-Expose-One");
    }
}
