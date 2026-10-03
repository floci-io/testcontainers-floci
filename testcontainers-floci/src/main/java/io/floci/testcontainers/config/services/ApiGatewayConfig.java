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

    private static final int DEFAULT_VTL_MAX_LOOPS = 10000;
    private static final int DEFAULT_VTL_MAX_OUTPUT_CHARS = 1048576;
    private static final long DEFAULT_VTL_TIMEOUT_MILLIS = 5000L;

    private final int vtlMaxLoops;
    private final int vtlMaxOutputChars;
    private final long vtlTimeoutMillis;

    private ApiGatewayConfig(Builder builder) {
        super(builder.enabled);
        this.vtlMaxLoops = builder.vtlMaxLoops;
        this.vtlMaxOutputChars = builder.vtlMaxOutputChars;
        this.vtlTimeoutMillis = builder.vtlTimeoutMillis;
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
     * Returns the maximum number of {@code #foreach} loop iterations allowed in a single VTL mapping template
     * render.
     *
     * <p>A hard backstop against runaway loops.
     *
     * @return the maximum number of {@code #foreach} loop iterations allowed in a single VTL mapping template render
     */
    public int getVtlMaxLoops() {
        return vtlMaxLoops;
    }

    /**
     * Returns the maximum rendered output size, in characters, of a single VTL mapping template render.
     *
     * @return the maximum rendered output size, in characters, of a single VTL mapping template render
     */
    public int getVtlMaxOutputChars() {
        return vtlMaxOutputChars;
    }

    /**
     * Returns the wall-clock execution budget, in milliseconds, of a single VTL mapping template render.
     *
     * @return the wall-clock execution budget, in milliseconds, of a single VTL mapping template render
     */
    public long getVtlTimeoutMillis() {
        return vtlTimeoutMillis;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APIGATEWAY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_LOOPS", String.valueOf(vtlMaxLoops));
            container.withEnv("FLOCI_SERVICES_APIGATEWAY_VTL_MAX_OUTPUT_CHARS", String.valueOf(vtlMaxOutputChars));
            container.withEnv("FLOCI_SERVICES_APIGATEWAY_VTL_TIMEOUT_MILLIS", String.valueOf(vtlTimeoutMillis));
        }
    }

    /**
     * Builder for {@link ApiGatewayConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApiGatewayConfig> {

        private int vtlMaxLoops = DEFAULT_VTL_MAX_LOOPS;
        private int vtlMaxOutputChars = DEFAULT_VTL_MAX_OUTPUT_CHARS;
        private long vtlTimeoutMillis = DEFAULT_VTL_TIMEOUT_MILLIS;

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
            this.vtlMaxLoops = instance.getVtlMaxLoops();
            this.vtlMaxOutputChars = instance.getVtlMaxOutputChars();
            this.vtlTimeoutMillis = instance.getVtlTimeoutMillis();
        }

        /**
         * Sets the maximum number of {@code #foreach} loop iterations allowed in a single VTL mapping
         * template render.
         *
         * <p>A hard backstop against runaway loops.
         *
         * @param vtlMaxLoops the maximum number of {@code #foreach} loop iterations allowed in a single VTL mapping template render (default {@value DEFAULT_VTL_MAX_LOOPS})
         * @return this builder
         */
        public Builder vtlMaxLoops(int vtlMaxLoops) {
            this.vtlMaxLoops = vtlMaxLoops;
            return this;
        }

        /**
         * Sets the maximum rendered output size, in characters, of a single VTL mapping template render.
         *
         * @param vtlMaxOutputChars the maximum rendered output size, in characters, of a single VTL mapping template render (default {@value DEFAULT_VTL_MAX_OUTPUT_CHARS})
         * @return this builder
         */
        public Builder vtlMaxOutputChars(int vtlMaxOutputChars) {
            this.vtlMaxOutputChars = vtlMaxOutputChars;
            return this;
        }

        /**
         * Sets the wall-clock execution budget, in milliseconds, of a single VTL mapping template render.
         *
         * @param vtlTimeoutMillis the wall-clock execution budget, in milliseconds, of a single VTL mapping template render (default {@value DEFAULT_VTL_TIMEOUT_MILLIS})
         * @return this builder
         */
        public Builder vtlTimeoutMillis(long vtlTimeoutMillis) {
            this.vtlTimeoutMillis = vtlTimeoutMillis;
            return this;
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
