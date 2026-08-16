package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Bedrock Runtime-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BedrockRuntimeConfig config = BedrockRuntimeConfig.builder()
 *     .backend("proxy")
 *     .proxyUrl("http://localhost:11434/v1")
 *     .proxyDefaultModel("llama3")
 *     .build();
 * }</pre>
 */
public class BedrockRuntimeConfig extends AbstractServiceConfig<BedrockRuntimeConfig.Builder> {

    private static final String DEFAULT_BACKEND = "stub";
    private static final boolean DEFAULT_PROXY_PASSTHROUGH = false;
    private static final int DEFAULT_PROXY_REQUEST_TIMEOUT_SECONDS = 60;

    private final String backend;
    private final String proxyUrl;
    private final String proxyApiKey;
    private final String proxyDefaultModel;
    private final String proxyModelMapping;
    private final boolean proxyPassthrough;
    private final int proxyRequestTimeoutSeconds;

    private BedrockRuntimeConfig(Builder builder) {
        super(builder.enabled);
        this.backend = builder.backend;
        this.proxyUrl = builder.proxyUrl;
        this.proxyApiKey = builder.proxyApiKey;
        this.proxyDefaultModel = builder.proxyDefaultModel;
        this.proxyModelMapping = builder.proxyModelMapping;
        this.proxyPassthrough = builder.proxyPassthrough;
        this.proxyRequestTimeoutSeconds = builder.proxyRequestTimeoutSeconds;
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the Converse/InvokeModel backend: {@code "stub"} (hardcoded response, no external
     * calls) or {@code "proxy"} (forwards Converse to an OpenAI-compatible {@code /chat/completions}
     * endpoint).
     *
     * @return the backend
     */
    public String getBackend() {
        return backend;
    }

    /**
     * Returns the base URL of the OpenAI-compatible backend (Ollama, OpenRouter, LiteLLM, vLLM),
     * or {@code null} if not set. Required when {@link #getBackend()} is {@code "proxy"}; requests
     * are POSTed to {@code "{url}/chat/completions"}.
     *
     * @return the proxy backend base URL, or {@code null}
     */
    public String getProxyUrl() {
        return proxyUrl;
    }

    /**
     * Returns the API key sent as {@code "Authorization: Bearer {apiKey}"} to the proxy backend,
     * or {@code null} if not set.
     *
     * @return the proxy backend API key, or {@code null}
     */
    public String getProxyApiKey() {
        return proxyApiKey;
    }

    /**
     * Returns the fallback OpenAI-side model id used when no explicit mapping matches and
     * {@link #isProxyPassthrough()} is disabled, or {@code null} if not set.
     *
     * @return the default proxy model, or {@code null}
     */
    public String getProxyDefaultModel() {
        return proxyDefaultModel;
    }

    /**
     * Returns the comma-separated {@code bedrockModelId=openaiModelId} pairs used to map Bedrock
     * model ids to OpenAI-side model ids, or {@code null} if not set.
     *
     * @return the proxy model mapping, or {@code null}
     */
    public String getProxyModelMapping() {
        return proxyModelMapping;
    }

    /**
     * Returns whether, when no explicit mapping matches, the raw Bedrock model id is forwarded
     * as-is instead of requiring a mapping or default model.
     *
     * @return {@code true} if proxy passthrough is enabled
     */
    public boolean isProxyPassthrough() {
        return proxyPassthrough;
    }

    /**
     * Returns how long to wait for the proxy backend to finish generating a response before
     * failing the request with {@code ModelTimeoutException}.
     *
     * @return the proxy request timeout, in seconds
     */
    public int getProxyRequestTimeoutSeconds() {
        return proxyRequestTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_BACKEND", backend);
            container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_PASSTHROUGH", String.valueOf(proxyPassthrough));
            container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_REQUEST_TIMEOUT_SECONDS",
                    String.valueOf(proxyRequestTimeoutSeconds));

            if (proxyUrl != null) {
                container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_URL", proxyUrl);
            }
            if (proxyApiKey != null) {
                container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_API_KEY", proxyApiKey);
            }
            if (proxyDefaultModel != null) {
                container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_DEFAULT_MODEL", proxyDefaultModel);
            }
            if (proxyModelMapping != null) {
                container.withEnv("FLOCI_SERVICES_BEDROCK_RUNTIME_PROXY_MODEL_MAPPING", proxyModelMapping);
            }
        }
    }

    /**
     * Builder for {@link BedrockRuntimeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BedrockRuntimeConfig> {

        private String backend = DEFAULT_BACKEND;
        private String proxyUrl;
        private String proxyApiKey;
        private String proxyDefaultModel;
        private String proxyModelMapping;
        private boolean proxyPassthrough = DEFAULT_PROXY_PASSTHROUGH;
        private int proxyRequestTimeoutSeconds = DEFAULT_PROXY_REQUEST_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via BedrockRuntimeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BedrockRuntimeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BedrockRuntimeConfig instance) {
            super(instance);
            this.backend = instance.getBackend();
            this.proxyUrl = instance.getProxyUrl();
            this.proxyApiKey = instance.getProxyApiKey();
            this.proxyDefaultModel = instance.getProxyDefaultModel();
            this.proxyModelMapping = instance.getProxyModelMapping();
            this.proxyPassthrough = instance.isProxyPassthrough();
            this.proxyRequestTimeoutSeconds = instance.getProxyRequestTimeoutSeconds();
        }

        /**
         * Sets the Converse/InvokeModel backend.
         *
         * @param backend {@code "stub"} (hardcoded response, no external calls) or {@code "proxy"}
         *                (forwards Converse to an OpenAI-compatible {@code /chat/completions}
         *                endpoint) (default {@value DEFAULT_BACKEND})
         * @return this builder
         */
        public Builder backend(String backend) {
            this.backend = backend;
            return this;
        }

        /**
         * Sets the base URL of the OpenAI-compatible backend (Ollama, OpenRouter, LiteLLM, vLLM),
         * e.g. {@code "http://localhost:11434/v1"}. Required when {@code backend} is {@code "proxy"};
         * requests are POSTed to {@code "{url}/chat/completions"}.
         *
         * @param proxyUrl the proxy backend base URL, or {@code null} (the default)
         * @return this builder
         */
        public Builder proxyUrl(String proxyUrl) {
            this.proxyUrl = proxyUrl;
            return this;
        }

        /**
         * Sets the API key sent as {@code "Authorization: Bearer {apiKey}"} to the proxy backend.
         *
         * @param proxyApiKey the proxy backend API key, or {@code null} (the default)
         * @return this builder
         */
        public Builder proxyApiKey(String proxyApiKey) {
            this.proxyApiKey = proxyApiKey;
            return this;
        }

        /**
         * Sets the fallback OpenAI-side model id used when no explicit mapping matches and
         * {@code proxyPassthrough} is disabled.
         *
         * @param proxyDefaultModel the default proxy model, or {@code null} (the default)
         * @return this builder
         */
        public Builder proxyDefaultModel(String proxyDefaultModel) {
            this.proxyDefaultModel = proxyDefaultModel;
            return this;
        }

        /**
         * Sets the comma-separated {@code bedrockModelId=openaiModelId} pairs used to map Bedrock
         * model ids to OpenAI-side model ids, e.g.
         * {@code "anthropic.claude-3-sonnet-20240229-v1:0=claude-3-sonnet"}. A delimited string
         * rather than a native map, since Bedrock model ids contain {@code '.'} and {@code ':'}.
         *
         * @param proxyModelMapping the proxy model mapping, or {@code null} (the default)
         * @return this builder
         */
        public Builder proxyModelMapping(String proxyModelMapping) {
            this.proxyModelMapping = proxyModelMapping;
            return this;
        }

        /**
         * Sets whether, when no explicit mapping matches, the raw Bedrock model id is forwarded
         * as-is instead of requiring a mapping or default model.
         *
         * @param proxyPassthrough {@code true} to enable passthrough (default {@value DEFAULT_PROXY_PASSTHROUGH})
         * @return this builder
         */
        public Builder proxyPassthrough(boolean proxyPassthrough) {
            this.proxyPassthrough = proxyPassthrough;
            return this;
        }

        /**
         * Sets how long to wait for the proxy backend to finish generating a response before
         * failing the request with {@code ModelTimeoutException}. Larger models on CPU-backed
         * backends (e.g. Ollama) may need more than the default.
         *
         * @param proxyRequestTimeoutSeconds the proxy request timeout, in seconds
         *                                   (default {@value DEFAULT_PROXY_REQUEST_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder proxyRequestTimeoutSeconds(int proxyRequestTimeoutSeconds) {
            this.proxyRequestTimeoutSeconds = proxyRequestTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link BedrockRuntimeConfig} from this builder.
         *
         * @return the Bedrock Runtime configuration
         */
        @Override
        public BedrockRuntimeConfig build() {
            return new BedrockRuntimeConfig(this);
        }
    }
}
