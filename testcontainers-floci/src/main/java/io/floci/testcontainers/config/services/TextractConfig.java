package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Textract-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TextractConfig config = TextractConfig.builder()
 *     .build();
 * }</pre>
 */
public class TextractConfig extends AbstractServiceConfig<TextractConfig.Builder> {

    private TextractConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_TEXTRACT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link TextractConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TextractConfig> {


        private Builder() {
            // Allow instantiation only via TextractConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TextractConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TextractConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link TextractConfig} from this builder.
         *
         * @return the Textract configuration
         */
        @Override
        public TextractConfig build() {
            return new TextractConfig(this);
        }
    }
}
