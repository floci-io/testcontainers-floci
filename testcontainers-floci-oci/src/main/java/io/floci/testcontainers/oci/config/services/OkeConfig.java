package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Oracle Container Engine for Kubernetes (OKE) of Floci OCI.
 *
 * <p>Each cluster is backed by a k3s container whose API server Floci OCI publishes on the Docker host; the
 * cluster's {@code kubernetes} endpoint points at that host port. In {@code mock} mode no k3s container is started.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * OkeConfig config = OkeConfig.builder()
 *     .mock(true)
 *     .defaultImage("rancher/k3s:v1.31.4-k3s1")
 *     .apiServerPortRange(16443, 5)
 *     .build();
 * }</pre>
 */
public class OkeConfig extends AbstractServiceConfig<OkeConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "rancher/k3s:v1.30.1-k3s1";
    private static final int DEFAULT_API_SERVER_BASE_PORT = 6443;
    private static final int DEFAULT_API_SERVER_PORTS_COUNT = 10;

    private final boolean mock;
    private final String defaultImage;
    private final int apiServerBasePort;
    private final int apiServerPortsCount;

    private OkeConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.apiServerBasePort = builder.apiServerBasePort;
        this.apiServerPortsCount = builder.apiServerPortsCount;
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
     * Returns whether no k3s container is started; clusters become {@code ACTIVE} immediately.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker image of the k3s containers backing the clusters.
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the first port of the host port range the k3s API servers are published on.
     *
     * @return the base port
     */
    public int getApiServerBasePort() {
        return apiServerBasePort;
    }

    /**
     * Returns the number of ports of the host port range the k3s API servers are published on, starting from {@link
     * #getApiServerBasePort()}.
     *
     * @return the number of ports
     */
    public int getApiServerPortsCount() {
        return apiServerPortsCount;
    }

    /**
     * Returns the last port of the host port range the k3s API servers are published on.
     *
     * @return the maximum port
     */
    public int getApiServerMaxPort() {
        return apiServerBasePort + apiServerPortsCount - 1;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_OCI_SERVICES_OKE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_OCI_SERVICES_OKE_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_OCI_SERVICES_OKE_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_OCI_SERVICES_OKE_API_SERVER_BASE_PORT", String.valueOf(apiServerBasePort));
            container.withEnv("FLOCI_OCI_SERVICES_OKE_API_SERVER_MAX_PORT", String.valueOf(getApiServerMaxPort()));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link OkeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, OkeConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private int apiServerBasePort = DEFAULT_API_SERVER_BASE_PORT;
        private int apiServerPortsCount = DEFAULT_API_SERVER_PORTS_COUNT;

        private Builder() {
            // Allow instantiation only via OkeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link OkeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(OkeConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.defaultImage = instance.defaultImage;
            this.apiServerBasePort = instance.apiServerBasePort;
            this.apiServerPortsCount = instance.apiServerPortsCount;
        }

        /**
         * Sets whether no k3s container is started; clusters become {@code ACTIVE} immediately. Useful for tests
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
         * Sets the Docker image of the k3s containers backing the clusters.
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range the k3s API servers are published on, one port per cluster. Floci itself allows
         * 6443-6543; this module defaults to 10 ports, i.e. 10 concurrent clusters.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_API_SERVER_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_API_SERVER_PORTS_COUNT})
         * @return this builder
         */
        public Builder apiServerPortRange(int basePort, int amount) {
            this.apiServerBasePort = basePort;
            this.apiServerPortsCount = amount;
            return this;
        }

        /**
         * Creates an immutable {@link OkeConfig} from this builder.
         *
         * @return the OKE configuration
         */
        @Override
        public OkeConfig build() {
            return new OkeConfig(this);
        }
    }
}
