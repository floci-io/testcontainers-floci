package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for SSM-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SsmConfig config = SsmConfig.builder()
 *     .maxParameterHistory(10)
 *     .build();
 * }</pre>
 */
public class SsmConfig extends AbstractServiceConfig<SsmConfig.Builder> {

    private static final int DEFAULT_MAX_PARAMETER_HISTORY = 5;

    private final int maxParameterHistory;

    private SsmConfig(Builder builder) {
        super(builder.enabled);
        this.maxParameterHistory = builder.maxParameterHistory;
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
     * Returns the maximum parameter history count.
     *
     * @return the maximum parameter history count
     */
    public int getMaxParameterHistory() {
        return maxParameterHistory;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SSM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SSM_MAX_PARAMETER_HISTORY", String.valueOf(maxParameterHistory));
        }
    }

    /**
     * Builder for {@link SsmConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SsmConfig> {

        private int maxParameterHistory = DEFAULT_MAX_PARAMETER_HISTORY;

        private Builder() {
            // Allow instantiation only via SsmConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SsmConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SsmConfig instance) {
            super(instance);
            this.maxParameterHistory = instance.getMaxParameterHistory();
        }

        /**
         * Sets the maximum number of parameter history entries to retain.
         *
         * @param maxParameterHistory the maximum number of parameter history entries to retain (default {@value DEFAULT_MAX_PARAMETER_HISTORY})
         * @return this builder
         */
        public Builder maxParameterHistory(int maxParameterHistory) {
            this.maxParameterHistory = maxParameterHistory;
            return this;
        }

        /**
         * Creates an immutable {@link SsmConfig} from this builder.
         *
         * @return the SSM configuration
         */
        public SsmConfig build() {
            return new SsmConfig(this);
        }
    }
}
