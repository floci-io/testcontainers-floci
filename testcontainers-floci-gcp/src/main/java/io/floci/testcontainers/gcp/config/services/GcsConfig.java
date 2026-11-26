package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Storage of Floci GCP.
 *
 * <p>Cloud Storage is served via its JSON, XML and gRPC APIs. Point the {@code Storage} client at
 * {@code FlociGcpContainer#getEndpoint()} (e.g. via {@code StorageOptions.Builder#setHost(String)}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GcsConfig config = GcsConfig.builder()
 *     .uploadSessionIdleTimeoutSeconds(3600)
 *     .uploadSessionSweepIntervalSeconds(60)
 *     .softDeleteSweepIntervalSeconds(60)
 *     .build();
 * }</pre>
 */
public class GcsConfig extends AbstractServiceConfig<GcsConfig.Builder> {

    private static final long DEFAULT_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS = 604800;
    private static final long DEFAULT_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS = 3600;
    private static final long DEFAULT_SOFT_DELETE_SWEEP_INTERVAL_SECONDS = 3600;

    private final long uploadSessionIdleTimeoutSeconds;
    private final long uploadSessionSweepIntervalSeconds;
    private final long softDeleteSweepIntervalSeconds;

    private GcsConfig(Builder builder) {
        super(builder);
        this.uploadSessionIdleTimeoutSeconds = builder.uploadSessionIdleTimeoutSeconds;
        this.uploadSessionSweepIntervalSeconds = builder.uploadSessionSweepIntervalSeconds;
        this.softDeleteSweepIntervalSeconds = builder.softDeleteSweepIntervalSeconds;
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
     * Returns the idle time in seconds after which an unfinished resumable or streaming upload session is dropped
     * along with its buffered bytes.
     *
     * @return the upload session idle timeout in seconds
     */
    public long getUploadSessionIdleTimeoutSeconds() {
        return uploadSessionIdleTimeoutSeconds;
    }

    /**
     * Returns the interval in seconds between sweeps for expired upload sessions. Zero or less disables the
     * sweeper.
     *
     * @return the upload session sweep interval in seconds
     */
    public long getUploadSessionSweepIntervalSeconds() {
        return uploadSessionSweepIntervalSeconds;
    }

    /**
     * Returns the interval in seconds between sweeps that permanently delete soft-deleted objects past their
     * {@code hardDeleteTime}. Zero or less disables the sweeper.
     *
     * @return the soft delete sweep interval in seconds
     */
    public long getSoftDeleteSweepIntervalSeconds() {
        return softDeleteSweepIntervalSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_GCS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS",
                    String.valueOf(uploadSessionIdleTimeoutSeconds));
            container.withEnv("FLOCI_GCP_SERVICES_GCS_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS",
                    String.valueOf(uploadSessionSweepIntervalSeconds));
            container.withEnv("FLOCI_GCP_SERVICES_GCS_SOFT_DELETE_SWEEP_INTERVAL_SECONDS",
                    String.valueOf(softDeleteSweepIntervalSeconds));
        }
    }

    /**
     * Builder for {@link GcsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GcsConfig> {

        private long uploadSessionIdleTimeoutSeconds = DEFAULT_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS;
        private long uploadSessionSweepIntervalSeconds = DEFAULT_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS;
        private long softDeleteSweepIntervalSeconds = DEFAULT_SOFT_DELETE_SWEEP_INTERVAL_SECONDS;

        private Builder() {
            // Allow instantiation only via GcsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GcsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GcsConfig instance) {
            super(instance);
            this.uploadSessionIdleTimeoutSeconds = instance.uploadSessionIdleTimeoutSeconds;
            this.uploadSessionSweepIntervalSeconds = instance.uploadSessionSweepIntervalSeconds;
            this.softDeleteSweepIntervalSeconds = instance.softDeleteSweepIntervalSeconds;
        }

        /**
         * Sets the idle time in seconds after which an unfinished resumable or streaming upload session is
         * dropped along with its buffered bytes. Defaults to the seven-day window of real Cloud Storage
         * resumable sessions.
         *
         * @param uploadSessionIdleTimeoutSeconds the idle timeout in seconds (default {@value DEFAULT_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder uploadSessionIdleTimeoutSeconds(long uploadSessionIdleTimeoutSeconds) {
            this.uploadSessionIdleTimeoutSeconds = uploadSessionIdleTimeoutSeconds;
            return this;
        }

        /**
         * Sets the interval in seconds between sweeps for expired upload sessions. Zero or less disables the
         * sweeper.
         *
         * @param uploadSessionSweepIntervalSeconds the sweep interval in seconds (default {@value DEFAULT_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder uploadSessionSweepIntervalSeconds(long uploadSessionSweepIntervalSeconds) {
            this.uploadSessionSweepIntervalSeconds = uploadSessionSweepIntervalSeconds;
            return this;
        }

        /**
         * Sets the interval in seconds between sweeps that permanently delete soft-deleted objects past their
         * {@code hardDeleteTime}. Zero or less disables the sweeper. Floci GCP enables soft delete for new buckets
         * by default, with a 7-day retention policy, as Cloud Storage does.
         *
         * @param softDeleteSweepIntervalSeconds the sweep interval in seconds (default {@value DEFAULT_SOFT_DELETE_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder softDeleteSweepIntervalSeconds(long softDeleteSweepIntervalSeconds) {
            this.softDeleteSweepIntervalSeconds = softDeleteSweepIntervalSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link GcsConfig} from this builder.
         *
         * @return the Cloud Storage configuration
         */
        @Override
        public GcsConfig build() {
            return new GcsConfig(this);
        }
    }
}
