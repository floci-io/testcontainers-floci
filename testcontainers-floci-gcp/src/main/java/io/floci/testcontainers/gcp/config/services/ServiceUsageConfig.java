package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.List;

/**
 * Configuration for Service Usage of Floci GCP.
 *
 * <p>The Service Usage API ({@code serviceusage.googleapis.com}) enables and disables APIs of a project. With
 * {@link Builder#enforcementEnabled(boolean) enforcement} enabled, Floci GCP rejects calls to APIs that are not
 * enabled for the request's project, like GCP does.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ServiceUsageConfig config = ServiceUsageConfig.builder()
 *     .enforcementEnabled(true)
 *     .defaultEnabled(List.of("storage.googleapis.com", "pubsub.googleapis.com"))
 *     .build();
 * }</pre>
 */
public class ServiceUsageConfig extends AbstractServiceConfig<ServiceUsageConfig.Builder> {

    private static final boolean DEFAULT_ENFORCEMENT_ENABLED = false;
    private static final List<String> DEFAULT_DEFAULT_ENABLED = List.of(
            "analyticshub.googleapis.com", "apptopology.googleapis.com", "bigquery.googleapis.com",
            "bigqueryconnection.googleapis.com", "bigquerydatapolicy.googleapis.com",
            "bigquerydatatransfer.googleapis.com", "bigquerymigration.googleapis.com",
            "bigqueryreservation.googleapis.com", "bigquerystorage.googleapis.com", "cloudapis.googleapis.com",
            "cloudtrace.googleapis.com", "dataform.googleapis.com", "dataplex.googleapis.com",
            "datastore.googleapis.com", "logging.googleapis.com", "monitoring.googleapis.com",
            "servicemanagement.googleapis.com", "serviceusage.googleapis.com", "sql-component.googleapis.com",
            "storage-api.googleapis.com", "storage-component.googleapis.com", "storage.googleapis.com",
            "telemetry.googleapis.com");

    private final boolean enforcementEnabled;
    private final List<String> defaultEnabled;

    private ServiceUsageConfig(Builder builder) {
        super(builder);
        this.enforcementEnabled = builder.enforcementEnabled;
        this.defaultEnabled = List.copyOf(builder.defaultEnabled);
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
     * Returns whether calls to an API that is not enabled for the request's project are rejected with
     * {@code 403 PERMISSION_DENIED} (reason {@code SERVICE_DISABLED}), over REST and gRPC.
     *
     * @return {@code true} if per-project API enablement is enforced
     */
    public boolean isEnforcementEnabled() {
        return enforcementEnabled;
    }

    /**
     * Returns the APIs that count as enabled for every project until they are disabled.
     *
     * @return the APIs enabled by default
     */
    public List<String> getDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENFORCEMENT_ENABLED", String.valueOf(enforcementEnabled));
            container.withEnv("FLOCI_GCP_SERVICES_SERVICEUSAGE_DEFAULT_ENABLED", String.join(",", defaultEnabled));
        }
    }

    /**
     * Builder for {@link ServiceUsageConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceUsageConfig> {

        private boolean enforcementEnabled = DEFAULT_ENFORCEMENT_ENABLED;
        private List<String> defaultEnabled = DEFAULT_DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via ServiceUsageConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ServiceUsageConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ServiceUsageConfig instance) {
            super(instance);
            this.enforcementEnabled = instance.enforcementEnabled;
            this.defaultEnabled = instance.defaultEnabled;
        }

        /**
         * Sets whether calls to an API that is not enabled for the request's project are rejected the way GCP
         * rejects them: {@code 403 PERMISSION_DENIED} with a {@code google.rpc.ErrorInfo} of reason
         * {@code SERVICE_DISABLED}, over REST and gRPC. APIs from {@link #defaultEnabled(List)} count as enabled
         * until disabled. Service Usage, Resource Manager, IAM Credentials, STS and Cloud Billing are never gated.
         *
         * @param enforcementEnabled {@code true} to enforce per-project API enablement
         *                           (default {@value DEFAULT_ENFORCEMENT_ENABLED})
         * @return this builder
         */
        public Builder enforcementEnabled(boolean enforcementEnabled) {
            this.enforcementEnabled = enforcementEnabled;
            return this;
        }

        /**
         * Sets the APIs that count as enabled for every project until they are disabled, e.g.
         * {@code storage.googleapis.com}. Only relevant while {@link #enforcementEnabled(boolean) enforcement} is
         * enabled.
         *
         * @param defaultEnabled the APIs enabled by default (default GCP's default set for new projects, such as
         *                       {@code bigquery.googleapis.com}, {@code logging.googleapis.com} and
         *                       {@code storage.googleapis.com})
         * @return this builder
         */
        public Builder defaultEnabled(List<String> defaultEnabled) {
            this.defaultEnabled = defaultEnabled;
            return this;
        }

        /**
         * Creates an immutable {@link ServiceUsageConfig} from this builder.
         *
         * @return the Service Usage configuration
         */
        @Override
        public ServiceUsageConfig build() {
            return new ServiceUsageConfig(this);
        }
    }
}
