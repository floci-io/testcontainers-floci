package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockRuntimeConfigTest {

    @Test
    void shouldApplyDefaultBedrockRuntimeConfig() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getBackend()).isEqualTo("stub");
        assertThat(config.getProxyUrl()).isNull();
        assertThat(config.getProxyApiKey()).isNull();
        assertThat(config.getProxyDefaultModel()).isNull();
        assertThat(config.getProxyModelMapping()).isNull();
        assertThat(config.isProxyPassthrough()).isFalse();
        assertThat(config.getProxyRequestTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyCustomBedrockRuntimeConfig() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyCustomProxyConfig() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
                .backend("proxy")
                .proxyUrl("http://localhost:11434/v1")
                .proxyApiKey("secret")
                .proxyDefaultModel("llama3")
                .proxyModelMapping("anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet")
                .proxyPassthrough(true)
                .proxyRequestTimeoutSeconds(120)
                .build();

        assertThat(config.getBackend()).isEqualTo("proxy");
        assertThat(config.getProxyUrl()).isEqualTo("http://localhost:11434/v1");
        assertThat(config.getProxyApiKey()).isEqualTo("secret");
        assertThat(config.getProxyDefaultModel()).isEqualTo("llama3");
        assertThat(config.getProxyModelMapping()).isEqualTo("anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet");
        assertThat(config.isProxyPassthrough()).isTrue();
        assertThat(config.getProxyRequestTimeoutSeconds()).isEqualTo(120);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockRuntimeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_BACKEND", "stub");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_PASSTHROUGH", "false");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_REQUEST_TIMEOUT_SECONDS", "60");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_URL");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_API_KEY");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_DEFAULT_MODEL");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_MODEL_MAPPING");
    }

    @Test
    void shouldApplyCustomProxyEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockRuntimeConfig.builder()
                .backend("proxy")
                .proxyUrl("http://localhost:11434/v1")
                .proxyApiKey("secret")
                .proxyDefaultModel("llama3")
                .proxyModelMapping("anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet")
                .proxyPassthrough(true)
                .proxyRequestTimeoutSeconds(120)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_BACKEND", "proxy");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_URL", "http://localhost:11434/v1");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_API_KEY", "secret");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_DEFAULT_MODEL", "llama3");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_MODEL_MAPPING",
                "anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_PASSTHROUGH", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_REQUEST_TIMEOUT_SECONDS", "120");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BedrockRuntimeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_BEDROCK_RUNTIME_BACKEND");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
                .enabled(false)
                .backend("proxy")
                .proxyUrl("http://localhost:11434/v1")
                .proxyApiKey("secret")
                .proxyDefaultModel("llama3")
                .proxyModelMapping("anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet")
                .proxyPassthrough(true)
                .proxyRequestTimeoutSeconds(120)
                .build();
        BedrockRuntimeConfig copy = config.toBuilder().build();

        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getBackend()).isEqualTo("proxy");
        assertThat(copy.getProxyUrl()).isEqualTo("http://localhost:11434/v1");
        assertThat(copy.getProxyApiKey()).isEqualTo("secret");
        assertThat(copy.getProxyDefaultModel()).isEqualTo("llama3");
        assertThat(copy.getProxyModelMapping()).isEqualTo("anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet");
        assertThat(copy.isProxyPassthrough()).isTrue();
        assertThat(copy.getProxyRequestTimeoutSeconds()).isEqualTo(120);
    }

}
