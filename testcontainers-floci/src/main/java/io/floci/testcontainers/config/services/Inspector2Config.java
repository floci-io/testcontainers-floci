package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Inspector-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Inspector2Config config = Inspector2Config.builder()
 *     .build();
 * }</pre>
 */
public class Inspector2Config extends AbstractServiceConfig<Inspector2Config.Builder> {

    private Inspector2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_INSPECTOR2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link Inspector2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Inspector2Config> {

        private Builder() {
            // Allow instantiation only via Inspector2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Inspector2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Inspector2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link Inspector2Config} from this builder.
         *
         * @return the Inspector configuration
         */
        @Override
        public Inspector2Config build() {
            return new Inspector2Config(this);
        }
    }
}
