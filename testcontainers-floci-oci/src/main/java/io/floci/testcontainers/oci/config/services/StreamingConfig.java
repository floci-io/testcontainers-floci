package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Streaming of Floci OCI.
 *
 * <p>The control plane and the data plane are both served on the Floci OCI endpoint. The {@code messagesEndpoint} returned for a stream is Floci OCI's own base URL ({@code http://localhost:4599}), so point data plane clients at {@code FlociOciContainer#getEndpoint()} instead.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StreamingConfig config = StreamingConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class StreamingConfig extends AbstractServiceConfig<StreamingConfig.Builder> {

    private StreamingConfig(Builder builder) {
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
        container.withEnv("FLOCI_OCI_SERVICES_STREAMING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link StreamingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, StreamingConfig> {

        private Builder() {
            // Allow instantiation only via StreamingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link StreamingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(StreamingConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link StreamingConfig} from this builder.
         *
         * @return the OCI Streaming configuration
         */
        @Override
        public StreamingConfig build() {
            return new StreamingConfig(this);
        }
    }
}
