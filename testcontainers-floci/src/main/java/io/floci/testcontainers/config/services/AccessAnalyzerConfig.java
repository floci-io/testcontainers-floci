package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for IAM Access Analyzer-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AccessAnalyzerConfig config = AccessAnalyzerConfig.builder()
 *     .build();
 * }</pre>
 */
public class AccessAnalyzerConfig extends AbstractServiceConfig<AccessAnalyzerConfig.Builder> {

    private AccessAnalyzerConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_ACCESSANALYZER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AccessAnalyzerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AccessAnalyzerConfig> {

        private Builder() {
            // Allow instantiation only via AccessAnalyzerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AccessAnalyzerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AccessAnalyzerConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AccessAnalyzerConfig} from this builder.
         *
         * @return the IAM Access Analyzer configuration
         */
        @Override
        public AccessAnalyzerConfig build() {
            return new AccessAnalyzerConfig(this);
        }
    }
}
