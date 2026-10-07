package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud SQL of Floci GCP.
 *
 * <p>Each PostgreSQL or MySQL instance is backed by a database container whose port Floci GCP publishes on the
 * Docker host; the instance's IP address and port point at it. In {@code mock} mode only the control plane is
 * emulated and no containers are started.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudSqlConfig config = CloudSqlConfig.builder()
 *     .postgres17Image("postgres:17-alpine")
 *     .startupTimeoutSeconds(120)
 *     .build();
 * }</pre>
 */
public class CloudSqlConfig extends AbstractServiceConfig<CloudSqlConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_POSTGRES15_IMAGE = "postgres:15.18-alpine";
    private static final String DEFAULT_POSTGRES16_IMAGE = "postgres:16.14-alpine";
    private static final String DEFAULT_POSTGRES17_IMAGE = "postgres:17.10-alpine";
    private static final String DEFAULT_POSTGRES18_IMAGE = "postgres:18.4-alpine";
    private static final String DEFAULT_MYSQL80_IMAGE = "mysql:8.0.46";
    private static final String DEFAULT_MYSQL84_IMAGE = "mysql:8.4.11";
    private static final int DEFAULT_STARTUP_TIMEOUT_SECONDS = 90;

    private final boolean mock;
    private final String postgres15Image;
    private final String postgres16Image;
    private final String postgres17Image;
    private final String postgres18Image;
    private final String mysql80Image;
    private final String mysql84Image;
    private final int startupTimeoutSeconds;

    private CloudSqlConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.postgres15Image = builder.postgres15Image;
        this.postgres16Image = builder.postgres16Image;
        this.postgres17Image = builder.postgres17Image;
        this.postgres18Image = builder.postgres18Image;
        this.mysql80Image = builder.mysql80Image;
        this.mysql84Image = builder.mysql84Image;
        this.startupTimeoutSeconds = builder.startupTimeoutSeconds;
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
     * Returns whether no database containers are started; instances are only emulated as control plane
     * resources.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker image used for {@code POSTGRES_15} instances.
     *
     * @return the Docker image
     */
    public String getPostgres15Image() {
        return postgres15Image;
    }

    /**
     * Returns the Docker image used for {@code POSTGRES_16} instances.
     *
     * @return the Docker image
     */
    public String getPostgres16Image() {
        return postgres16Image;
    }

    /**
     * Returns the Docker image used for {@code POSTGRES_17} instances.
     *
     * @return the Docker image
     */
    public String getPostgres17Image() {
        return postgres17Image;
    }

    /**
     * Returns the Docker image used for {@code POSTGRES_18} instances.
     *
     * @return the Docker image
     */
    public String getPostgres18Image() {
        return postgres18Image;
    }

    /**
     * Returns the Docker image used for {@code MYSQL_8_0} and {@code MYSQL_8_0_NN} instances.
     *
     * @return the Docker image
     */
    public String getMysql80Image() {
        return mysql80Image;
    }

    /**
     * Returns the Docker image used for {@code MYSQL_8_4} instances.
     *
     * @return the Docker image
     */
    public String getMysql84Image() {
        return mysql84Image;
    }

    /**
     * Returns the maximum time in seconds to wait for a database container to accept connections after it was
     * started.
     *
     * @return the startup timeout in seconds
     */
    public int getStartupTimeoutSeconds() {
        return startupTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES15_IMAGE", postgres15Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES16_IMAGE", postgres16Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES17_IMAGE", postgres17Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_POSTGRES18_IMAGE", postgres18Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL80_IMAGE", mysql80Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_MYSQL84_IMAGE", mysql84Image);
            container.withEnv("FLOCI_GCP_SERVICES_CLOUDSQL_STARTUP_TIMEOUT_SECONDS", String.valueOf(startupTimeoutSeconds));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link CloudSqlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudSqlConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String postgres15Image = DEFAULT_POSTGRES15_IMAGE;
        private String postgres16Image = DEFAULT_POSTGRES16_IMAGE;
        private String postgres17Image = DEFAULT_POSTGRES17_IMAGE;
        private String postgres18Image = DEFAULT_POSTGRES18_IMAGE;
        private String mysql80Image = DEFAULT_MYSQL80_IMAGE;
        private String mysql84Image = DEFAULT_MYSQL84_IMAGE;
        private int startupTimeoutSeconds = DEFAULT_STARTUP_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via CloudSqlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudSqlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudSqlConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.postgres15Image = instance.postgres15Image;
            this.postgres16Image = instance.postgres16Image;
            this.postgres17Image = instance.postgres17Image;
            this.postgres18Image = instance.postgres18Image;
            this.mysql80Image = instance.mysql80Image;
            this.mysql84Image = instance.mysql84Image;
            this.startupTimeoutSeconds = instance.startupTimeoutSeconds;
        }

        /**
         * Sets whether no database containers are started; instances are only emulated as control plane
         * resources. Useful for tests without Docker.
         *
         * @param mock {@code true} to mock the service without Docker (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Docker image used for {@code POSTGRES_15} instances.
         *
         * @param postgres15Image the Docker image (default {@value DEFAULT_POSTGRES15_IMAGE})
         * @return this builder
         */
        public Builder postgres15Image(String postgres15Image) {
            this.postgres15Image = postgres15Image;
            return this;
        }

        /**
         * Sets the Docker image used for {@code POSTGRES_16} instances.
         *
         * @param postgres16Image the Docker image (default {@value DEFAULT_POSTGRES16_IMAGE})
         * @return this builder
         */
        public Builder postgres16Image(String postgres16Image) {
            this.postgres16Image = postgres16Image;
            return this;
        }

        /**
         * Sets the Docker image used for {@code POSTGRES_17} instances.
         *
         * @param postgres17Image the Docker image (default {@value DEFAULT_POSTGRES17_IMAGE})
         * @return this builder
         */
        public Builder postgres17Image(String postgres17Image) {
            this.postgres17Image = postgres17Image;
            return this;
        }

        /**
         * Sets the Docker image used for {@code POSTGRES_18} instances.
         *
         * @param postgres18Image the Docker image (default {@value DEFAULT_POSTGRES18_IMAGE})
         * @return this builder
         */
        public Builder postgres18Image(String postgres18Image) {
            this.postgres18Image = postgres18Image;
            return this;
        }

        /**
         * Sets the Docker image used for {@code MYSQL_8_0} and {@code MYSQL_8_0_NN} instances.
         *
         * @param mysql80Image the Docker image (default {@value DEFAULT_MYSQL80_IMAGE})
         * @return this builder
         */
        public Builder mysql80Image(String mysql80Image) {
            this.mysql80Image = mysql80Image;
            return this;
        }

        /**
         * Sets the Docker image used for {@code MYSQL_8_4} instances.
         *
         * @param mysql84Image the Docker image (default {@value DEFAULT_MYSQL84_IMAGE})
         * @return this builder
         */
        public Builder mysql84Image(String mysql84Image) {
            this.mysql84Image = mysql84Image;
            return this;
        }

        /**
         * Sets the maximum time in seconds to wait for a database container to accept connections after it was
         * started.
         *
         * @param startupTimeoutSeconds the startup timeout in seconds (default {@value DEFAULT_STARTUP_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder startupTimeoutSeconds(int startupTimeoutSeconds) {
            this.startupTimeoutSeconds = startupTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link CloudSqlConfig} from this builder.
         *
         * @return the Cloud SQL configuration
         */
        @Override
        public CloudSqlConfig build() {
            return new CloudSqlConfig(this);
        }
    }
}
