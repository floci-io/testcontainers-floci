package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for EMR (Elastic MapReduce)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EmrConfig config = EmrConfig.builder()
 *     .enabled(true)
 *     .defaultReleaseLabel("emr-7.8.0")
 *     .clusterStartupDelaySeconds(5)
 *     .build();
 * }</pre>
 */
public class EmrConfig extends AbstractServiceConfig<EmrConfig.Builder> {

    private static final String DEFAULT_RELEASE_LABEL = "emr-7.5.0";
    private static final int DEFAULT_CLUSTER_STARTUP_DELAY_SECONDS = 0;
    private static final int DEFAULT_STEP_DURATION_SECONDS = 0;
    private static final int DEFAULT_CLUSTER_TERMINATION_DELAY_SECONDS = 0;
    private static final String DEFAULT_RUNNER_MODE = "immediate";
    private static final String DOCKER_RUNNER_MODE = "docker";
    private static final int DEFAULT_YARN_NODE_MEMORY_MB = 4096;
    private static final int DEFAULT_YARN_NODE_VCORES = 4;
    private static final int DEFAULT_YARN_MAX_WORKER_NODES = 4;
    private static final boolean DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP = true;

    private final String defaultReleaseLabel;
    private final int clusterStartupDelaySeconds;
    private final int stepDurationSeconds;
    private final int clusterTerminationDelaySeconds;
    private final String runnerMode;
    private final String dockerNetwork;
    private final String imageTemplate;
    private final String hiveImageTemplate;
    private final int yarnNodeMemoryMb;
    private final int yarnNodeVcores;
    private final int yarnMaxWorkerNodes;
    private final boolean reconcileContainersOnStartup;

    private EmrConfig(Builder builder) {
        super(builder);
        this.defaultReleaseLabel = builder.defaultReleaseLabel;
        this.clusterStartupDelaySeconds = builder.clusterStartupDelaySeconds;
        this.stepDurationSeconds = builder.stepDurationSeconds;
        this.clusterTerminationDelaySeconds = builder.clusterTerminationDelaySeconds;
        this.runnerMode = builder.runnerMode;
        this.dockerNetwork = builder.dockerNetwork;
        this.imageTemplate = builder.imageTemplate;
        this.hiveImageTemplate = builder.hiveImageTemplate;
        this.yarnNodeMemoryMb = builder.yarnNodeMemoryMb;
        this.yarnNodeVcores = builder.yarnNodeVcores;
        this.yarnMaxWorkerNodes = builder.yarnMaxWorkerNodes;
        this.reconcileContainersOnStartup = builder.reconcileContainersOnStartup;
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
     * Returns the default EMR release label used when creating clusters.
     *
     * @return the release label
     */
    public String getDefaultReleaseLabel() {
        return defaultReleaseLabel;
    }

    /**
     * Returns how long, in seconds, a new cluster takes to be ready to run steps, the first half
     * {@code STARTING} and the second {@code BOOTSTRAPPING}. A value of {@code 0} makes it ready as soon as
     * it is created.
     *
     * @return the cluster startup delay in seconds
     */
    public int getClusterStartupDelaySeconds() {
        return clusterStartupDelaySeconds;
    }

    /**
     * Returns how long, in seconds, a step stays {@code RUNNING} before it completes.
     *
     * <p>{@code 0} completes it as soon as it starts.
     *
     * @return how long, in seconds, a step stays {@code RUNNING} before it completes
     */
    public int getStepDurationSeconds() {
        return stepDurationSeconds;
    }

    /**
     * Returns how long, in seconds, a cluster stays {@code TERMINATING} before it is {@code TERMINATED}.
     *
     * <p>{@code 0} ends it at once.
     *
     * @return how long, in seconds, a cluster stays {@code TERMINATING} before it is {@code TERMINATED}
     */
    public int getClusterTerminationDelaySeconds() {
        return clusterTerminationDelaySeconds;
    }

    /**
     * Returns the runner mode of EMR steps.
     *
     * <p>{@code immediate} completes each step by time without running anything; {@code docker} runs each Spark step's {@code spark-submit} (and, on a cluster with Hive, each Hive step's {@code hive}) in a container of the release's EMR Serverless image, which needs the Docker socket.
     *
     * @return the runner mode of EMR steps
     */
    public String getRunnerMode() {
        return runnerMode;
    }

    /**
     * Returns the Docker network the containers of the {@code docker} runner mode join.
     *
     * @return the Docker network the containers of the {@code docker} runner mode join, or empty to keep Floci's default
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    /**
     * Returns the image a {@code docker} step uses, with {@code %s} standing for the cluster's release label.
     *
     * <p>For example {@code registry.internal/emr-serverless/spark/%s:20250425}. If unset, Floci uses the release's public EMR Serverless image on Amazon ECR Public. Every cluster also runs its HDFS in the Hive image template's image, so a mirror names both.
     *
     * @return the image a {@code docker} step uses, with {@code %s} standing for the cluster's release label, or empty to keep Floci's default
     */
    public Optional<String> getImageTemplate() {
        return Optional.ofNullable(imageTemplate);
    }

    /**
     * Returns the EMR Serverless Hive image, with {@code %s} standing for the release label, every {@code docker} cluster runs its HDFS in and a cluster with Hive takes Hive from.
     *
     * <p>For example {@code registry.internal/emr-serverless/hive/%s:20250425}. If unset, Floci uses the release's public EMR Serverless Hive image on Amazon ECR Public.
     *
     * @return the EMR Serverless Hive image, with {@code %s} standing for the release label, every {@code docker} cluster runs its HDFS in and a cluster with Hive takes Hive from, or empty to keep Floci's default
     */
    public Optional<String> getHiveImageTemplate() {
        return Optional.ofNullable(hiveImageTemplate);
    }

    /**
     * Returns the memory, in MB, a {@code docker} cluster's NodeManager offers the YARN containers its steps' Spark executors run in.
     *
     * @return the memory, in MB, a {@code docker} cluster's NodeManager offers the YARN containers its steps' Spark executors run in
     */
    public int getYarnNodeMemoryMb() {
        return yarnNodeMemoryMb;
    }

    /**
     * Returns the virtual cores a {@code docker} cluster's NodeManager offers YARN's containers.
     *
     * @return the virtual cores a {@code docker} cluster's NodeManager offers YARN's containers
     */
    public int getYarnNodeVcores() {
        return yarnNodeVcores;
    }

    /**
     * Returns the most core and task nodes a {@code docker} cluster runs as containers of their own, core nodes first.
     *
     * <p>Each costs 300 to 800 MB while idle. With {@code 0} the primary node holds its DataNode and NodeManager, as a cluster of one node does.
     *
     * @return the most core and task nodes a {@code docker} cluster runs as containers of their own, core nodes first
     */
    public int getYarnMaxWorkerNodes() {
        return yarnMaxWorkerNodes;
    }

    /**
     * Returns whether Floci removes, in the {@code docker} runner mode, the EMR step driver and bootstrap containers a previous run of the same Floci left on the Docker daemon when the EMR service starts.
     *
     * <p>Turn it off when two Floci instances share a daemon with the same port and no resource namespace.
     *
     * @return whether Floci removes, in the {@code docker} runner mode, the EMR step driver and bootstrap containers a previous run of the same Floci left on the Docker daemon when the EMR service starts
     */
    public boolean isReconcileContainersOnStartup() {
        return reconcileContainersOnStartup;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_EMR_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_EMR_DEFAULT_RELEASE_LABEL", defaultReleaseLabel);
            container.withEnv("FLOCI_SERVICES_EMR_CLUSTER_STARTUP_DELAY_SECONDS", String.valueOf(clusterStartupDelaySeconds));
            container.withEnv("FLOCI_SERVICES_EMR_STEP_DURATION_SECONDS", String.valueOf(stepDurationSeconds));
            container.withEnv("FLOCI_SERVICES_EMR_CLUSTER_TERMINATION_DELAY_SECONDS", String.valueOf(clusterTerminationDelaySeconds));
            container.withEnv("FLOCI_SERVICES_EMR_RUNNER_MODE", runnerMode);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_EMR_DOCKER_NETWORK", dockerNetwork);
            }

            if (imageTemplate != null) {
                container.withEnv("FLOCI_SERVICES_EMR_IMAGE_TEMPLATE", imageTemplate);
            }

            if (hiveImageTemplate != null) {
                container.withEnv("FLOCI_SERVICES_EMR_HIVE_IMAGE_TEMPLATE", hiveImageTemplate);
            }

            container.withEnv("FLOCI_SERVICES_EMR_YARN_NODE_MEMORY_MB", String.valueOf(yarnNodeMemoryMb));
            container.withEnv("FLOCI_SERVICES_EMR_YARN_NODE_VCORES", String.valueOf(yarnNodeVcores));
            container.withEnv("FLOCI_SERVICES_EMR_YARN_MAX_WORKER_NODES", String.valueOf(yarnMaxWorkerNodes));
            container.withEnv("FLOCI_SERVICES_EMR_RECONCILE_CONTAINERS_ON_STARTUP", String.valueOf(reconcileContainersOnStartup));
        }
    }

    /**
     * Returns {@code true} while enabled with the {@code docker} runner mode, the only mode that runs steps in
     * containers.
     */
    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && DOCKER_RUNNER_MODE.equalsIgnoreCase(runnerMode);
    }

    /**
     * Builder for {@link EmrConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EmrConfig> {

        private String defaultReleaseLabel = DEFAULT_RELEASE_LABEL;
        private int clusterStartupDelaySeconds = DEFAULT_CLUSTER_STARTUP_DELAY_SECONDS;
        private int stepDurationSeconds = DEFAULT_STEP_DURATION_SECONDS;
        private int clusterTerminationDelaySeconds = DEFAULT_CLUSTER_TERMINATION_DELAY_SECONDS;
        private String runnerMode = DEFAULT_RUNNER_MODE;
        private String dockerNetwork;
        private String imageTemplate;
        private String hiveImageTemplate;
        private int yarnNodeMemoryMb = DEFAULT_YARN_NODE_MEMORY_MB;
        private int yarnNodeVcores = DEFAULT_YARN_NODE_VCORES;
        private int yarnMaxWorkerNodes = DEFAULT_YARN_MAX_WORKER_NODES;
        private boolean reconcileContainersOnStartup = DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP;

        private Builder() {
            // Allow instantiation only via EmrConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EmrConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EmrConfig instance) {
            super(instance);
            this.defaultReleaseLabel = instance.getDefaultReleaseLabel();
            this.clusterStartupDelaySeconds = instance.getClusterStartupDelaySeconds();
            this.stepDurationSeconds = instance.getStepDurationSeconds();
            this.clusterTerminationDelaySeconds = instance.getClusterTerminationDelaySeconds();
            this.runnerMode = instance.getRunnerMode();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
            this.imageTemplate = instance.getImageTemplate().orElse(null);
            this.hiveImageTemplate = instance.getHiveImageTemplate().orElse(null);
            this.yarnNodeMemoryMb = instance.getYarnNodeMemoryMb();
            this.yarnNodeVcores = instance.getYarnNodeVcores();
            this.yarnMaxWorkerNodes = instance.getYarnMaxWorkerNodes();
            this.reconcileContainersOnStartup = instance.isReconcileContainersOnStartup();
        }

        /**
         * Sets the default EMR release label used when creating clusters.
         *
         * @param defaultReleaseLabel the release label (default {@value DEFAULT_RELEASE_LABEL})
         * @return this builder
         */
        public Builder defaultReleaseLabel(String defaultReleaseLabel) {
            this.defaultReleaseLabel = defaultReleaseLabel;
            return this;
        }

        /**
         * Sets how long, in seconds, a new cluster takes to be ready to run steps, the first half
         * {@code STARTING} and the second {@code BOOTSTRAPPING}. Use {@code 0} to make it ready as soon as it is
         * created (default {@value DEFAULT_CLUSTER_STARTUP_DELAY_SECONDS}).
         *
         * @param clusterStartupDelaySeconds the startup delay in seconds
         * @return this builder
         */
        public Builder clusterStartupDelaySeconds(int clusterStartupDelaySeconds) {
            this.clusterStartupDelaySeconds = clusterStartupDelaySeconds;
            return this;
        }

        /**
         * Sets how long, in seconds, a step stays {@code RUNNING} before it completes.
         *
         * <p>{@code 0} completes it as soon as it starts.
         *
         * @param stepDurationSeconds how long, in seconds, a step stays {@code RUNNING} before it completes (default {@value DEFAULT_STEP_DURATION_SECONDS})
         * @return this builder
         */
        public Builder stepDurationSeconds(int stepDurationSeconds) {
            this.stepDurationSeconds = stepDurationSeconds;
            return this;
        }

        /**
         * Sets how long, in seconds, a cluster stays {@code TERMINATING} before it is {@code TERMINATED}.
         *
         * <p>{@code 0} ends it at once.
         *
         * @param clusterTerminationDelaySeconds how long, in seconds, a cluster stays {@code TERMINATING} before it is {@code TERMINATED} (default {@value DEFAULT_CLUSTER_TERMINATION_DELAY_SECONDS})
         * @return this builder
         */
        public Builder clusterTerminationDelaySeconds(int clusterTerminationDelaySeconds) {
            this.clusterTerminationDelaySeconds = clusterTerminationDelaySeconds;
            return this;
        }

        /**
         * Sets the runner mode of EMR steps.
         *
         * <p>{@code immediate} completes each step by time without running anything; {@code docker} runs each Spark step's {@code spark-submit} (and, on a cluster with Hive, each Hive step's {@code hive}) in a container of the release's EMR Serverless image, which needs the Docker socket.
         *
         * @param runnerMode the runner mode of EMR steps (default {@value DEFAULT_RUNNER_MODE})
         * @return this builder
         */
        public Builder runnerMode(String runnerMode) {
            this.runnerMode = runnerMode;
            return this;
        }

        /**
         * Sets the Docker network the containers of the {@code docker} runner mode join.
         *
         * @param dockerNetwork the Docker network the containers of the {@code docker} runner mode join, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets the image a {@code docker} step uses, with {@code %s} standing for the cluster's release label.
         *
         * <p>For example {@code registry.internal/emr-serverless/spark/%s:20250425}. If unset, Floci uses the release's public EMR Serverless image on Amazon ECR Public. Every cluster also runs its HDFS in the Hive image template's image, so a mirror names both.
         *
         * @param imageTemplate the image a {@code docker} step uses, with {@code %s} standing for the cluster's release label, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder imageTemplate(String imageTemplate) {
            this.imageTemplate = imageTemplate;
            return this;
        }

        /**
         * Sets the EMR Serverless Hive image, with {@code %s} standing for the release label, every {@code docker} cluster runs its HDFS in and a cluster with Hive takes Hive from.
         *
         * <p>For example {@code registry.internal/emr-serverless/hive/%s:20250425}. If unset, Floci uses the release's public EMR Serverless Hive image on Amazon ECR Public.
         *
         * @param hiveImageTemplate the EMR Serverless Hive image, with {@code %s} standing for the release label, every {@code docker} cluster runs its HDFS in and a cluster with Hive takes Hive from, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder hiveImageTemplate(String hiveImageTemplate) {
            this.hiveImageTemplate = hiveImageTemplate;
            return this;
        }

        /**
         * Sets the memory, in MB, a {@code docker} cluster's NodeManager offers the YARN containers its steps' Spark executors run in.
         *
         * @param yarnNodeMemoryMb the memory, in MB, a {@code docker} cluster's NodeManager offers the YARN containers its steps' Spark executors run in (default {@value DEFAULT_YARN_NODE_MEMORY_MB})
         * @return this builder
         */
        public Builder yarnNodeMemoryMb(int yarnNodeMemoryMb) {
            this.yarnNodeMemoryMb = yarnNodeMemoryMb;
            return this;
        }

        /**
         * Sets the virtual cores a {@code docker} cluster's NodeManager offers YARN's containers.
         *
         * @param yarnNodeVcores the virtual cores a {@code docker} cluster's NodeManager offers YARN's containers (default {@value DEFAULT_YARN_NODE_VCORES})
         * @return this builder
         */
        public Builder yarnNodeVcores(int yarnNodeVcores) {
            this.yarnNodeVcores = yarnNodeVcores;
            return this;
        }

        /**
         * Sets the most core and task nodes a {@code docker} cluster runs as containers of their own, core nodes first.
         *
         * <p>Each costs 300 to 800 MB while idle. With {@code 0} the primary node holds its DataNode and NodeManager, as a cluster of one node does.
         *
         * @param yarnMaxWorkerNodes the most core and task nodes a {@code docker} cluster runs as containers of their own, core nodes first (default {@value DEFAULT_YARN_MAX_WORKER_NODES})
         * @return this builder
         */
        public Builder yarnMaxWorkerNodes(int yarnMaxWorkerNodes) {
            this.yarnMaxWorkerNodes = yarnMaxWorkerNodes;
            return this;
        }

        /**
         * Sets whether Floci removes, in the {@code docker} runner mode, the EMR step driver and bootstrap containers a previous run of the same Floci left on the Docker daemon when the EMR service starts.
         *
         * <p>Turn it off when two Floci instances share a daemon with the same port and no resource namespace.
         *
         * @param reconcileContainersOnStartup whether Floci removes, in the {@code docker} runner mode, the EMR step driver and bootstrap containers a previous run of the same Floci left on the Docker daemon when the EMR service starts (default {@value DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP})
         * @return this builder
         */
        public Builder reconcileContainersOnStartup(boolean reconcileContainersOnStartup) {
            this.reconcileContainersOnStartup = reconcileContainersOnStartup;
            return this;
        }

        /**
         * Creates an immutable {@link EmrConfig} from this builder.
         *
         * @return the EMR configuration
         */
        @Override
        public EmrConfig build() {
            return new EmrConfig(this);
        }
    }
}
