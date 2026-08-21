package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Athena-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AthenaConfig config = AthenaConfig.builder()
 *     .mock(false)
 *     .build();
 * }</pre>
 */
public class AthenaConfig extends AbstractServiceConfig<AthenaConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;

    private final boolean mock;

    private AthenaConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
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

    /**
     * Returns whether Athena operates in mock mode (no real DuckDB backend).
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ATHENA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ATHENA_MOCK", String.valueOf(mock));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link AthenaConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AthenaConfig> {

        private boolean mock = DEFAULT_MOCK;

        private Builder() {
            // Allow instantiation only via AthenaConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AthenaConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AthenaConfig instance) {
            super(instance);
            this.mock = instance.isMock();
        }

        /**
         * Sets whether Athena operates in mock mode (no real DuckDB backend).
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Creates an immutable {@link AthenaConfig} from this builder.
         *
         * @return the Athena configuration
         */
        @Override
        public AthenaConfig build() {
            return new AthenaConfig(this);
        }
    }
}
