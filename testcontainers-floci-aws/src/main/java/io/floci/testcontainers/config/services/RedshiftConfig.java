package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Redshift-specific container settings.
 *
 * <p>Redshift clusters are backed by sibling PostgreSQL Docker containers, so an enabled
 * Redshift service requires access to the host Docker socket.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedshiftConfig config = RedshiftConfig.builder()
 *     .defaultPort(5439)
 *     .imageVersion("postgres:16-alpine")
 *     .dockerNetwork("my-redshift-network")
 *     .build();
 * }</pre>
 */
public class RedshiftConfig extends AbstractServiceConfig<RedshiftConfig.Builder> {

    private static final int DEFAULT_PORT = 5439;
    private static final String DEFAULT_IMAGE_VERSION = "postgres:15-alpine";
    private static final long DEFAULT_POLL_INTERVAL_MS = 1000L;
    private static final int DEFAULT_DEFAULT_CREDENTIAL_DURATION_SECONDS = 900;
    private static final int DEFAULT_PROXY_HANDSHAKE_TIMEOUT_MILLIS = 10000;
    private static final int DEFAULT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int DEFAULT_PROXY_MAX_CONNECTIONS = 2000;
    private static final long DEFAULT_SPECTRUM_MAX_ROWS = 1000000L;
    private static final boolean DEFAULT_AUTOMATED_SNAPSHOTS_ENABLED = true;
    private static final boolean DEFAULT_CLOUDWATCH_METRICS_ENABLED = true;
    private static final long DEFAULT_SNAPSHOT_SWEEP_INTERVAL_SECONDS = 60L;
    private static final boolean DEFAULT_SNAPSHOT_COPY_ENABLED = true;
    private static final long DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS = 60L;
    private static final boolean DEFAULT_SCHEDULED_ACTIONS_ENABLED = true;
    private static final long DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS = 60L;
    private static final boolean DEFAULT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT = false;

    private final int defaultPort;
    private final String imageVersion;
    private final String dockerNetwork;
    private final long pollIntervalMs;
    private final int defaultCredentialDurationSeconds;
    private final int proxyHandshakeTimeoutMillis;
    private final int proxyBackendConnectTimeoutMillis;
    private final int proxyMaxConnections;
    private final long spectrumMaxRows;
    private final boolean automatedSnapshotsEnabled;
    private final boolean cloudwatchMetricsEnabled;
    private final long snapshotSweepIntervalSeconds;
    private final boolean snapshotCopyEnabled;
    private final long snapshotCopySweepIntervalSeconds;
    private final boolean scheduledActionsEnabled;
    private final long scheduledActionSweepIntervalSeconds;
    private final boolean spectrumLakeFormationEnforcement;

    private RedshiftConfig(Builder builder) {
        super(builder);
        this.defaultPort = builder.defaultPort;
        this.imageVersion = builder.imageVersion;
        this.dockerNetwork = builder.dockerNetwork;
        this.pollIntervalMs = builder.pollIntervalMs;
        this.defaultCredentialDurationSeconds = builder.defaultCredentialDurationSeconds;
        this.proxyHandshakeTimeoutMillis = builder.proxyHandshakeTimeoutMillis;
        this.proxyBackendConnectTimeoutMillis = builder.proxyBackendConnectTimeoutMillis;
        this.proxyMaxConnections = builder.proxyMaxConnections;
        this.spectrumMaxRows = builder.spectrumMaxRows;
        this.automatedSnapshotsEnabled = builder.automatedSnapshotsEnabled;
        this.cloudwatchMetricsEnabled = builder.cloudwatchMetricsEnabled;
        this.snapshotSweepIntervalSeconds = builder.snapshotSweepIntervalSeconds;
        this.snapshotCopyEnabled = builder.snapshotCopyEnabled;
        this.snapshotCopySweepIntervalSeconds = builder.snapshotCopySweepIntervalSeconds;
        this.scheduledActionsEnabled = builder.scheduledActionsEnabled;
        this.scheduledActionSweepIntervalSeconds = builder.scheduledActionSweepIntervalSeconds;
        this.spectrumLakeFormationEnforcement = builder.spectrumLakeFormationEnforcement;
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
     * Returns the default port advertised for Redshift cluster endpoints.
     *
     * @return the default port
     */
    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * Returns the Docker image used for the PostgreSQL containers backing Redshift clusters.
     *
     * @return the image name
     */
    public String getImageVersion() {
        return imageVersion;
    }

    /**
     * Returns the Docker network used for Redshift cluster containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    /**
     * Returns the interval, in milliseconds, at which DynamoDB to Redshift zero-ETL integrations poll their
     * source table for changes.
     *
     * @return the interval, in milliseconds, at which DynamoDB to Redshift zero-ETL integrations poll their source table for changes
     */
    public long getPollIntervalMs() {
        return pollIntervalMs;
    }

    /**
     * Returns the default lifetime, in seconds, of credentials returned by GetClusterCredentials and
     * GetClusterCredentialsWithIAM when {@code DurationSeconds} is omitted.
     *
     * <p>AWS allows 900 to 3600.
     *
     * @return the default lifetime, in seconds, of credentials returned by GetClusterCredentials and GetClusterCredentialsWithIAM when {@code DurationSeconds} is omitted
     */
    public int getDefaultCredentialDurationSeconds() {
        return defaultCredentialDurationSeconds;
    }

    /**
     * Returns how long, in milliseconds, a client has to complete the startup/auth handshake with the
     * per-cluster auth proxy.
     *
     * @return how long, in milliseconds, a client has to complete the startup/auth handshake with the per-cluster auth proxy
     */
    public int getProxyHandshakeTimeoutMillis() {
        return proxyHandshakeTimeoutMillis;
    }

    /**
     * Returns how long, in milliseconds, a backend connect attempt of the per-cluster auth proxy may take.
     *
     * @return how long, in milliseconds, a backend connect attempt of the per-cluster auth proxy may take
     */
    public int getProxyBackendConnectTimeoutMillis() {
        return proxyBackendConnectTimeoutMillis;
    }

    /**
     * Returns how many concurrent connections the per-cluster auth proxy accepts before refusing new ones.
     *
     * @return how many concurrent connections the per-cluster auth proxy accepts before refusing new ones
     */
    public int getProxyMaxConnections() {
        return proxyMaxConnections;
    }

    /**
     * Returns the maximum number of rows a Glue-backed Redshift Spectrum external table may load into the
     * cluster's PostgreSQL; loading a larger table fails.
     *
     * @return the maximum number of rows a Glue-backed Redshift Spectrum external table may load
     */
    public long getSpectrumMaxRows() {
        return spectrumMaxRows;
    }

    /**
     * Returns whether Floci takes the automated snapshots of clusters on their snapshot schedules.
     *
     * @return whether Floci takes the automated snapshots of clusters on their snapshot schedules
     */
    public boolean isAutomatedSnapshotsEnabled() {
        return automatedSnapshotsEnabled;
    }

    /**
     * Returns whether Floci publishes the {@code AWS/Redshift} CloudWatch metrics of every available cluster once a minute.
     *
     * @return whether Floci publishes the {@code AWS/Redshift} CloudWatch metrics of every available cluster once a minute
     */
    public boolean isCloudwatchMetricsEnabled() {
        return cloudwatchMetricsEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for due automated snapshots.
     *
     * @return how often, in seconds, Floci checks for due automated snapshots
     */
    public long getSnapshotSweepIntervalSeconds() {
        return snapshotSweepIntervalSeconds;
    }

    /**
     * Returns whether Floci copies the snapshots of clusters with snapshot copy enabled into the destination region.
     *
     * @return whether Floci copies the snapshots of clusters with snapshot copy enabled into the destination region
     */
    public boolean isSnapshotCopyEnabled() {
        return snapshotCopyEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for snapshots to copy into their destination region.
     *
     * @return how often, in seconds, Floci checks for snapshots to copy into their destination region
     */
    public long getSnapshotCopySweepIntervalSeconds() {
        return snapshotCopySweepIntervalSeconds;
    }

    /**
     * Returns whether Floci runs the scheduled actions of provisioned clusters, which pause or resume a cluster on a schedule.
     *
     * @return whether Floci runs the scheduled actions of provisioned clusters, which pause or resume a cluster on a schedule
     */
    public boolean isScheduledActionsEnabled() {
        return scheduledActionsEnabled;
    }

    /**
     * Returns how often, in seconds, Floci checks for due scheduled actions.
     *
     * @return how often, in seconds, Floci checks for due scheduled actions
     */
    public long getScheduledActionSweepIntervalSeconds() {
        return scheduledActionSweepIntervalSeconds;
    }

    /**
     * Returns whether Redshift Spectrum queries enforce the Lake Formation permissions of the external tables they read.
     *
     * @return whether Redshift Spectrum queries enforce the Lake Formation permissions of the external tables they read
     */
    public boolean isSpectrumLakeFormationEnforcement() {
        return spectrumLakeFormationEnforcement;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_REDSHIFT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", String.valueOf(defaultPort));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", imageVersion);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK", dockerNetwork);
            }

            container.withEnv("FLOCI_SERVICES_REDSHIFT_POLL_INTERVAL_MS", String.valueOf(pollIntervalMs));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DEFAULT_CREDENTIAL_DURATION_SECONDS", String.valueOf(defaultCredentialDurationSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_PROXY_HANDSHAKE_TIMEOUT_MILLIS", String.valueOf(proxyHandshakeTimeoutMillis));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS", String.valueOf(proxyBackendConnectTimeoutMillis));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_PROXY_MAX_CONNECTIONS", String.valueOf(proxyMaxConnections));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SPECTRUM_MAX_ROWS", String.valueOf(spectrumMaxRows));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_AUTOMATED_SNAPSHOTS_ENABLED", String.valueOf(automatedSnapshotsEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_CLOUDWATCH_METRICS_ENABLED", String.valueOf(cloudwatchMetricsEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_SWEEP_INTERVAL_SECONDS", String.valueOf(snapshotSweepIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_ENABLED", String.valueOf(snapshotCopyEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS", String.valueOf(snapshotCopySweepIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTIONS_ENABLED", String.valueOf(scheduledActionsEnabled));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS", String.valueOf(scheduledActionSweepIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT", String.valueOf(spectrumLakeFormationEnforcement));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled();
    }

    /**
     * Builder for {@link RedshiftConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedshiftConfig> {

        private int defaultPort = DEFAULT_PORT;
        private String imageVersion = DEFAULT_IMAGE_VERSION;
        private String dockerNetwork;
        private long pollIntervalMs = DEFAULT_POLL_INTERVAL_MS;
        private int defaultCredentialDurationSeconds = DEFAULT_DEFAULT_CREDENTIAL_DURATION_SECONDS;
        private int proxyHandshakeTimeoutMillis = DEFAULT_PROXY_HANDSHAKE_TIMEOUT_MILLIS;
        private int proxyBackendConnectTimeoutMillis = DEFAULT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS;
        private int proxyMaxConnections = DEFAULT_PROXY_MAX_CONNECTIONS;
        private long spectrumMaxRows = DEFAULT_SPECTRUM_MAX_ROWS;
        private boolean automatedSnapshotsEnabled = DEFAULT_AUTOMATED_SNAPSHOTS_ENABLED;
        private boolean cloudwatchMetricsEnabled = DEFAULT_CLOUDWATCH_METRICS_ENABLED;
        private long snapshotSweepIntervalSeconds = DEFAULT_SNAPSHOT_SWEEP_INTERVAL_SECONDS;
        private boolean snapshotCopyEnabled = DEFAULT_SNAPSHOT_COPY_ENABLED;
        private long snapshotCopySweepIntervalSeconds = DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS;
        private boolean scheduledActionsEnabled = DEFAULT_SCHEDULED_ACTIONS_ENABLED;
        private long scheduledActionSweepIntervalSeconds = DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS;
        private boolean spectrumLakeFormationEnforcement = DEFAULT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT;

        private Builder() {
            // Allow instantiation only via RedshiftConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedshiftConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedshiftConfig instance) {
            super(instance);
            this.defaultPort = instance.getDefaultPort();
            this.imageVersion = instance.getImageVersion();
            this.dockerNetwork = instance.getDockerNetwork();
            this.pollIntervalMs = instance.getPollIntervalMs();
            this.defaultCredentialDurationSeconds = instance.getDefaultCredentialDurationSeconds();
            this.proxyHandshakeTimeoutMillis = instance.getProxyHandshakeTimeoutMillis();
            this.proxyBackendConnectTimeoutMillis = instance.getProxyBackendConnectTimeoutMillis();
            this.proxyMaxConnections = instance.getProxyMaxConnections();
            this.spectrumMaxRows = instance.getSpectrumMaxRows();
            this.automatedSnapshotsEnabled = instance.isAutomatedSnapshotsEnabled();
            this.cloudwatchMetricsEnabled = instance.isCloudwatchMetricsEnabled();
            this.snapshotSweepIntervalSeconds = instance.getSnapshotSweepIntervalSeconds();
            this.snapshotCopyEnabled = instance.isSnapshotCopyEnabled();
            this.snapshotCopySweepIntervalSeconds = instance.getSnapshotCopySweepIntervalSeconds();
            this.scheduledActionsEnabled = instance.isScheduledActionsEnabled();
            this.scheduledActionSweepIntervalSeconds = instance.getScheduledActionSweepIntervalSeconds();
            this.spectrumLakeFormationEnforcement = instance.isSpectrumLakeFormationEnforcement();
        }

        /**
         * Sets the default port advertised for Redshift cluster endpoints.
         *
         * @param defaultPort the default port (default {@value DEFAULT_PORT})
         * @return this builder
         */
        public Builder defaultPort(int defaultPort) {
            this.defaultPort = defaultPort;
            return this;
        }

        /**
         * Sets the Docker image used for the PostgreSQL containers backing Redshift clusters.
         *
         * @param imageVersion the image name (default {@value DEFAULT_IMAGE_VERSION})
         * @return this builder
         */
        public Builder imageVersion(String imageVersion) {
            this.imageVersion = imageVersion;
            return this;
        }

        /**
         * Sets the Docker network that Redshift cluster containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the interval, in milliseconds, at which DynamoDB to Redshift zero-ETL integrations poll their
         * source table for changes.
         *
         * @param pollIntervalMs the interval, in milliseconds, at which DynamoDB to Redshift zero-ETL integrations poll their source table for changes (default {@value DEFAULT_POLL_INTERVAL_MS})
         * @return this builder
         */
        public Builder pollIntervalMs(long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
            return this;
        }

        /**
         * Sets the default lifetime, in seconds, of credentials returned by GetClusterCredentials and
         * GetClusterCredentialsWithIAM when {@code DurationSeconds} is omitted.
         *
         * <p>AWS allows 900 to 3600.
         *
         * @param defaultCredentialDurationSeconds the default lifetime, in seconds, of credentials returned by GetClusterCredentials and GetClusterCredentialsWithIAM when {@code DurationSeconds} is omitted (default {@value DEFAULT_DEFAULT_CREDENTIAL_DURATION_SECONDS})
         * @return this builder
         */
        public Builder defaultCredentialDurationSeconds(int defaultCredentialDurationSeconds) {
            this.defaultCredentialDurationSeconds = defaultCredentialDurationSeconds;
            return this;
        }

        /**
         * Sets how long, in milliseconds, a client has to complete the startup/auth handshake with the
         * per-cluster auth proxy.
         *
         * @param proxyHandshakeTimeoutMillis how long, in milliseconds, a client has to complete the startup/auth handshake with the per-cluster auth proxy (default {@value DEFAULT_PROXY_HANDSHAKE_TIMEOUT_MILLIS})
         * @return this builder
         */
        public Builder proxyHandshakeTimeoutMillis(int proxyHandshakeTimeoutMillis) {
            this.proxyHandshakeTimeoutMillis = proxyHandshakeTimeoutMillis;
            return this;
        }

        /**
         * Sets how long, in milliseconds, a backend connect attempt of the per-cluster auth proxy may take.
         *
         * @param proxyBackendConnectTimeoutMillis how long, in milliseconds, a backend connect attempt of the per-cluster auth proxy may take (default {@value DEFAULT_PROXY_BACKEND_CONNECT_TIMEOUT_MILLIS})
         * @return this builder
         */
        public Builder proxyBackendConnectTimeoutMillis(int proxyBackendConnectTimeoutMillis) {
            this.proxyBackendConnectTimeoutMillis = proxyBackendConnectTimeoutMillis;
            return this;
        }

        /**
         * Sets how many concurrent connections the per-cluster auth proxy accepts before refusing new ones.
         *
         * @param proxyMaxConnections how many concurrent connections the per-cluster auth proxy accepts before refusing new ones (default {@value DEFAULT_PROXY_MAX_CONNECTIONS})
         * @return this builder
         */
        public Builder proxyMaxConnections(int proxyMaxConnections) {
            this.proxyMaxConnections = proxyMaxConnections;
            return this;
        }

        /**
         * Sets the maximum number of rows a Glue-backed Redshift Spectrum external table may load into the
         * cluster's PostgreSQL; loading a larger table fails.
         *
         * @param spectrumMaxRows the maximum number of rows a Glue-backed Redshift Spectrum external table may load (default {@value DEFAULT_SPECTRUM_MAX_ROWS})
         * @return this builder
         */
        public Builder spectrumMaxRows(long spectrumMaxRows) {
            this.spectrumMaxRows = spectrumMaxRows;
            return this;
        }

        /**
         * Sets whether Floci takes the automated snapshots of clusters on their snapshot schedules.
         *
         * @param automatedSnapshotsEnabled whether Floci takes the automated snapshots of clusters on their snapshot schedules (default {@value DEFAULT_AUTOMATED_SNAPSHOTS_ENABLED})
         * @return this builder
         */
        public Builder automatedSnapshotsEnabled(boolean automatedSnapshotsEnabled) {
            this.automatedSnapshotsEnabled = automatedSnapshotsEnabled;
            return this;
        }

        /**
         * Sets whether Floci publishes the {@code AWS/Redshift} CloudWatch metrics of every available cluster once a minute.
         *
         * @param cloudwatchMetricsEnabled whether Floci publishes the {@code AWS/Redshift} CloudWatch metrics of every available cluster once a minute (default {@value DEFAULT_CLOUDWATCH_METRICS_ENABLED})
         * @return this builder
         */
        public Builder cloudwatchMetricsEnabled(boolean cloudwatchMetricsEnabled) {
            this.cloudwatchMetricsEnabled = cloudwatchMetricsEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for due automated snapshots.
         *
         * @param snapshotSweepIntervalSeconds how often, in seconds, Floci checks for due automated snapshots (default {@value DEFAULT_SNAPSHOT_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder snapshotSweepIntervalSeconds(long snapshotSweepIntervalSeconds) {
            this.snapshotSweepIntervalSeconds = snapshotSweepIntervalSeconds;
            return this;
        }

        /**
         * Sets whether Floci copies the snapshots of clusters with snapshot copy enabled into the destination region.
         *
         * @param snapshotCopyEnabled whether Floci copies the snapshots of clusters with snapshot copy enabled into the destination region (default {@value DEFAULT_SNAPSHOT_COPY_ENABLED})
         * @return this builder
         */
        public Builder snapshotCopyEnabled(boolean snapshotCopyEnabled) {
            this.snapshotCopyEnabled = snapshotCopyEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for snapshots to copy into their destination region.
         *
         * @param snapshotCopySweepIntervalSeconds how often, in seconds, Floci checks for snapshots to copy into their destination region (default {@value DEFAULT_SNAPSHOT_COPY_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder snapshotCopySweepIntervalSeconds(long snapshotCopySweepIntervalSeconds) {
            this.snapshotCopySweepIntervalSeconds = snapshotCopySweepIntervalSeconds;
            return this;
        }

        /**
         * Sets whether Floci runs the scheduled actions of provisioned clusters, which pause or resume a cluster on a schedule.
         *
         * @param scheduledActionsEnabled whether Floci runs the scheduled actions of provisioned clusters, which pause or resume a cluster on a schedule (default {@value DEFAULT_SCHEDULED_ACTIONS_ENABLED})
         * @return this builder
         */
        public Builder scheduledActionsEnabled(boolean scheduledActionsEnabled) {
            this.scheduledActionsEnabled = scheduledActionsEnabled;
            return this;
        }

        /**
         * Sets how often, in seconds, Floci checks for due scheduled actions.
         *
         * @param scheduledActionSweepIntervalSeconds how often, in seconds, Floci checks for due scheduled actions (default {@value DEFAULT_SCHEDULED_ACTION_SWEEP_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder scheduledActionSweepIntervalSeconds(long scheduledActionSweepIntervalSeconds) {
            this.scheduledActionSweepIntervalSeconds = scheduledActionSweepIntervalSeconds;
            return this;
        }

        /**
         * Sets whether Redshift Spectrum queries enforce the Lake Formation permissions of the external tables they read.
         *
         * @param spectrumLakeFormationEnforcement whether Redshift Spectrum queries enforce the Lake Formation permissions of the external tables they read (default {@value DEFAULT_SPECTRUM_LAKE_FORMATION_ENFORCEMENT})
         * @return this builder
         */
        public Builder spectrumLakeFormationEnforcement(boolean spectrumLakeFormationEnforcement) {
            this.spectrumLakeFormationEnforcement = spectrumLakeFormationEnforcement;
            return this;
        }

        /**
         * Creates an immutable {@link RedshiftConfig} from this builder.
         *
         * @return the Redshift configuration
         */
        @Override
        public RedshiftConfig build() {
            return new RedshiftConfig(this);
        }
    }
}
