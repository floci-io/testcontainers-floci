package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Functions of Floci GCP.
 *
 * <p>Cloud Functions (v2) manages functions as control plane only: functions are stored and reported as
 * active, but their code is not executed, so no Docker access is needed.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudFunctionsConfig config = CloudFunctionsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class CloudFunctionsConfig extends AbstractServiceConfig<CloudFunctionsConfig.Builder> {

    private CloudFunctionsConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_CLOUDFUNCTIONS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudFunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudFunctionsConfig> {

        private Builder() {
            // Allow instantiation only via CloudFunctionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudFunctionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudFunctionsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudFunctionsConfig} from this builder.
         *
         * @return the Cloud Functions configuration
         */
        @Override
        public CloudFunctionsConfig build() {
            return new CloudFunctionsConfig(this);
        }
    }
}
