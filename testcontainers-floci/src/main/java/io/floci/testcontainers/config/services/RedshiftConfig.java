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

    private final int defaultPort;
    private final String imageVersion;
    private final String dockerNetwork;

    private RedshiftConfig(Builder builder) {
        super(builder.enabled);
        this.defaultPort = builder.defaultPort;
        this.imageVersion = builder.imageVersion;
        this.dockerNetwork = builder.dockerNetwork;
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_REDSHIFT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DEFAULT_PORT", String.valueOf(defaultPort));
            container.withEnv("FLOCI_SERVICES_REDSHIFT_IMAGE_VERSION", imageVersion);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_REDSHIFT_DOCKER_NETWORK", dockerNetwork);
            }
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
