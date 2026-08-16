package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.List;
import java.util.Optional;

/**
 * Configuration for MWAA (Managed Workflows for Apache Airflow)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MwaaConfig config = MwaaConfig.builder()
 *     .mock(true)
 *     .proxyPortRange(8700, 50)
 *     .defaultVersion("2.10.5")
 *     .build();
 * }</pre>
 */
public class MwaaConfig extends AbstractServiceConfig<MwaaConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_POSTGRES_IMAGE = "postgres:16-alpine";
    private static final List<String> DEFAULT_SUPPORTED_VERSIONS = List.of("2.10.5", "2.9.3", "2.8.4");
    private static final String DEFAULT_VERSION = "2.10.5";
    private static final int DEFAULT_PROXY_BASE_PORT = 8700;
    private static final int DEFAULT_PROXY_PORTS_COUNT = 10;
    private static final String DEFAULT_DATA_PATH = "./data/mwaa";
    private static final boolean DEFAULT_KEEP_RUNNING_ON_SHUTDOWN = false;
    private static final int DEFAULT_DAG_SYNC_INTERVAL_SECONDS = 30;
    private static final boolean DEFAULT_INSTALL_REQUIREMENTS = true;

    private final boolean mock;
    private final String defaultPostgresImage;
    private final List<String> supportedVersions;
    private final String defaultVersion;
    private final int proxyBasePort;
    private final int proxyPortsCount;
    private final String dataPath;
    private final String dockerNetwork;
    private final boolean keepRunningOnShutdown;
    private final int dagSyncIntervalSeconds;
    private final boolean installRequirements;

    private MwaaConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.defaultPostgresImage = builder.defaultPostgresImage;
        this.supportedVersions = builder.supportedVersions;
        this.defaultVersion = builder.defaultVersion;
        this.proxyBasePort = builder.proxyBasePort;
        this.proxyPortsCount = builder.proxyPortsCount;
        this.dataPath = builder.dataPath;
        this.dockerNetwork = builder.dockerNetwork;
        this.keepRunningOnShutdown = builder.keepRunningOnShutdown;
        this.dagSyncIntervalSeconds = builder.dagSyncIntervalSeconds;
        this.installRequirements = builder.installRequirements;
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
     * Returns whether environments go straight to {@code AVAILABLE} without starting real Docker
     * containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the image used for the per-environment Postgres metadata database.
     *
     * @return the Postgres image name
     */
    public String getDefaultPostgresImage() {
        return defaultPostgresImage;
    }

    /**
     * Returns the Airflow versions environments may request.
     *
     * @return the list of supported Airflow versions
     */
    public List<String> getSupportedVersions() {
        return supportedVersions;
    }

    /**
     * Returns the Airflow version used when {@code CreateEnvironment} omits {@code AirflowVersion}.
     *
     * @return the default Airflow version
     */
    public String getDefaultVersion() {
        return defaultVersion;
    }

    /**
     * Returns the base port of the web/CLI proxy port range.
     *
     * @return the base port
     */
    public int getProxyBasePort() {
        return proxyBasePort;
    }

    /**
     * Returns the number of ports allocated for the MWAA proxy, starting from {@link #getProxyBasePort()}.
     *
     * @return the number of proxy ports
     */
    public int getProxyPortsCount() {
        return proxyPortsCount;
    }

    /**
     * Returns the maximum port for the MWAA proxy port range.
     *
     * @return the maximum port
     */
    public int getProxyMaxPort() {
        return proxyBasePort + proxyPortsCount - 1;
    }

    /**
     * Returns the host path environment data (Postgres/Airflow volumes) is stored under.
     *
     * @return the data path
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * Returns the Docker network used for the Postgres/Airflow containers, or
     * {@link Optional#empty()} if the default bridge network is used.
     *
     * @return the Docker network name, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    /**
     * Returns whether environment containers are left running when Floci shuts down.
     *
     * @return {@code true} if environment containers are kept running on shutdown
     */
    public boolean isKeepRunningOnShutdown() {
        return keepRunningOnShutdown;
    }

    /**
     * Returns the poll interval, in seconds, for syncing DAGs (and optionally requirements) from
     * an environment's S3 {@code DagS3Path} into its Airflow container.
     *
     * @return the DAG sync interval in seconds
     */
    public int getDagSyncIntervalSeconds() {
        return dagSyncIntervalSeconds;
    }

    /**
     * Returns whether {@code RequirementsS3Path} is installed via {@code pip install -r} on
     * create and whenever its ETag changes on a DAG-sync pass.
     *
     * @return {@code true} if requirements installation is enabled
     */
    public boolean isInstallRequirements() {
        return installRequirements;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_MWAA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_MWAA_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_MWAA_DEFAULT_POSTGRES_IMAGE", defaultPostgresImage);
            container.withEnv("FLOCI_SERVICES_MWAA_SUPPORTED_VERSIONS", String.join(",", supportedVersions));
            container.withEnv("FLOCI_SERVICES_MWAA_DEFAULT_VERSION", defaultVersion);
            container.withEnv("FLOCI_SERVICES_MWAA_PROXY_BASE_PORT", String.valueOf(proxyBasePort));
            container.withEnv("FLOCI_SERVICES_MWAA_PROXY_MAX_PORT", String.valueOf(getProxyMaxPort()));
            container.withEnv("FLOCI_SERVICES_MWAA_DATA_PATH", dataPath);
            container.withEnv("FLOCI_SERVICES_MWAA_KEEP_RUNNING_ON_SHUTDOWN", String.valueOf(keepRunningOnShutdown));
            container.withEnv("FLOCI_SERVICES_MWAA_DAG_SYNC_INTERVAL_SECONDS", String.valueOf(dagSyncIntervalSeconds));
            container.withEnv("FLOCI_SERVICES_MWAA_INSTALL_REQUIREMENTS", String.valueOf(installRequirements));

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_MWAA_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            for (int port = proxyBasePort; port <= getProxyMaxPort(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    /**
     * Builder for {@link MwaaConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MwaaConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultPostgresImage = DEFAULT_POSTGRES_IMAGE;
        private List<String> supportedVersions = DEFAULT_SUPPORTED_VERSIONS;
        private String defaultVersion = DEFAULT_VERSION;
        private int proxyBasePort = DEFAULT_PROXY_BASE_PORT;
        private int proxyPortsCount = DEFAULT_PROXY_PORTS_COUNT;
        private String dataPath = DEFAULT_DATA_PATH;
        private String dockerNetwork;
        private boolean keepRunningOnShutdown = DEFAULT_KEEP_RUNNING_ON_SHUTDOWN;
        private int dagSyncIntervalSeconds = DEFAULT_DAG_SYNC_INTERVAL_SECONDS;
        private boolean installRequirements = DEFAULT_INSTALL_REQUIREMENTS;

        private Builder() {
            // Allow instantiation only via MwaaConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MwaaConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MwaaConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultPostgresImage = instance.getDefaultPostgresImage();
            this.supportedVersions = instance.getSupportedVersions();
            this.defaultVersion = instance.getDefaultVersion();
            this.proxyBasePort = instance.getProxyBasePort();
            this.proxyPortsCount = instance.getProxyPortsCount();
            this.dataPath = instance.getDataPath();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
            this.keepRunningOnShutdown = instance.isKeepRunningOnShutdown();
            this.dagSyncIntervalSeconds = instance.getDagSyncIntervalSeconds();
            this.installRequirements = instance.isInstallRequirements();
        }

        /**
         * Sets whether environments go straight to {@code AVAILABLE} without starting real Docker
         * containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the image used for the per-environment Postgres metadata database.
         *
         * @param defaultPostgresImage the image name (default {@value DEFAULT_POSTGRES_IMAGE})
         * @return this builder
         */
        public Builder defaultPostgresImage(String defaultPostgresImage) {
            this.defaultPostgresImage = defaultPostgresImage;
            return this;
        }

        /**
         * Sets the Airflow versions environments may request.
         *
         * @param supportedVersions the list of supported Airflow versions (default {@code ["2.10.5", "2.9.3", "2.8.4"]})
         * @return this builder
         */
        public Builder supportedVersions(List<String> supportedVersions) {
            this.supportedVersions = List.copyOf(supportedVersions);
            return this;
        }

        /**
         * Sets the Airflow version used when {@code CreateEnvironment} omits {@code AirflowVersion}.
         *
         * @param defaultVersion the default Airflow version (default {@value DEFAULT_VERSION})
         * @return this builder
         */
        public Builder defaultVersion(String defaultVersion) {
            this.defaultVersion = defaultVersion;
            return this;
        }

        /**
         * Sets the port range for the MWAA web/CLI proxy.
         *
         * @param basePort the base port (default {@value DEFAULT_PROXY_BASE_PORT})
         * @param amount   the amount of ports (default {@value DEFAULT_PROXY_PORTS_COUNT})
         * @return this builder
         */
        public Builder proxyPortRange(int basePort, int amount) {
            this.proxyBasePort = basePort;
            this.proxyPortsCount = amount;
            return this;
        }

        /**
         * Sets the host path environment data (Postgres/Airflow volumes) is stored under.
         *
         * @param dataPath the data path (default {@value DEFAULT_DATA_PATH})
         * @return this builder
         */
        public Builder dataPath(String dataPath) {
            this.dataPath = dataPath;
            return this;
        }

        /**
         * Sets the Docker network that the Postgres/Airflow containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets whether environment containers are left running when Floci shuts down.
         *
         * @param keepRunningOnShutdown {@code true} to keep environment containers running (default {@value DEFAULT_KEEP_RUNNING_ON_SHUTDOWN})
         * @return this builder
         */
        public Builder keepRunningOnShutdown(boolean keepRunningOnShutdown) {
            this.keepRunningOnShutdown = keepRunningOnShutdown;
            return this;
        }

        /**
         * Sets the poll interval, in seconds, for syncing DAGs (and optionally requirements) from
         * an environment's S3 {@code DagS3Path} into its Airflow container.
         *
         * @param dagSyncIntervalSeconds the sync interval in seconds (default {@value DEFAULT_DAG_SYNC_INTERVAL_SECONDS})
         * @return this builder
         */
        public Builder dagSyncIntervalSeconds(int dagSyncIntervalSeconds) {
            this.dagSyncIntervalSeconds = dagSyncIntervalSeconds;
            return this;
        }

        /**
         * Sets whether {@code RequirementsS3Path} is installed via {@code pip install -r} on
         * create and whenever its ETag changes on a DAG-sync pass.
         *
         * @param installRequirements {@code true} to enable requirements installation (default {@value DEFAULT_INSTALL_REQUIREMENTS})
         * @return this builder
         */
        public Builder installRequirements(boolean installRequirements) {
            this.installRequirements = installRequirements;
            return this;
        }

        /**
         * Creates an immutable {@link MwaaConfig} from this builder.
         *
         * @return the MWAA configuration
         */
        @Override
        public MwaaConfig build() {
            return new MwaaConfig(this);
        }
    }
}
