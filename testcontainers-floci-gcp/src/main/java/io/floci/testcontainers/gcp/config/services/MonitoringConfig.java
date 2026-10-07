package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Monitoring of Floci GCP.
 *
 * <p>Cloud Monitoring is served via gRPC and REST on the main port.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MonitoringConfig config = MonitoringConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class MonitoringConfig extends AbstractServiceConfig<MonitoringConfig.Builder> {

    private MonitoringConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_MONITORING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link MonitoringConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MonitoringConfig> {

        private Builder() {
            // Allow instantiation only via MonitoringConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MonitoringConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MonitoringConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link MonitoringConfig} from this builder.
         *
         * @return the Cloud Monitoring configuration
         */
        @Override
        public MonitoringConfig build() {
            return new MonitoringConfig(this);
        }
    }
}
