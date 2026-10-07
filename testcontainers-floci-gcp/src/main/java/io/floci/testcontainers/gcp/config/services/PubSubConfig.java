package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Pub/Sub of Floci GCP.
 *
 * <p>Pub/Sub is served via gRPC on the main port. Clients connect via
 * {@code FlociGcpContainer#getEmulatorHost()}, e.g. as {@code PUBSUB_EMULATOR_HOST} or as target of a
 * plaintext gRPC channel.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PubSubConfig config = PubSubConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class PubSubConfig extends AbstractServiceConfig<PubSubConfig.Builder> {

    private PubSubConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_GCP_SERVICES_PUBSUB_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link PubSubConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PubSubConfig> {

        private Builder() {
            // Allow instantiation only via PubSubConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PubSubConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PubSubConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link PubSubConfig} from this builder.
         *
         * @return the Pub/Sub configuration
         */
        @Override
        public PubSubConfig build() {
            return new PubSubConfig(this);
        }
    }
}
