package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Transcribe-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TranscribeConfig config = TranscribeConfig.builder()
 *     .build();
 * }</pre>
 */
public class TranscribeConfig extends AbstractServiceConfig<TranscribeConfig.Builder> {

    private TranscribeConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_TRANSCRIBE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link TranscribeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TranscribeConfig> {

        private Builder() {
            // Allow instantiation only via TranscribeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TranscribeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TranscribeConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link TranscribeConfig} from this builder.
         *
         * @return the Transcribe configuration
         */
        public TranscribeConfig build() {
            return new TranscribeConfig(this);
        }
    }
}
