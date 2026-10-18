package io.floci.testcontainers.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable description of one Floci emulator: its image, port, paths and the names of the environment variables
 * the provider-independent {@link AbstractFlociContainer} needs to configure it.
 *
 * <p>Each container class exposes its descriptor as {@code public static final CloudDescriptor DESCRIPTOR} and passes
 * it to {@link AbstractFlociContainer#AbstractFlociContainer(org.testcontainers.utility.DockerImageName, CloudDescriptor)}.
 *
 * <pre>{@code
 * CloudDescriptor descriptor = CloudDescriptor.builder()
 *     .name("az")
 *     .image("floci/floci-az")
 *     .port(4577)
 *     .envPrefix("FLOCI_AZ_")
 *     .healthPath("/_floci/health")
 *     .resetPath("/_admin/reset")
 *     .logLevelEnvVar("QUARKUS_LOG_CATEGORY__IO_FLOCI_AZ__LEVEL")
 *     .networkEnvVar("FLOCI_AZ_SERVICES_DOCKER_NETWORK")
 *     .resourceNamespaceEnvVar("FLOCI_AZ_DOCKER_RESOURCE_NAMESPACE")
 *     .tlsHttpsPortEnvVar("FLOCI_AZ_TLS_HTTPS_PORT")
 *     .defaultEnv("FLOCI_AZ_TLS_HTTPS_PORT", "0")
 *     .build();
 * }</pre>
 */
public final class CloudDescriptor {

    private final String name;
    private final String image;
    private final int port;
    private final String envPrefix;
    private final String healthPath;
    private final String resetPath;
    private final String logLevelEnvVar;
    private final String networkEnvVar;
    private final String resourceNamespaceEnvVar;
    private final String tlsHttpsPortEnvVar;
    private final Map<String, String> defaultEnv;

    private CloudDescriptor(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.image = Objects.requireNonNull(builder.image, "image");
        if (builder.port <= 0) {
            throw new IllegalArgumentException("port must be positive");
        }
        this.port = builder.port;
        this.envPrefix = Objects.requireNonNull(builder.envPrefix, "envPrefix");
        this.healthPath = Objects.requireNonNull(builder.healthPath, "healthPath");
        this.resetPath = Objects.requireNonNull(builder.resetPath, "resetPath");
        this.logLevelEnvVar = Objects.requireNonNull(builder.logLevelEnvVar, "logLevelEnvVar");
        this.networkEnvVar = Objects.requireNonNull(builder.networkEnvVar, "networkEnvVar");
        this.resourceNamespaceEnvVar = Objects.requireNonNull(builder.resourceNamespaceEnvVar, "resourceNamespaceEnvVar");
        this.tlsHttpsPortEnvVar = Objects.requireNonNull(builder.tlsHttpsPortEnvVar, "tlsHttpsPortEnvVar");
        this.defaultEnv = Collections.unmodifiableMap(new LinkedHashMap<>(builder.defaultEnv));
    }

    /**
     * Returns a new, empty {@link Builder}.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the short name of the cloud (e.g. {@code aws}, {@code az}, {@code gcp}, {@code oci}).
     *
     * @return the cloud name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the official image of the emulator without a tag (e.g. {@code floci/floci-az}). Images passed to the
     * container must be compatible with it.
     *
     * @return the official image name
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the port the emulator serves its API on inside the container.
     *
     * @return the emulator port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the prefix of all of the emulator's configuration environment variables (e.g. {@code FLOCI_AZ_}).
     *
     * @return the environment variable prefix
     */
    public String getEnvPrefix() {
        return envPrefix;
    }

    /**
     * Returns the HTTP path that answers {@code 200} once the emulator is up (e.g. {@code /_floci/health}).
     *
     * @return the health path
     */
    public String getHealthPath() {
        return healthPath;
    }

    /**
     * Returns the HTTP path that resets the emulator's state on {@code POST} (e.g. {@code /_floci/state/reset}).
     *
     * @return the reset path
     */
    public String getResetPath() {
        return resetPath;
    }

    /**
     * Returns the environment variable that sets the log level of the emulator's code.
     *
     * @return the log level environment variable
     */
    public String getLogLevelEnvVar() {
        return logLevelEnvVar;
    }

    /**
     * Returns the environment variable that tells the emulator which Docker network to use for the containers it
     * spawns.
     *
     * @return the Docker network environment variable
     */
    public String getNetworkEnvVar() {
        return networkEnvVar;
    }

    /**
     * Returns the environment variable that sets the namespace the emulator puts into the names and the
     * {@code floci_namespace} label of the containers and volumes it creates.
     *
     * @return the resource namespace environment variable
     */
    public String getResourceNamespaceEnvVar() {
        return resourceNamespaceEnvVar;
    }

    /**
     * Returns the environment variable that sets the additional HTTPS port the emulator binds while TLS is enabled.
     *
     * @return the TLS HTTPS port environment variable
     */
    public String getTlsHttpsPortEnvVar() {
        return tlsHttpsPortEnvVar;
    }

    /**
     * Returns the environment variables every container of this cloud starts with. They are applied before
     * anything else, so a caller's setting or a configuration overrides them.
     *
     * @return the default environment, in insertion order (unmodifiable)
     */
    public Map<String, String> getDefaultEnv() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(defaultEnv));
    }

    @Override
    public String toString() {
        return "CloudDescriptor[" + name + ", " + image + ":" + port + "]";
    }

    /**
     * Builder for {@link CloudDescriptor}. All properties except {@link #defaultEnv(String, String)} are required.
     */
    public static final class Builder {

        private String name;
        private String image;
        private int port;
        private String envPrefix;
        private String healthPath;
        private String resetPath;
        private String logLevelEnvVar;
        private String networkEnvVar;
        private String resourceNamespaceEnvVar;
        private String tlsHttpsPortEnvVar;
        private final Map<String, String> defaultEnv = new LinkedHashMap<>();

        private Builder() {
            // Allow instantiation only via CloudDescriptor.builder()
        }

        /**
         * Sets the short name of the cloud.
         *
         * @param name the cloud name (e.g. {@code az})
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the official image of the emulator, without a tag.
         *
         * @param image the image name (e.g. {@code floci/floci-az})
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets the port the emulator serves its API on.
         *
         * @param port the emulator port
         * @return this builder
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets the prefix of the emulator's configuration environment variables.
         *
         * @param envPrefix the prefix (e.g. {@code FLOCI_AZ_})
         * @return this builder
         */
        public Builder envPrefix(String envPrefix) {
            this.envPrefix = envPrefix;
            return this;
        }

        /**
         * Sets the HTTP path that answers {@code 200} once the emulator is up.
         *
         * @param healthPath the health path
         * @return this builder
         */
        public Builder healthPath(String healthPath) {
            this.healthPath = healthPath;
            return this;
        }

        /**
         * Sets the HTTP path that resets the emulator's state on {@code POST}.
         *
         * @param resetPath the reset path
         * @return this builder
         */
        public Builder resetPath(String resetPath) {
            this.resetPath = resetPath;
            return this;
        }

        /**
         * Sets the environment variable that sets the log level of the emulator's code.
         *
         * @param logLevelEnvVar the environment variable name
         * @return this builder
         */
        public Builder logLevelEnvVar(String logLevelEnvVar) {
            this.logLevelEnvVar = logLevelEnvVar;
            return this;
        }

        /**
         * Sets the environment variable that names the Docker network for spawned containers.
         *
         * @param networkEnvVar the environment variable name
         * @return this builder
         */
        public Builder networkEnvVar(String networkEnvVar) {
            this.networkEnvVar = networkEnvVar;
            return this;
        }

        /**
         * Sets the environment variable that sets the resource namespace of spawned containers and volumes.
         *
         * @param resourceNamespaceEnvVar the environment variable name
         * @return this builder
         */
        public Builder resourceNamespaceEnvVar(String resourceNamespaceEnvVar) {
            this.resourceNamespaceEnvVar = resourceNamespaceEnvVar;
            return this;
        }

        /**
         * Sets the environment variable that sets the additional HTTPS port bound while TLS is enabled.
         *
         * @param tlsHttpsPortEnvVar the environment variable name
         * @return this builder
         */
        public Builder tlsHttpsPortEnvVar(String tlsHttpsPortEnvVar) {
            this.tlsHttpsPortEnvVar = tlsHttpsPortEnvVar;
            return this;
        }

        /**
         * Adds an environment variable every container of this cloud starts with.
         *
         * @param key   the environment variable name
         * @param value the value
         * @return this builder
         */
        public Builder defaultEnv(String key, String value) {
            this.defaultEnv.put(key, value);
            return this;
        }

        /**
         * Creates an immutable {@link CloudDescriptor} from this builder.
         *
         * @return the descriptor
         * @throws NullPointerException     if a required property is missing
         * @throws IllegalArgumentException if the port is not positive
         */
        public CloudDescriptor build() {
            return new CloudDescriptor(this);
        }
    }
}
