package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Macie-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Macie2Config config = Macie2Config.builder()
 *     .build();
 * }</pre>
 */
public class Macie2Config extends AbstractServiceConfig<Macie2Config.Builder> {

    private Macie2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_MACIE2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link Macie2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Macie2Config> {

        private Builder() {
            // Allow instantiation only via Macie2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Macie2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Macie2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link Macie2Config} from this builder.
         *
         * @return the Macie configuration
         */
        @Override
        public Macie2Config build() {
            return new Macie2Config(this);
        }
    }
}
