package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;
import java.util.List;

/**
 * Configuration for ECS-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EcsConfig config = EcsConfig.builder()
 *     .enabled(true)
 *     .mock(true)
 *     .defaultMemoryMb(512)
 *     .build();
 * }</pre>
 */
public class EcsConfig extends AbstractServiceConfig<EcsConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_MEMORY_MB = 512;
    private static final int DEFAULT_CPU_UNITS = 256;
    private static final boolean DEFAULT_PUBLISH_AWSVPC_PORTS_TO_HOST = false;
    private static final boolean DEFAULT_ALLOW_UNSAFE_HOST_VOLUMES = false;
    private static final boolean DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP = true;
    private static final String DEFAULT_IMAGE_PULL_BEHAVIOR = "default";
    private static final boolean DEFAULT_TASK_ROLE_CREDENTIALS_ENABLED = false;
    private static final long DEFAULT_TASK_ROLE_CREDENTIALS_TTL_SECONDS = 21600L;
    private static final int DEFAULT_TASK_ROLE_CREDENTIALS_PORT = 51679;
    private static final String DEFAULT_TASK_ROLE_CREDENTIALS_PROXY_IMAGE = "floci/network-helper:local";

    private final boolean mock;
    private final String dockerNetwork;
    private final int defaultMemoryMb;
    private final int defaultCpuUnits;
    private final boolean publishAwsvpcPortsToHost;
    private final List<String> hostVolumeRoots;
    private final boolean allowUnsafeHostVolumes;
    private final boolean reconcileContainersOnStartup;
    private final String imagePullBehavior;
    private final boolean taskRoleCredentialsEnabled;
    private final long taskRoleCredentialsTtlSeconds;
    private final int taskRoleCredentialsPort;
    private final String taskRoleCredentialsProxyImage;

    private EcsConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.dockerNetwork = builder.dockerNetwork;
        this.defaultMemoryMb = builder.defaultMemoryMb;
        this.defaultCpuUnits = builder.defaultCpuUnits;
        this.publishAwsvpcPortsToHost = builder.publishAwsvpcPortsToHost;
        this.hostVolumeRoots = builder.hostVolumeRoots;
        this.allowUnsafeHostVolumes = builder.allowUnsafeHostVolumes;
        this.reconcileContainersOnStartup = builder.reconcileContainersOnStartup;
        this.imagePullBehavior = builder.imagePullBehavior;
        this.taskRoleCredentialsEnabled = builder.taskRoleCredentialsEnabled;
        this.taskRoleCredentialsTtlSeconds = builder.taskRoleCredentialsTtlSeconds;
        this.taskRoleCredentialsPort = builder.taskRoleCredentialsPort;
        this.taskRoleCredentialsProxyImage = builder.taskRoleCredentialsProxyImage;
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
     * Returns whether ECS tasks go straight to RUNNING without starting real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the Docker network used for ECS task containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    /**
     * Returns the default memory size for ECS tasks in megabytes.
     *
     * @return memory in MB
     */
    public int getDefaultMemoryMb() {
        return defaultMemoryMb;
    }

    /**
     * Returns the default CPU units for ECS tasks.
     *
     * @return CPU units
     */
    public int getDefaultCpuUnits() {
        return defaultCpuUnits;
    }

    /**
     * Returns whether {@code awsvpc} task ports are published on the Docker host so local host processes can
     * reach them.
     *
     * <p>An emulator-only escape hatch that can cause port collisions when more than one task exposes the
     * same port.
     *
     * @return whether {@code awsvpc} task ports are published on the Docker host so local host processes can reach them
     */
    public boolean isPublishAwsvpcPortsToHost() {
        return publishAwsvpcPortsToHost;
    }

    /**
     * Returns the approved parent directories for task definition host volume bind mounts
     * ({@code volumes[].host.sourcePath}).
     *
     * <p>A {@code sourcePath} must resolve under one of these roots. Unset rejects every host volume
     * {@code sourcePath} unless unsafe host volumes are allowed. Traversal segments, the bare root and the
     * Docker socket are always rejected.
     *
     * @return the approved parent directories for task definition host volume bind mounts ({@code volumes[].host.sourcePath}), or {@link Optional#empty()} if not configured
     */
    public Optional<List<String>> getHostVolumeRoots() {
        return Optional.ofNullable(hostVolumeRoots);
    }

    /**
     * Returns whether the host volume roots allowlist check is bypassed, allowing any absolute host volume
     * path.
     *
     * <p>Traversal segments, the bare root {@code /} and the Docker socket (or any ancestor directory
     * containing it) are still always rejected.
     *
     * @return whether the host volume roots allowlist check is bypassed, allowing any absolute host volume path
     */
    public boolean isAllowUnsafeHostVolumes() {
        return allowUnsafeHostVolumes;
    }

    /**
     * Returns whether Floci removes, on startup, every ECS container a previous run of the same Floci left on
     * the Docker daemon.
     *
     * <p>Turn it off when two Floci instances share a daemon with the same port and no resource namespace, so
     * one does not remove the other's containers.
     *
     * @return whether Floci removes, on startup, every ECS container a previous run of the same Floci left on the Docker daemon
     */
    public boolean isReconcileContainersOnStartup() {
        return reconcileContainersOnStartup;
    }

    /**
     * Returns how a task's container images are pulled when the task starts.
     *
     * <p>Same values and semantics as the ECS agent's {@code ECS_IMAGE_PULL_BEHAVIOR}: {@code default} (pull
     * on every launch, fall back to the cached image if the pull fails), {@code always} (pull on every
     * launch, fail the task if the pull fails), {@code once} (pull once per Floci run) or
     * {@code prefer-cached} (pull only when there is no cached image).
     *
     * @return how a task's container images are pulled when the task starts
     */
    public String getImagePullBehavior() {
        return imagePullBehavior;
    }

    /**
     * Returns whether real task-role credentials are vended to tasks over the AWS container-credentials wire
     * contract.
     *
     * <p>Reaching the credentials endpoint from a task container needs a user-defined Docker network that
     * both the task and the credentials proxy join, so set the ECS Docker network alongside this.
     *
     * @return whether real task-role credentials are vended to tasks over the AWS container-credentials wire contract
     */
    public boolean isTaskRoleCredentialsEnabled() {
        return taskRoleCredentialsEnabled;
    }

    /**
     * Returns the lifetime, in seconds, of the vended task-role credentials.
     *
     * @return the lifetime, in seconds, of the vended task-role credentials
     */
    public long getTaskRoleCredentialsTtlSeconds() {
        return taskRoleCredentialsTtlSeconds;
    }

    /**
     * Returns the port on the Floci host serving the task-role credentials endpoint.
     *
     * <p>Task containers do not talk to it directly: a small proxy container holds {@code 169.254.170.2} on
     * each task network and forwards to this port.
     *
     * @return the port on the Floci host serving the task-role credentials endpoint
     */
    public int getTaskRoleCredentialsPort() {
        return taskRoleCredentialsPort;
    }

    /**
     * Returns the image of the per-network task-role credentials proxy container.
     *
     * @return the image of the per-network task-role credentials proxy container
     */
    public String getTaskRoleCredentialsProxyImage() {
        return taskRoleCredentialsProxyImage;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ECS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ECS_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_ECS_DEFAULT_MEMORY_MB", String.valueOf(defaultMemoryMb));
            container.withEnv("FLOCI_SERVICES_ECS_DEFAULT_CPU_UNITS", String.valueOf(defaultCpuUnits));

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_ECS_DOCKER_NETWORK", dockerNetwork);
            }

            container.withEnv("FLOCI_SERVICES_ECS_PUBLISH_AWSVPC_PORTS_TO_HOST", String.valueOf(publishAwsvpcPortsToHost));

            if (hostVolumeRoots != null && !hostVolumeRoots.isEmpty()) {
                container.withEnv("FLOCI_SERVICES_ECS_HOST_VOLUME_ROOTS", String.join(",", hostVolumeRoots));
            }

            container.withEnv("FLOCI_SERVICES_ECS_ALLOW_UNSAFE_HOST_VOLUMES", String.valueOf(allowUnsafeHostVolumes));
            container.withEnv("FLOCI_SERVICES_ECS_RECONCILE_CONTAINERS_ON_STARTUP", String.valueOf(reconcileContainersOnStartup));
            container.withEnv("FLOCI_SERVICES_ECS_IMAGE_PULL_BEHAVIOR", imagePullBehavior);
            container.withEnv("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_ENABLED", String.valueOf(taskRoleCredentialsEnabled));
            container.withEnv("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_TTL_SECONDS", String.valueOf(taskRoleCredentialsTtlSeconds));
            container.withEnv("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PORT", String.valueOf(taskRoleCredentialsPort));
            container.withEnv("FLOCI_SERVICES_ECS_TASK_ROLE_CREDENTIALS_PROXY_IMAGE", taskRoleCredentialsProxyImage);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link EcsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EcsConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String dockerNetwork;
        private int defaultMemoryMb = DEFAULT_MEMORY_MB;
        private int defaultCpuUnits = DEFAULT_CPU_UNITS;
        private boolean publishAwsvpcPortsToHost = DEFAULT_PUBLISH_AWSVPC_PORTS_TO_HOST;
        private List<String> hostVolumeRoots;
        private boolean allowUnsafeHostVolumes = DEFAULT_ALLOW_UNSAFE_HOST_VOLUMES;
        private boolean reconcileContainersOnStartup = DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP;
        private String imagePullBehavior = DEFAULT_IMAGE_PULL_BEHAVIOR;
        private boolean taskRoleCredentialsEnabled = DEFAULT_TASK_ROLE_CREDENTIALS_ENABLED;
        private long taskRoleCredentialsTtlSeconds = DEFAULT_TASK_ROLE_CREDENTIALS_TTL_SECONDS;
        private int taskRoleCredentialsPort = DEFAULT_TASK_ROLE_CREDENTIALS_PORT;
        private String taskRoleCredentialsProxyImage = DEFAULT_TASK_ROLE_CREDENTIALS_PROXY_IMAGE;

        private Builder() {
            // Allow instantiation only via EcsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EcsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EcsConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.dockerNetwork = instance.getDockerNetwork();
            this.defaultMemoryMb = instance.getDefaultMemoryMb();
            this.defaultCpuUnits = instance.getDefaultCpuUnits();
            this.publishAwsvpcPortsToHost = instance.isPublishAwsvpcPortsToHost();
            this.hostVolumeRoots = instance.getHostVolumeRoots().orElse(null);
            this.allowUnsafeHostVolumes = instance.isAllowUnsafeHostVolumes();
            this.reconcileContainersOnStartup = instance.isReconcileContainersOnStartup();
            this.imagePullBehavior = instance.getImagePullBehavior();
            this.taskRoleCredentialsEnabled = instance.isTaskRoleCredentialsEnabled();
            this.taskRoleCredentialsTtlSeconds = instance.getTaskRoleCredentialsTtlSeconds();
            this.taskRoleCredentialsPort = instance.getTaskRoleCredentialsPort();
            this.taskRoleCredentialsProxyImage = instance.getTaskRoleCredentialsProxyImage();
        }

        /**
         * Sets whether ECS tasks go straight to RUNNING without starting real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the Docker network that ECS task containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the default memory size for ECS tasks in megabytes.
         *
         * @param defaultMemoryMb memory in MB (default {@value DEFAULT_MEMORY_MB})
         * @return this builder
         */
        public Builder defaultMemoryMb(int defaultMemoryMb) {
            this.defaultMemoryMb = defaultMemoryMb;
            return this;
        }

        /**
         * Sets the default CPU units for ECS tasks.
         *
         * @param defaultCpuUnits CPU units (default {@value DEFAULT_CPU_UNITS})
         * @return this builder
         */
        public Builder defaultCpuUnits(int defaultCpuUnits) {
            this.defaultCpuUnits = defaultCpuUnits;
            return this;
        }

        /**
         * Sets whether {@code awsvpc} task ports are published on the Docker host so local host processes can
         * reach them.
         *
         * <p>An emulator-only escape hatch that can cause port collisions when more than one task exposes the
         * same port.
         *
         * @param publishAwsvpcPortsToHost whether {@code awsvpc} task ports are published on the Docker host so local host processes can reach them (default {@value DEFAULT_PUBLISH_AWSVPC_PORTS_TO_HOST})
         * @return this builder
         */
        public Builder publishAwsvpcPortsToHost(boolean publishAwsvpcPortsToHost) {
            this.publishAwsvpcPortsToHost = publishAwsvpcPortsToHost;
            return this;
        }

        /**
         * Sets the approved parent directories for task definition host volume bind mounts
         * ({@code volumes[].host.sourcePath}).
         *
         * <p>A {@code sourcePath} must resolve under one of these roots. Unset rejects every host volume
         * {@code sourcePath} unless unsafe host volumes are allowed. Traversal segments, the bare root and
         * the Docker socket are always rejected.
         *
         * @param hostVolumeRoots the approved parent directories for task definition host volume bind mounts ({@code volumes[].host.sourcePath}), or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder hostVolumeRoots(List<String> hostVolumeRoots) {
            this.hostVolumeRoots = hostVolumeRoots;
            return this;
        }

        /**
         * Sets whether the host volume roots allowlist check is bypassed, allowing any absolute host volume
         * path.
         *
         * <p>Traversal segments, the bare root {@code /} and the Docker socket (or any ancestor directory
         * containing it) are still always rejected.
         *
         * @param allowUnsafeHostVolumes whether the host volume roots allowlist check is bypassed, allowing any absolute host volume path (default {@value DEFAULT_ALLOW_UNSAFE_HOST_VOLUMES})
         * @return this builder
         */
        public Builder allowUnsafeHostVolumes(boolean allowUnsafeHostVolumes) {
            this.allowUnsafeHostVolumes = allowUnsafeHostVolumes;
            return this;
        }

        /**
         * Sets whether Floci removes, on startup, every ECS container a previous run of the same Floci left
         * on the Docker daemon.
         *
         * <p>Turn it off when two Floci instances share a daemon with the same port and no resource
         * namespace, so one does not remove the other's containers.
         *
         * @param reconcileContainersOnStartup whether Floci removes, on startup, every ECS container a previous run of the same Floci left on the Docker daemon (default {@value DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP})
         * @return this builder
         */
        public Builder reconcileContainersOnStartup(boolean reconcileContainersOnStartup) {
            this.reconcileContainersOnStartup = reconcileContainersOnStartup;
            return this;
        }

        /**
         * Sets how a task's container images are pulled when the task starts.
         *
         * <p>Same values and semantics as the ECS agent's {@code ECS_IMAGE_PULL_BEHAVIOR}: {@code default}
         * (pull on every launch, fall back to the cached image if the pull fails), {@code always} (pull on
         * every launch, fail the task if the pull fails), {@code once} (pull once per Floci run) or
         * {@code prefer-cached} (pull only when there is no cached image).
         *
         * @param imagePullBehavior how a task's container images are pulled when the task starts (default {@value DEFAULT_IMAGE_PULL_BEHAVIOR})
         * @return this builder
         */
        public Builder imagePullBehavior(String imagePullBehavior) {
            this.imagePullBehavior = imagePullBehavior;
            return this;
        }

        /**
         * Sets whether real task-role credentials are vended to tasks over the AWS container-credentials wire
         * contract.
         *
         * <p>Reaching the credentials endpoint from a task container needs a user-defined Docker network that
         * both the task and the credentials proxy join, so set the ECS Docker network alongside this.
         *
         * @param taskRoleCredentialsEnabled whether real task-role credentials are vended to tasks over the AWS container-credentials wire contract (default {@value DEFAULT_TASK_ROLE_CREDENTIALS_ENABLED})
         * @return this builder
         */
        public Builder taskRoleCredentialsEnabled(boolean taskRoleCredentialsEnabled) {
            this.taskRoleCredentialsEnabled = taskRoleCredentialsEnabled;
            return this;
        }

        /**
         * Sets the lifetime, in seconds, of the vended task-role credentials.
         *
         * @param taskRoleCredentialsTtlSeconds the lifetime, in seconds, of the vended task-role credentials (default {@value DEFAULT_TASK_ROLE_CREDENTIALS_TTL_SECONDS})
         * @return this builder
         */
        public Builder taskRoleCredentialsTtlSeconds(long taskRoleCredentialsTtlSeconds) {
            this.taskRoleCredentialsTtlSeconds = taskRoleCredentialsTtlSeconds;
            return this;
        }

        /**
         * Sets the port on the Floci host serving the task-role credentials endpoint.
         *
         * <p>Task containers do not talk to it directly: a small proxy container holds {@code 169.254.170.2}
         * on each task network and forwards to this port.
         *
         * @param taskRoleCredentialsPort the port on the Floci host serving the task-role credentials endpoint (default {@value DEFAULT_TASK_ROLE_CREDENTIALS_PORT})
         * @return this builder
         */
        public Builder taskRoleCredentialsPort(int taskRoleCredentialsPort) {
            this.taskRoleCredentialsPort = taskRoleCredentialsPort;
            return this;
        }

        /**
         * Sets the image of the per-network task-role credentials proxy container.
         *
         * @param taskRoleCredentialsProxyImage the image of the per-network task-role credentials proxy container (default {@value DEFAULT_TASK_ROLE_CREDENTIALS_PROXY_IMAGE})
         * @return this builder
         */
        public Builder taskRoleCredentialsProxyImage(String taskRoleCredentialsProxyImage) {
            this.taskRoleCredentialsProxyImage = taskRoleCredentialsProxyImage;
            return this;
        }

        /**
         * Creates an immutable {@link EcsConfig} from this builder.
         *
         * @return the ECS configuration
         */
        @Override
        public EcsConfig build() {
            return new EcsConfig(this);
        }
    }
}
