package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Tasks of Floci GCP.
 *
 * <p>Cloud Tasks is served via gRPC and REST on the main port.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudTasksConfig config = CloudTasksConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class CloudTasksConfig extends AbstractServiceConfig<CloudTasksConfig.Builder> {

    private CloudTasksConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_CLOUDTASKS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudTasksConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudTasksConfig> {

        private Builder() {
            // Allow instantiation only via CloudTasksConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudTasksConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudTasksConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudTasksConfig} from this builder.
         *
         * @return the Cloud Tasks configuration
         */
        @Override
        public CloudTasksConfig build() {
            return new CloudTasksConfig(this);
        }
    }
}
