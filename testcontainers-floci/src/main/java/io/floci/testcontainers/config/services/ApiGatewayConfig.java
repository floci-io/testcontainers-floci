package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for API Gateway-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ApiGatewayConfig config = ApiGatewayConfig.builder()
 *     .build();
 * }</pre>
 */
public class ApiGatewayConfig extends AbstractServiceConfig<ApiGatewayConfig.Builder> {


    private ApiGatewayConfig(Builder builder) {
        super(builder.enabled);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APIGATEWAY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ApiGatewayConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApiGatewayConfig> {


        private Builder() {
            // Allow instantiation only via ApiGatewayConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ApiGatewayConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ApiGatewayConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ApiGatewayConfig} from this builder.
         *
         * @return the API Gateway configuration
         */
        @Override
        public ApiGatewayConfig build() {
            return new ApiGatewayConfig(this);
        }
    }
}
