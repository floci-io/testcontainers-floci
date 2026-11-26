package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Billing of Floci GCP.
 *
 * <p>The Cloud Billing API ({@code cloudbilling.googleapis.com}, v1) is served as a small REST stub: projects
 * can be linked to billing accounts, and any well-formed billing account ID is reported as an open account.
 * There are no charges, budgets or stored billing accounts.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudBillingConfig config = CloudBillingConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class CloudBillingConfig extends AbstractServiceConfig<CloudBillingConfig.Builder> {

    private CloudBillingConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_CLOUDBILLING_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CloudBillingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudBillingConfig> {

        private Builder() {
            // Allow instantiation only via CloudBillingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudBillingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudBillingConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CloudBillingConfig} from this builder.
         *
         * @return the Cloud Billing configuration
         */
        @Override
        public CloudBillingConfig build() {
            return new CloudBillingConfig(this);
        }
    }
}
