package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EKS (Elastic Kubernetes Service)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EksConfig config = EksConfig.builder()
 *     .enabled(true)
 *     .mock(false)
 *     .provider("k3s")
 *     .apiServerPortRange(6500, 100)
 *     .disableCni(false)
 *     .build();
 * }</pre>
 */
public class EksConfig extends AbstractServiceConfig<EksConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_PROVIDER = "k3s";
    private static final String DEFAULT_IMAGE = "rancher/k3s:latest";
    private static final int DEFAULT_API_SERVER_BASE_PORT = 6500;
    private static final int DEFAULT_API_SERVER_PORTS_COUNT = 10;
    private static final String DEFAULT_ENDPOINT_MODE = "host";
    private static final boolean DEFAULT_IAM_AUTH_WEBHOOK = true;
    private static final boolean DEFAULT_ECR_REGISTRY_MIRROR = true;
    private static final boolean DEFAULT_DISABLE_CNI = false;

    private final boolean mock;
    private final String provider;
    private final String defaultImage;
    private final int apiServerBasePort;
    private final int apiServerPortsCount;
    private final String dockerNetwork;
    private final String endpointMode;
    private final boolean iamAuthWebhook;
    private final boolean ecrRegistryMirror;
    private final boolean disableCni;

    private EksConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.provider = builder.provider;
        this.defaultImage = builder.defaultImage;
        this.apiServerBasePort = builder.apiServerBasePort;
        this.apiServerPortsCount = builder.apiServerPortsCount;
        this.dockerNetwork = builder.dockerNetwork;
        this.endpointMode = builder.endpointMode;
        this.iamAuthWebhook = builder.iamAuthWebhook;
        this.ecrRegistryMirror = builder.ecrRegistryMirror;
        this.disableCni = builder.disableCni;
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
     * Returns whether clusters go straight to ACTIVE without starting real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Kubernetes provider used for EKS clusters.
     *
     * @return the provider name
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the default Docker image used for EKS (k3s) instances.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the base port for the EKS API server port range.
     *
     * @return the base port
     */
    public int getApiServerBasePort() {
        return apiServerBasePort;
    }

    /**
     * Returns the number of ports allocated for the EKS API server, starting from {@link #getApiServerBasePort()}.
     *
     * @return the number of API server ports
     */
    public int getApiServerPortsCount() {
        return apiServerPortsCount;
    }

    /**
     * Returns the maximum port for the EKS API server port range.
     *
     * @return the maximum port
     */
    public int getApiServerMaxPort() {
        return apiServerBasePort + apiServerPortsCount - 1;
    }

    /**
     * Returns the Docker network used for EKS containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    /**
     * Returns the endpoint mode used in {@code describe-cluster} responses.
     *
     * <ul>
     *   <li>{@code host} (default) — {@code https://localhost:<hostPort>}, reachable from the
     *       host so {@code kubectl}/{@code aws eks} work out of the box.</li>
     *   <li>{@code network} — the container DNS name {@code https://floci-eks-<name>:6443},
     *       reachable from other containers on the Docker network (pre-#1118 behaviour). Falls
     *       back to the host endpoint when Floci runs natively.</li>
     * </ul>
     *
     * @return the endpoint mode
     */
    public String getEndpointMode() {
        return endpointMode;
    }

    /**
     * Returns whether a token-authentication webhook is wired into k3s so that the bearer token
     * produced by {@code aws eks get-token} is validated by Floci and mapped to cluster-admin.
     *
     * @return {@code true} if the IAM auth webhook is enabled
     */
    public boolean isIamAuthWebhook() {
        return iamAuthWebhook;
    }

    /**
     * Returns whether each new k3s cluster gets a generated
     * {@code /etc/rancher/k3s/registries.yaml} that mirrors every ECR repository URI the
     * emulator can mint to the registry container's in-network endpoint, so pods can pull images
     * pushed to Floci ECR without any manual containerd configuration. Only takes effect when
     * ECR is also enabled.
     *
     * @return {@code true} if the ECR registry mirror is enabled
     */
    public boolean isEcrRegistryMirror() {
        return ecrRegistryMirror;
    }

    /**
     * Returns whether k3s starts with {@code --flannel-backend=none --disable-network-policy
     * --disable-kube-proxy} instead of its bundled networking stack.
     *
     * <p>k3s's default flannel CNI and kube-proxy run embedded in the k3s server process itself
     * (not separate, killable DaemonSets), so a real CNI (e.g. Cilium) can only cleanly take over
     * if k3s never starts its own in the first place — there is no way to evict them after the
     * fact. CoreDNS, local-path-provisioner, and metrics-server are unaffected; they don't depend
     * on which CNI is in place.
     *
     * @return {@code true} if k3s's bundled CNI, network policy, and kube-proxy are disabled
     */
    public boolean isDisableCni() {
        return disableCni;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_EKS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_EKS_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_EKS_PROVIDER", provider);
            container.withEnv("FLOCI_SERVICES_EKS_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_SERVICES_EKS_API_SERVER_BASE_PORT", String.valueOf(apiServerBasePort));
            container.withEnv("FLOCI_SERVICES_EKS_API_SERVER_MAX_PORT", String.valueOf(getApiServerMaxPort()));
            container.withEnv("FLOCI_SERVICES_EKS_ENDPOINT_MODE", endpointMode);
            container.withEnv("FLOCI_SERVICES_EKS_IAM_AUTH_WEBHOOK", String.valueOf(iamAuthWebhook));
            container.withEnv("FLOCI_SERVICES_EKS_ECR_REGISTRY_MIRROR", String.valueOf(ecrRegistryMirror));
            container.withEnv("FLOCI_SERVICES_EKS_DISABLE_CNI", String.valueOf(disableCni));

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_EKS_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            for (int port = apiServerBasePort; port <= getApiServerMaxPort(); port++) {
                container.addExposedPorts(port);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link EksConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EksConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String provider = DEFAULT_PROVIDER;
        private String defaultImage = DEFAULT_IMAGE;
        private int apiServerBasePort = DEFAULT_API_SERVER_BASE_PORT;
        private int apiServerPortsCount = DEFAULT_API_SERVER_PORTS_COUNT;
        private String dockerNetwork;
        private String endpointMode = DEFAULT_ENDPOINT_MODE;
        private boolean iamAuthWebhook = DEFAULT_IAM_AUTH_WEBHOOK;
        private boolean ecrRegistryMirror = DEFAULT_ECR_REGISTRY_MIRROR;
        private boolean disableCni = DEFAULT_DISABLE_CNI;

        private Builder() {
            // Allow instantiation only via EksConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EksConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EksConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.provider = instance.getProvider();
            this.defaultImage = instance.getDefaultImage();
            this.apiServerBasePort = instance.getApiServerBasePort();
            this.apiServerPortsCount = instance.getApiServerPortsCount();
            this.dockerNetwork = instance.getDockerNetwork();
            this.endpointMode = instance.getEndpointMode();
            this.iamAuthWebhook = instance.isIamAuthWebhook();
            this.ecrRegistryMirror = instance.isEcrRegistryMirror();
            this.disableCni = instance.isDisableCni();
        }

        /**
         * Sets whether clusters go straight to ACTIVE without starting real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Kubernetes provider used for EKS clusters.
         *
         * @param provider the provider name (default {@value DEFAULT_PROVIDER})
         * @return this builder
         */
        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Sets the default Docker image for EKS (k3s) instances.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the port range for the EKS API server.
         *
         * @param basePort the base port (default {@value DEFAULT_API_SERVER_BASE_PORT})
         * @param amount   the amount of ports (default {@value DEFAULT_API_SERVER_PORTS_COUNT})
         * @return this builder
         */
        public Builder apiServerPortRange(int basePort, int amount) {
            this.apiServerBasePort = basePort;
            this.apiServerPortsCount = amount;
            return this;
        }

        /**
         * Sets the Docker network that EKS containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the endpoint mode used in {@code describe-cluster} responses.
         *
         * <ul>
         *   <li>{@code host} (default) — {@code https://localhost:<hostPort>}</li>
         *   <li>{@code network} — the container DNS name {@code https://floci-eks-<name>:6443}</li>
         * </ul>
         *
         * @param endpointMode the endpoint mode (default {@value DEFAULT_ENDPOINT_MODE})
         * @return this builder
         */
        public Builder endpointMode(String endpointMode) {
            this.endpointMode = endpointMode;
            return this;
        }

        /**
         * Controls whether a token-authentication webhook is wired into k3s so that the bearer
         * token produced by {@code aws eks get-token} is validated by Floci and mapped to
         * cluster-admin.
         *
         * @param iamAuthWebhook {@code true} to enable the IAM auth webhook (default {@value DEFAULT_IAM_AUTH_WEBHOOK})
         * @return this builder
         */
        public Builder iamAuthWebhook(boolean iamAuthWebhook) {
            this.iamAuthWebhook = iamAuthWebhook;
            return this;
        }

        /**
         * Controls whether each new k3s cluster gets a generated
         * {@code /etc/rancher/k3s/registries.yaml} that mirrors every ECR repository URI the
         * emulator can mint to the registry container's in-network endpoint, so pods can pull
         * images pushed to Floci ECR without any manual containerd configuration. Only takes
         * effect when ECR is also enabled.
         *
         * @param ecrRegistryMirror {@code true} to enable the ECR registry mirror (default {@value DEFAULT_ECR_REGISTRY_MIRROR})
         * @return this builder
         */
        public Builder ecrRegistryMirror(boolean ecrRegistryMirror) {
            this.ecrRegistryMirror = ecrRegistryMirror;
            return this;
        }

        /**
         * Controls whether k3s starts with {@code --flannel-backend=none --disable-network-policy
         * --disable-kube-proxy} instead of its bundled networking stack.
         *
         * <p>k3s's default flannel CNI and kube-proxy run embedded in the k3s server process
         * itself (not separate, killable DaemonSets), so a real CNI (e.g. Cilium) can only cleanly
         * take over if k3s never starts its own in the first place — there is no way to evict them
         * after the fact. CoreDNS, local-path-provisioner, and metrics-server are unaffected; they
         * don't depend on which CNI is in place.
         *
         * @param disableCni {@code true} to disable k3s's bundled CNI, network policy, and
         *                   kube-proxy (default {@value DEFAULT_DISABLE_CNI})
         * @return this builder
         */
        public Builder disableCni(boolean disableCni) {
            this.disableCni = disableCni;
            return this;
        }

        /**
         * Creates an immutable {@link EksConfig} from this builder.
         *
         * @return the EKS configuration
         */
        @Override
        public EksConfig build() {
            return new EksConfig(this);
        }
    }
}
