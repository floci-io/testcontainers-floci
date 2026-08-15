package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Step Functions-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StepFunctionsConfig config = StepFunctionsConfig.builder()
 *     .allowPlaintextHttp(false)
 *     .build();
 * }</pre>
 */
public class StepFunctionsConfig extends AbstractServiceConfig<StepFunctionsConfig.Builder> {

    private static final boolean DEFAULT_ALLOW_PLAINTEXT_HTTP = true;

    private final boolean allowPlaintextHttp;

    private StepFunctionsConfig(Builder builder) {
        super(builder.enabled);
        this.allowPlaintextHttp = builder.allowPlaintextHttp;
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

    /**
     * Returns whether plain HTTP endpoints may be invoked by Step Functions state machines. By
     * default, AWS only allows HTTPS.
     *
     * @return {@code true} if plain HTTP endpoints are allowed
     */
    public boolean isAllowPlaintextHttp() {
        return allowPlaintextHttp;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", String.valueOf(allowPlaintextHttp));
        }
    }

    /**
     * Builder for {@link StepFunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, StepFunctionsConfig> {

        private boolean allowPlaintextHttp = DEFAULT_ALLOW_PLAINTEXT_HTTP;

        private Builder() {
            // Allow instantiation only via StepFunctionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link StepFunctionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(StepFunctionsConfig instance) {
            super(instance);
            this.allowPlaintextHttp = instance.isAllowPlaintextHttp();
        }

        /**
         * Sets whether plain HTTP endpoints may be invoked by Step Functions state machines. By
         * default, AWS only allows HTTPS.
         *
         * @param allowPlaintextHttp {@code true} to allow plain HTTP endpoints (default {@value DEFAULT_ALLOW_PLAINTEXT_HTTP})
         * @return this builder
         */
        public Builder allowPlaintextHttp(boolean allowPlaintextHttp) {
            this.allowPlaintextHttp = allowPlaintextHttp;
            return this;
        }

        /**
         * Creates an immutable {@link StepFunctionsConfig} from this builder.
         *
         * @return the Step Functions configuration
         */
        public StepFunctionsConfig build() {
            return new StepFunctionsConfig(this);
        }
    }
}
