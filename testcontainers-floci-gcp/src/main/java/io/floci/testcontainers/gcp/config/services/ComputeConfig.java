package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;
import org.testcontainers.images.builder.Transferable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Configuration for Compute Engine of Floci GCP.
 *
 * <p>Compute Engine (instances, networks, disks and other {@code compute.googleapis.com} resources) is emulated
 * as control plane only by default ({@link Builder#mock(boolean) mock} mode); no virtual machines are started.
 * With {@code mock(false)}, every instance runs as a Docker container started by Floci GCP: startup and shutdown
 * scripts run in it, its serial port output is served, and it can reach the GCE metadata server. This mode needs
 * the Docker socket, which the container then mounts automatically.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ComputeConfig config = ComputeConfig.builder()
 *     .operationDelayMs(0)
 *     .regions(List.of("us-central1", "europe-west3"))
 *     .mock(false)
 *     .startupScriptTimeout(Duration.ofMinutes(5))
 *     .imageCatalog(Files.readString(Path.of("image-catalog.json")))
 *     .build();
 * }</pre>
 */
public class ComputeConfig extends AbstractServiceConfig<ComputeConfig.Builder> {

    private static final long DEFAULT_OPERATION_DELAY_MS = 50;
    private static final boolean DEFAULT_MOCK = true;
    private static final String DEFAULT_DEFAULT_IMAGE = "debian:12";
    private static final boolean DEFAULT_INSTANCE_RESOURCE_LIMITS = true;
    private static final boolean DEFAULT_ALLOW_PRIVILEGED_CONTAINERS = false;
    private static final boolean DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP = true;
    private static final Duration DEFAULT_LAUNCH_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration DEFAULT_STARTUP_SCRIPT_TIMEOUT = Duration.ofMinutes(30);
    private static final Duration DEFAULT_SHUTDOWN_SCRIPT_TIMEOUT = Duration.ofSeconds(90);
    private static final boolean DEFAULT_METADATA_SERVER_ENABLED = true;
    private static final Duration DEFAULT_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT = Duration.ofSeconds(300);
    private static final String IMAGE_CATALOG_FILE_PREFIX = "/tmp/floci-gcp-compute-image-catalog-";
    private static final String IMAGE_CATALOG_FILE_SUFFIX = ".json";

    private final long operationDelayMs;
    private final List<String> regions;
    private final boolean mock;
    private final String defaultImage;
    private final String imageCatalogPath;
    private final String imageCatalog;
    private final String dockerNetwork;
    private final boolean instanceResourceLimits;
    private final boolean allowPrivilegedContainers;
    private final boolean reconcileContainersOnStartup;
    private final Duration launchTimeout;
    private final Duration startupScriptTimeout;
    private final Duration shutdownScriptTimeout;
    private final boolean metadataServerEnabled;
    private final Duration metadataServerDefaultWaitTimeout;

    private ComputeConfig(Builder builder) {
        super(builder);
        this.operationDelayMs = builder.operationDelayMs;
        this.regions = builder.regions == null ? null : List.copyOf(builder.regions);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.imageCatalogPath = builder.imageCatalogPath;
        this.imageCatalog = builder.imageCatalog;
        this.dockerNetwork = builder.dockerNetwork;
        this.instanceResourceLimits = builder.instanceResourceLimits;
        this.allowPrivilegedContainers = builder.allowPrivilegedContainers;
        this.reconcileContainersOnStartup = builder.reconcileContainersOnStartup;
        this.launchTimeout = builder.launchTimeout;
        this.startupScriptTimeout = builder.startupScriptTimeout;
        this.shutdownScriptTimeout = builder.shutdownScriptTimeout;
        this.metadataServerEnabled = builder.metadataServerEnabled;
        this.metadataServerDefaultWaitTimeout = builder.metadataServerDefaultWaitTimeout;
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
     * Returns the time in milliseconds until a long-running Compute Engine operation is reported as
     * {@code DONE}.
     *
     * @return the operation delay in milliseconds
     */
    public long getOperationDelayMs() {
        return operationDelayMs;
    }

    /**
     * Returns the allow-list of regions Floci GCP offers for Compute Engine. Each listed region keeps its real
     * zones from Floci GCP's bundled location catalog; requests for other regions or zones are rejected as not
     * found. When empty, Floci GCP serves all regions of its location catalog.
     *
     * @return the regions, or empty if not set
     */
    public Optional<List<String>> getRegions() {
        return Optional.ofNullable(regions);
    }

    /**
     * Returns whether instances are kept in memory only, without Docker containers.
     *
     * @return {@code true} if instances are not backed by Docker containers
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker image instance containers run for boot images that Floci GCP's image catalog does not
     * map to a Docker image.
     *
     * @return the default Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the path, inside the Floci GCP container, of a JSON file that replaces Floci GCP's bundled
     * boot-image-to-Docker-image catalog.
     *
     * <p>The path is either the one passed to {@link Builder#imageCatalogPath(String)} verbatim, or a generated
     * path pointing at the file whose content was passed to {@link Builder#imageCatalog(String)}.
     *
     * @return the image catalog path, or empty if the bundled catalog is used
     */
    public Optional<String> getImageCatalogPath() {
        return Optional.ofNullable(imageCatalogPath);
    }

    /**
     * Returns the image catalog content supplied via {@link Builder#imageCatalog(String)}, if any. When present,
     * this content is copied into the container at {@link #getImageCatalogPath()}.
     *
     * @return the image catalog content, or empty if the image catalog was not configured by content
     */
    public Optional<String> getImageCatalog() {
        return Optional.ofNullable(imageCatalog);
    }

    /**
     * Returns the Docker network instance containers are attached to. When empty, Floci GCP uses the shared
     * services Docker network.
     *
     * @return the Docker network, or empty if not set
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    /**
     * Returns whether each instance container is limited to its machine type's vCPUs and memory.
     *
     * @return {@code true} if instance containers get resource limits
     */
    public boolean isInstanceResourceLimits() {
        return instanceResourceLimits;
    }

    /**
     * Returns whether {@code securityContext.privileged} of a {@code gce-container-declaration} is honoured.
     *
     * @return {@code true} if declared containers may run privileged on the Docker host
     */
    public boolean isAllowPrivilegedContainers() {
        return allowPrivilegedContainers;
    }

    /**
     * Returns whether Floci GCP reconciles instance containers at startup: it restarts the containers of
     * {@code RUNNING} instances, stops running containers of {@code TERMINATED} instances and removes those of
     * deleted instances.
     *
     * @return {@code true} if instance containers are reconciled on startup
     */
    public boolean isReconcileContainersOnStartup() {
        return reconcileContainersOnStartup;
    }

    /**
     * Returns the longest time an operation waits for its instance container before the instance continues
     * without one.
     *
     * @return the launch timeout
     */
    public Duration getLaunchTimeout() {
        return launchTimeout;
    }

    /**
     * Returns the longest time a startup script may run before it is reported as timed out and left running.
     *
     * @return the startup script timeout
     */
    public Duration getStartupScriptTimeout() {
        return startupScriptTimeout;
    }

    /**
     * Returns the longest time stop and delete wait for the shutdown scripts before stopping the instance
     * container.
     *
     * @return the shutdown script timeout
     */
    public Duration getShutdownScriptTimeout() {
        return shutdownScriptTimeout;
    }

    /**
     * Returns whether Floci GCP serves the GCE metadata server under {@code /computeMetadata/v1} on its main
     * port and points instance containers at it via {@code GCE_METADATA_HOST}.
     *
     * @return {@code true} if the metadata server is enabled
     */
    public boolean isMetadataServerEnabled() {
        return metadataServerEnabled;
    }

    /**
     * Returns the longest time a {@code wait_for_change} request to the metadata server blocks when it names no
     * {@code timeout_sec}.
     *
     * @return the default wait timeout of the metadata server
     */
    public Duration getMetadataServerDefaultWaitTimeout() {
        return metadataServerDefaultWaitTimeout;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", String.valueOf(operationDelayMs));
            if (regions != null) {
                container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", String.join(",", regions));
            }
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_DEFAULT_IMAGE", defaultImage);
            if (imageCatalogPath != null) {
                container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_IMAGE_CATALOG_PATH", imageCatalogPath);
            }
            if (dockerNetwork != null) {
                container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_DOCKER_NETWORK", dockerNetwork);
            }
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_INSTANCE_RESOURCE_LIMITS",
                    String.valueOf(instanceResourceLimits));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_ALLOW_PRIVILEGED_CONTAINERS",
                    String.valueOf(allowPrivilegedContainers));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_RECONCILE_CONTAINERS_ON_STARTUP",
                    String.valueOf(reconcileContainersOnStartup));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_LAUNCH_TIMEOUT", format(launchTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_STARTUP_SCRIPT_TIMEOUT", format(startupScriptTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_SHUTDOWN_SCRIPT_TIMEOUT", format(shutdownScriptTimeout));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_ENABLED",
                    String.valueOf(metadataServerEnabled));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT",
                    format(metadataServerDefaultWaitTimeout));
        }
    }

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && imageCatalogPath != null && imageCatalog != null) {
            container.withCopyToContainer(Transferable.of(imageCatalog), imageCatalogPath);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    private static String format(Duration duration) {
        long millis = duration.toMillis();
        return millis % 1000 == 0 ? (millis / 1000) + "s" : millis + "ms";
    }

    /**
     * Builder for {@link ComputeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ComputeConfig> {

        private long operationDelayMs = DEFAULT_OPERATION_DELAY_MS;
        private List<String> regions;
        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private String imageCatalogPath;
        private String imageCatalog;
        private String dockerNetwork;
        private boolean instanceResourceLimits = DEFAULT_INSTANCE_RESOURCE_LIMITS;
        private boolean allowPrivilegedContainers = DEFAULT_ALLOW_PRIVILEGED_CONTAINERS;
        private boolean reconcileContainersOnStartup = DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP;
        private Duration launchTimeout = DEFAULT_LAUNCH_TIMEOUT;
        private Duration startupScriptTimeout = DEFAULT_STARTUP_SCRIPT_TIMEOUT;
        private Duration shutdownScriptTimeout = DEFAULT_SHUTDOWN_SCRIPT_TIMEOUT;
        private boolean metadataServerEnabled = DEFAULT_METADATA_SERVER_ENABLED;
        private Duration metadataServerDefaultWaitTimeout = DEFAULT_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT;

        private Builder() {
            // Allow instantiation only via ComputeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ComputeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ComputeConfig instance) {
            super(instance);
            this.operationDelayMs = instance.operationDelayMs;
            this.regions = instance.regions;
            this.mock = instance.mock;
            this.defaultImage = instance.defaultImage;
            this.imageCatalogPath = instance.imageCatalogPath;
            this.imageCatalog = instance.imageCatalog;
            this.dockerNetwork = instance.dockerNetwork;
            this.instanceResourceLimits = instance.instanceResourceLimits;
            this.allowPrivilegedContainers = instance.allowPrivilegedContainers;
            this.reconcileContainersOnStartup = instance.reconcileContainersOnStartup;
            this.launchTimeout = instance.launchTimeout;
            this.startupScriptTimeout = instance.startupScriptTimeout;
            this.shutdownScriptTimeout = instance.shutdownScriptTimeout;
            this.metadataServerEnabled = instance.metadataServerEnabled;
            this.metadataServerDefaultWaitTimeout = instance.metadataServerDefaultWaitTimeout;
        }

        /**
         * Sets the time in milliseconds until a long-running Compute Engine operation is reported as
         * {@code DONE}. Floci GCP uses at least 2 ms.
         *
         * @param operationDelayMs the operation delay in milliseconds (default {@value DEFAULT_OPERATION_DELAY_MS})
         * @return this builder
         */
        public Builder operationDelayMs(long operationDelayMs) {
            this.operationDelayMs = operationDelayMs;
            return this;
        }

        /**
         * Sets an allow-list of regions Floci GCP offers for Compute Engine. Each listed region keeps its real
         * zones from Floci GCP's bundled location catalog; requests for other regions or zones are rejected as
         * not found. Regions unknown to the catalog are ignored by Floci GCP. When not set (or empty), Floci GCP
         * serves all regions of its location catalog.
         *
         * @param regions the regions, or {@code null} to serve the whole location catalog (default)
         * @return this builder
         */
        public Builder regions(List<String> regions) {
            this.regions = regions;
            return this;
        }

        /**
         * Sets whether instances are kept in memory only, without Docker containers. With {@code false}, every
         * instance runs as a Docker container (pulled from the boot image's Docker image) that is started,
         * stopped and removed along with the instance; this needs the Docker socket, which the container then
         * mounts automatically. Instance containers are opt-in in Floci GCP while the runtime matures.
         *
         * @param mock {@code false} to back instances with Docker containers (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Docker image instance containers run for boot images that Floci GCP's image catalog does not
         * map to a Docker image. Only used while {@link #mock(boolean) mock} is off.
         *
         * @param defaultImage the default Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the path, inside the Floci GCP container, of a JSON file that replaces Floci GCP's bundled
         * boot-image-to-Docker-image catalog (same format as Floci GCP's {@code compute/image-catalog.json}) and
         * that already exists in the container (for example one added through a volume or another
         * {@code withCopy*} call).
         *
         * <p>Use {@link #imageCatalog(String)} instead to hand over just the file content and let
         * {@link ComputeConfig} take care of placing the file into the container.
         *
         * <p>Calling this method clears any content previously set via {@link #imageCatalog(String)}.
         *
         * @param imageCatalogPath the image catalog path, or {@code null} to use the bundled catalog (default)
         * @return this builder
         */
        public Builder imageCatalogPath(String imageCatalogPath) {
            this.imageCatalogPath = imageCatalogPath;
            this.imageCatalog = null;
            return this;
        }

        /**
         * Sets the content of a JSON file that replaces Floci GCP's bundled boot-image-to-Docker-image catalog
         * (same format as Floci GCP's {@code compute/image-catalog.json}), e.g.
         * {@code {"images": [{"family": "golden", "prefixes": ["golden-"], "dockerImage": "registry.local/golden:1"}]}}.
         *
         * <p>The content is copied into the container under a generated, randomized path, which is then used as
         * {@link ComputeConfig#getImageCatalogPath()}.
         *
         * <p>Calling this method clears any path previously set via {@link #imageCatalogPath(String)}.
         *
         * @param imageCatalog the image catalog content, or {@code null} to use the bundled catalog (default)
         * @return this builder
         */
        public Builder imageCatalog(String imageCatalog) {
            this.imageCatalog = imageCatalog;
            this.imageCatalogPath = null;
            return this;
        }

        /**
         * Sets the Docker network instance containers are attached to. When not set, Floci GCP uses the shared
         * services Docker network (see {@code FlociGcpContainer#withDedicatedNetwork()}).
         *
         * @param dockerNetwork the Docker network, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets whether each instance container (and the container a {@code gce-container-declaration} declares)
         * is limited to its machine type's vCPUs and memory.
         *
         * @param instanceResourceLimits {@code false} to run instance containers without resource limits
         *                               (default {@value DEFAULT_INSTANCE_RESOURCE_LIMITS})
         * @return this builder
         */
        public Builder instanceResourceLimits(boolean instanceResourceLimits) {
            this.instanceResourceLimits = instanceResourceLimits;
            return this;
        }

        /**
         * Sets whether {@code securityContext.privileged} of a {@code gce-container-declaration} is honoured. Off
         * by default, because the declared container would be privileged on the Docker host, not only inside the
         * emulated VM.
         *
         * @param allowPrivilegedContainers {@code true} to run declared privileged containers privileged
         *                                  (default {@value DEFAULT_ALLOW_PRIVILEGED_CONTAINERS})
         * @return this builder
         */
        public Builder allowPrivilegedContainers(boolean allowPrivilegedContainers) {
            this.allowPrivilegedContainers = allowPrivilegedContainers;
            return this;
        }

        /**
         * Sets whether Floci GCP reconciles instance containers at startup: it restarts the containers of
         * {@code RUNNING} instances, stops running containers of {@code TERMINATED} instances and removes those of
         * deleted instances.
         *
         * @param reconcileContainersOnStartup {@code false} to leave existing instance containers alone
         *                                     (default {@value DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP})
         * @return this builder
         */
        public Builder reconcileContainersOnStartup(boolean reconcileContainersOnStartup) {
            this.reconcileContainersOnStartup = reconcileContainersOnStartup;
            return this;
        }

        /**
         * Sets the longest time an operation waits for its instance container (image pull and start) before the
         * instance continues without one.
         *
         * @param launchTimeout the launch timeout (default 5 minutes)
         * @return this builder
         */
        public Builder launchTimeout(Duration launchTimeout) {
            this.launchTimeout = launchTimeout;
            return this;
        }

        /**
         * Sets the longest time a startup script (and each part of cloud-init) may run before it is reported as
         * timed out and left running.
         *
         * @param startupScriptTimeout the startup script timeout (default 30 minutes)
         * @return this builder
         */
        public Builder startupScriptTimeout(Duration startupScriptTimeout) {
            this.startupScriptTimeout = startupScriptTimeout;
            return this;
        }

        /**
         * Sets the longest time stop and delete wait for the shutdown scripts before stopping the instance
         * container.
         *
         * @param shutdownScriptTimeout the shutdown script timeout (default 90 seconds)
         * @return this builder
         */
        public Builder shutdownScriptTimeout(Duration shutdownScriptTimeout) {
            this.shutdownScriptTimeout = shutdownScriptTimeout;
            return this;
        }

        /**
         * Sets whether Floci GCP serves the GCE metadata server under {@code /computeMetadata/v1} on its main
         * port and points instance containers at it via {@code GCE_METADATA_HOST}.
         *
         * @param metadataServerEnabled {@code false} to disable the metadata server
         *                              (default {@value DEFAULT_METADATA_SERVER_ENABLED})
         * @return this builder
         */
        public Builder metadataServerEnabled(boolean metadataServerEnabled) {
            this.metadataServerEnabled = metadataServerEnabled;
            return this;
        }

        /**
         * Sets the longest time a {@code wait_for_change} request to the metadata server blocks when it names no
         * {@code timeout_sec}.
         *
         * @param metadataServerDefaultWaitTimeout the default wait timeout (default 300 seconds)
         * @return this builder
         */
        public Builder metadataServerDefaultWaitTimeout(Duration metadataServerDefaultWaitTimeout) {
            this.metadataServerDefaultWaitTimeout = metadataServerDefaultWaitTimeout;
            return this;
        }

        /**
         * Creates an immutable {@link ComputeConfig} from this builder.
         *
         * @return the Compute Engine configuration
         */
        @Override
        public ComputeConfig build() {
            if (imageCatalog != null && imageCatalogPath == null) {
                this.imageCatalogPath = IMAGE_CATALOG_FILE_PREFIX + UUID.randomUUID() + IMAGE_CATALOG_FILE_SUFFIX;
            }
            return new ComputeConfig(this);
        }
    }
}
