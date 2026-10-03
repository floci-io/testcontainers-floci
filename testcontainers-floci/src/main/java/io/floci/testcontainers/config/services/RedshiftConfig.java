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
    private static final int DEFAULT_PROXY_MAX_CONNECTIONS = 100;

    private final int defaultPort;
    private final String imageVersion;
    private final String dockerNetwork;
    private final long pollIntervalMs;
    private final int defaultCredentialDurationSeconds;
    private final int proxyHandshakeTimeoutMillis;
    private final int proxyBackendConnectTimeoutMillis;
    private final int proxyMaxConnections;

    private RedshiftConfig(Builder builder) {
        super(builder.enabled);
        this.defaultPort = builder.defaultPort;
        this.imageVersion = builder.imageVersion;
        this.dockerNetwork = builder.dockerNetwork;
        this.pollIntervalMs = builder.pollIntervalMs;
        this.defaultCredentialDurationSeconds = builder.defaultCredentialDurationSeconds;
        this.proxyHandshakeTimeoutMillis = builder.proxyHandshakeTimeoutMillis;
        this.proxyBackendConnectTimeoutMillis = builder.proxyBackendConnectTimeoutMillis;
        this.proxyMaxConnections = builder.proxyMaxConnections;
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
