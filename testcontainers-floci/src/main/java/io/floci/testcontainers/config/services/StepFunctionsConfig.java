package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Step Functions-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StepFunctionsConfig config = StepFunctionsConfig.builder()
 *     .build();
 * }</pre>
 */
public class StepFunctionsConfig extends AbstractServiceConfig<StepFunctionsConfig.Builder> {

    private StepFunctionsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link StepFunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, StepFunctionsConfig> {


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
