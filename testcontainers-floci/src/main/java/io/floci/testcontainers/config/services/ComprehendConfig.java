package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Comprehend-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ComprehendConfig config = ComprehendConfig.builder()
 *     .build();
 * }</pre>
 */
public class ComprehendConfig extends AbstractServiceConfig<ComprehendConfig.Builder> {

    private ComprehendConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_COMPREHEND_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ComprehendConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ComprehendConfig> {

        private Builder() {
            // Allow instantiation only via ComprehendConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ComprehendConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ComprehendConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ComprehendConfig} from this builder.
         *
         * @return the Comprehend configuration
         */
        @Override
        public ComprehendConfig build() {
            return new ComprehendConfig(this);
        }
    }
}
