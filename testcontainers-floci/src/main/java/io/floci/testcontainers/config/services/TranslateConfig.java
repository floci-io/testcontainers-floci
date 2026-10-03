package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Translate-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TranslateConfig config = TranslateConfig.builder()
 *     .build();
 * }</pre>
 */
public class TranslateConfig extends AbstractServiceConfig<TranslateConfig.Builder> {

    private TranslateConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_TRANSLATE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link TranslateConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TranslateConfig> {

        private Builder() {
            // Allow instantiation only via TranslateConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TranslateConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TranslateConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link TranslateConfig} from this builder.
         *
         * @return the Translate configuration
         */
        @Override
        public TranslateConfig build() {
            return new TranslateConfig(this);
        }
    }
}
