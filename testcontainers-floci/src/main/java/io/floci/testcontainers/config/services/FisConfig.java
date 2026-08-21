package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for FIS (Fault Injection Simulator)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FisConfig config = FisConfig.builder()
 *     .build();
 * }</pre>
 */
public class FisConfig extends AbstractServiceConfig<FisConfig.Builder> {

    private FisConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_FIS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link FisConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FisConfig> {

        private Builder() {
            // Allow instantiation only via FisConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FisConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FisConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link FisConfig} from this builder.
         *
         * @return the FIS configuration
         */
        @Override
        public FisConfig build() {
            return new FisConfig(this);
        }
    }
}
