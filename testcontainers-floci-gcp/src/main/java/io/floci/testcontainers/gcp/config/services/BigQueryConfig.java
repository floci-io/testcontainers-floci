package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for BigQuery of Floci GCP.
 *
 * <p>Queries run in a DuckDB sidecar container (floci-duck), which Floci GCP starts on the first query and reaches
 * via a port published on the Docker host. Alternatively, Floci GCP can use an already running floci-duck (see
 * {@link Builder#duckUrl(String)}), or run queries on a built-in SQL subset in {@code mock} mode.
 *
 * <p>The properties of Floci's {@code bigquery.duck} group are flattened into this class
 * ({@code FLOCI_GCP_SERVICES_BIGQUERY_DUCK_*}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BigQueryConfig config = BigQueryConfig.builder()
 *     .duckUrl("http://floci-duck:3000")
 *     .build();
 * }</pre>
 */
public class BigQueryConfig extends AbstractServiceConfig<BigQueryConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final long DEFAULT_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS = 604800;
    private static final long DEFAULT_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS = 3600;
    private static final String DEFAULT_DUCK_DEFAULT_IMAGE = "floci/floci-duck:latest";

    private final boolean mock;
    private final long uploadSessionIdleTimeoutSeconds;
    private final long uploadSessionSweepIntervalSeconds;
    private final String duckUrl;
    private final String duckCallbackUrl;
    private final String duckDefaultImage;

    private BigQueryConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.uploadSessionIdleTimeoutSeconds = builder.uploadSessionIdleTimeoutSeconds;
        this.uploadSessionSweepIntervalSeconds = builder.uploadSessionSweepIntervalSeconds;
        this.duckUrl = builder.duckUrl;
        this.duckCallbackUrl = builder.duckCallbackUrl;
        this.duckDefaultImage = builder.duckDefaultImage;
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
     * Returns whether queries run on the built-in SQL subset instead of the DuckDB sidecar.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the idle time in seconds after which an unfinished resumable media upload of a load job is dropped
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
     * Returns the URL of an already running floci-duck that is used instead of starting a sidecar container, or
     * empty if not set.
     *
     * @return the floci-duck URL, or empty
     */
    public Optional<String> getDuckUrl() {
        return Optional.ofNullable(duckUrl);
    }

    /**
     * Returns the base URL floci-duck uses to read staged rows back from Floci GCP, or empty if not set.
     *
     * @return the callback URL, or empty
     */
    public Optional<String> getDuckCallbackUrl() {
        return Optional.ofNullable(duckCallbackUrl);
    }

    /**
     * Returns the Docker image of the floci-duck sidecar container.
     *
     * @return the floci-duck Docker image
     */
    public String getDuckDefaultImage() {
        return duckDefaultImage;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS",
                    String.valueOf(uploadSessionIdleTimeoutSeconds));
            container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS",
                    String.valueOf(uploadSessionSweepIntervalSeconds));

            if (duckUrl != null) {
                container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_URL", duckUrl);
            }
            if (duckCallbackUrl != null) {
                container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_CALLBACK_URL", duckCallbackUrl);
            }

            container.withEnv("FLOCI_GCP_SERVICES_BIGQUERY_DUCK_DEFAULT_IMAGE", duckDefaultImage);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock && duckUrl == null;
    }

    /**
     * Builder for {@link BigQueryConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BigQueryConfig> {

        private boolean mock = DEFAULT_MOCK;
        private long uploadSessionIdleTimeoutSeconds = DEFAULT_UPLOAD_SESSION_IDLE_TIMEOUT_SECONDS;
        private long uploadSessionSweepIntervalSeconds = DEFAULT_UPLOAD_SESSION_SWEEP_INTERVAL_SECONDS;
        private String duckUrl;
        private String duckCallbackUrl;
        private String duckDefaultImage = DEFAULT_DUCK_DEFAULT_IMAGE;

        private Builder() {
            // Allow instantiation only via BigQueryConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BigQueryConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BigQueryConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.uploadSessionIdleTimeoutSeconds = instance.uploadSessionIdleTimeoutSeconds;
            this.uploadSessionSweepIntervalSeconds = instance.uploadSessionSweepIntervalSeconds;
            this.duckUrl = instance.duckUrl;
            this.duckCallbackUrl = instance.duckCallbackUrl;
            this.duckDefaultImage = instance.duckDefaultImage;
        }

        /**
         * Sets whether queries run on the built-in SQL subset instead of the DuckDB sidecar. Useful for tests
         * without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the idle time in seconds after which an unfinished resumable media upload of a load job is dropped
         * along with its buffered bytes. Defaults to the seven-day window of Google's resumable upload protocol.
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
         * Sets the URL of an already running floci-duck. When set, no sidecar container is started and the
         * service no longer requires the Docker socket.
         *
         * @param duckUrl the floci-duck URL, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder duckUrl(String duckUrl) {
            this.duckUrl = duckUrl;
            return this;
        }

        /**
         * Sets the base URL floci-duck uses to read staged rows back from Floci GCP. Only needed when
         * {@link #duckUrl(String)} points at a floci-duck that cannot reach Floci GCP through the resolved Docker
         * host, e.g. one on another machine.
         *
         * @param duckCallbackUrl the callback URL, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder duckCallbackUrl(String duckCallbackUrl) {
            this.duckCallbackUrl = duckCallbackUrl;
            return this;
        }

        /**
         * Sets the Docker image of the floci-duck sidecar container.
         *
         * @param duckDefaultImage the floci-duck Docker image (default {@value DEFAULT_DUCK_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder duckDefaultImage(String duckDefaultImage) {
            this.duckDefaultImage = duckDefaultImage;
            return this;
        }

        /**
         * Creates an immutable {@link BigQueryConfig} from this builder.
         *
         * @return the BigQuery configuration
         */
        @Override
        public BigQueryConfig build() {
            return new BigQueryConfig(this);
        }
    }
}
