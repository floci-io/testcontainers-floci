package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for API Gateway V2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ApiGatewayV2Config config = ApiGatewayV2Config.builder()
 *     .build();
 * }</pre>
 */
public class ApiGatewayV2Config extends AbstractServiceConfig<ApiGatewayV2Config.Builder> {


    private ApiGatewayV2Config(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APIGATEWAYV2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ApiGatewayV2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApiGatewayV2Config> {


        private Builder() {
            // Allow instantiation only via ApiGatewayV2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ApiGatewayV2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ApiGatewayV2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ApiGatewayV2Config} from this builder.
         *
         * @return the API Gateway V2 configuration
         */
        public ApiGatewayV2Config build() {
            return new ApiGatewayV2Config(this);
        }
    }
}
