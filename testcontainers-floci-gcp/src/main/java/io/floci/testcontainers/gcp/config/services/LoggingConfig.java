package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Logging of Floci GCP.
 *
 * <p>Cloud Logging is served via gRPC and REST on the main port.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * LoggingConfig config = LoggingConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class LoggingConfig extends AbstractServiceConfig<LoggingConfig.Builder> {

    private LoggingConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_LOGGING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link LoggingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, LoggingConfig> {

        private Builder() {
            // Allow instantiation only via LoggingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link LoggingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(LoggingConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link LoggingConfig} from this builder.
         *
         * @return the Cloud Logging configuration
         */
        @Override
        public LoggingConfig build() {
            return new LoggingConfig(this);
        }
    }
}
