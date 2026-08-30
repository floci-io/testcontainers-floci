package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Connect-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ConnectConfig config = ConnectConfig.builder()
 *     .build();
 * }</pre>
 */
public class ConnectConfig extends AbstractServiceConfig<ConnectConfig.Builder> {

    private ConnectConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CONNECT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ConnectConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ConnectConfig> {

        private Builder() {
            // Allow instantiation only via ConnectConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ConnectConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ConnectConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ConnectConfig} from this builder.
         *
         * @return the Connect configuration
         */
        @Override
        public ConnectConfig build() {
            return new ConnectConfig(this);
        }
    }
}
