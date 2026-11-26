package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for EMR Serverless-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EmrServerlessConfig config = EmrServerlessConfig.builder()
 *     .runnerMode("docker")
 *     .executorContainers(2)
 *     .build();
 * }</pre>
 */
public class EmrServerlessConfig extends AbstractServiceConfig<EmrServerlessConfig.Builder> {

    private static final int DEFAULT_JOB_RUN_DURATION_SECONDS = 0;
    private static final String DEFAULT_RUNNER_MODE = "immediate";
    private static final String DOCKER_RUNNER_MODE = "docker";
    private static final int DEFAULT_EXECUTOR_CONTAINERS = 0;
    private static final boolean DEFAULT_HONOUR_ARCHITECTURES = false;
    private static final boolean DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP = true;

    private final int jobRunDurationSeconds;
    private final String runnerMode;
    private final String dockerNetwork;
    private final String imageTemplate;
    private final String hiveImageTemplate;
    private final int executorContainers;
    private final boolean honourArchitectures;
    private final boolean reconcileContainersOnStartup;

    private EmrServerlessConfig(Builder builder) {
        super(builder);
        this.jobRunDurationSeconds = builder.jobRunDurationSeconds;
        this.runnerMode = builder.runnerMode;
        this.dockerNetwork = builder.dockerNetwork;
        this.imageTemplate = builder.imageTemplate;
        this.hiveImageTemplate = builder.hiveImageTemplate;
        this.executorContainers = builder.executorContainers;
        this.honourArchitectures = builder.honourArchitectures;
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
     * Returns how long, in seconds, a {@code BATCH} job run stays {@code RUNNING} before it succeeds.
     *
     * <p>{@code 0} makes a run succeed as soon as it starts.
     *
     * @return how long, in seconds, a {@code BATCH} job run stays {@code RUNNING} before it succeeds
     */
    public int getJobRunDurationSeconds() {
        return jobRunDurationSeconds;
    }

    /**
     * Returns the runner mode of EMR Serverless job runs.
     *
     * <p>{@code immediate} follows the job run states without running anything; {@code docker} runs each Spark job run's {@code spark-submit}, and each Hive job run's {@code hive}, in a container of the application's custom image or else of the release's EMR Serverless image, which needs the Docker socket.
     *
     * @return the runner mode of EMR Serverless job runs
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
     * Returns the image a {@code docker} Spark job run uses when its application names no custom image, with {@code %s} standing for the application's release label.
     *
     * <p>For example {@code registry.internal/emr-serverless/spark/%s:20250425}. If unset, Floci uses the release's public image on Amazon ECR Public.
     *
     * @return the image a {@code docker} Spark job run uses when its application names no custom image, with {@code %s} standing for the application's release label, or empty to keep Floci's default
     */
    public Optional<String> getImageTemplate() {
        return Optional.ofNullable(imageTemplate);
    }

    /**
     * Returns the image a {@code docker} Hive job run uses when its application names no custom image, with {@code %s} standing for the application's release label.
     *
     * <p>For example {@code registry.internal/emr-serverless/hive/%s:20250425}. If unset, Floci uses the release's public Hive image on Amazon ECR Public.
     *
     * @return the image a {@code docker} Hive job run uses when its application names no custom image, with {@code %s} standing for the application's release label, or empty to keep Floci's default
     */
    public Optional<String> getHiveImageTemplate() {
        return Optional.ofNullable(hiveImageTemplate);
    }

    /**
     * Returns how many executors a {@code docker} Spark job run may have at once, each in a container of its own.
     *
     * <p>{@code 0} runs a job's executors in its driver's container, in Spark's local mode. The application's maximum capacity limits them too.
     *
     * @return how many executors a {@code docker} Spark job run may have at once, each in a container of its own
     */
    public int getExecutorContainers() {
        return executorContainers;
    }

    /**
     * Returns whether a {@code docker} job run's driver and executors run for its application's architecture ({@code X86_64} on linux/amd64, {@code ARM64} on linux/arm64) instead of on the Docker host's own platform.
     *
     * <p>A foreign platform needs the Docker host to emulate it (Docker Desktop, or {@code binfmt_misc} with QEMU).
     *
     * @return whether a {@code docker} job run's driver and executors run for its application's architecture ({@code X86_64} on linux/amd64, {@code ARM64} on linux/arm64) instead of on the Docker host's own platform
     */
    public boolean isHonourArchitectures() {
        return honourArchitectures;
    }

    /**
     * Returns whether Floci removes, in the {@code docker} runner mode, the EMR Serverless drivers a previous run of the same Floci left on the Docker daemon before its first job run starts.
     *
     * <p>Turn it off when two Floci instances share a daemon with the same port and no resource namespace.
     *
     * @return whether Floci removes, in the {@code docker} runner mode, the EMR Serverless drivers a previous run of the same Floci left on the Docker daemon before its first job run starts
     */
    public boolean isReconcileContainersOnStartup() {
        return reconcileContainersOnStartup;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_JOB_RUN_DURATION_SECONDS", String.valueOf(jobRunDurationSeconds));
            container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_RUNNER_MODE", runnerMode);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_DOCKER_NETWORK", dockerNetwork);
            }

            if (imageTemplate != null) {
                container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_IMAGE_TEMPLATE", imageTemplate);
            }

            if (hiveImageTemplate != null) {
                container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_HIVE_IMAGE_TEMPLATE", hiveImageTemplate);
            }

            container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_EXECUTOR_CONTAINERS", String.valueOf(executorContainers));
            container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_HONOUR_ARCHITECTURES", String.valueOf(honourArchitectures));
            container.withEnv("FLOCI_SERVICES_EMRSERVERLESS_RECONCILE_CONTAINERS_ON_STARTUP", String.valueOf(reconcileContainersOnStartup));
        }
    }

    /**
     * Returns {@code true} while enabled with the {@code docker} runner mode, the only mode that runs job runs in
     * containers.
     */
    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && DOCKER_RUNNER_MODE.equalsIgnoreCase(runnerMode);
    }

    /**
     * Builder for {@link EmrServerlessConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EmrServerlessConfig> {

        private int jobRunDurationSeconds = DEFAULT_JOB_RUN_DURATION_SECONDS;
        private String runnerMode = DEFAULT_RUNNER_MODE;
        private String dockerNetwork;
        private String imageTemplate;
        private String hiveImageTemplate;
        private int executorContainers = DEFAULT_EXECUTOR_CONTAINERS;
        private boolean honourArchitectures = DEFAULT_HONOUR_ARCHITECTURES;
        private boolean reconcileContainersOnStartup = DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP;

        private Builder() {
            // Allow instantiation only via EmrServerlessConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EmrServerlessConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EmrServerlessConfig instance) {
            super(instance);
            this.jobRunDurationSeconds = instance.getJobRunDurationSeconds();
            this.runnerMode = instance.getRunnerMode();
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
            this.imageTemplate = instance.getImageTemplate().orElse(null);
            this.hiveImageTemplate = instance.getHiveImageTemplate().orElse(null);
            this.executorContainers = instance.getExecutorContainers();
            this.honourArchitectures = instance.isHonourArchitectures();
            this.reconcileContainersOnStartup = instance.isReconcileContainersOnStartup();
        }

        /**
         * Sets how long, in seconds, a {@code BATCH} job run stays {@code RUNNING} before it succeeds.
         *
         * <p>{@code 0} makes a run succeed as soon as it starts.
         *
         * @param jobRunDurationSeconds how long, in seconds, a {@code BATCH} job run stays {@code RUNNING} before it succeeds (default {@value DEFAULT_JOB_RUN_DURATION_SECONDS})
         * @return this builder
         */
        public Builder jobRunDurationSeconds(int jobRunDurationSeconds) {
            this.jobRunDurationSeconds = jobRunDurationSeconds;
            return this;
        }

        /**
         * Sets the runner mode of EMR Serverless job runs.
         *
         * <p>{@code immediate} follows the job run states without running anything; {@code docker} runs each Spark job run's {@code spark-submit}, and each Hive job run's {@code hive}, in a container of the application's custom image or else of the release's EMR Serverless image, which needs the Docker socket.
         *
         * @param runnerMode the runner mode of EMR Serverless job runs (default {@value DEFAULT_RUNNER_MODE})
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
         * Sets the image a {@code docker} Spark job run uses when its application names no custom image, with {@code %s} standing for the application's release label.
         *
         * <p>For example {@code registry.internal/emr-serverless/spark/%s:20250425}. If unset, Floci uses the release's public image on Amazon ECR Public.
         *
         * @param imageTemplate the image a {@code docker} Spark job run uses when its application names no custom image, with {@code %s} standing for the application's release label, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder imageTemplate(String imageTemplate) {
            this.imageTemplate = imageTemplate;
            return this;
        }

        /**
         * Sets the image a {@code docker} Hive job run uses when its application names no custom image, with {@code %s} standing for the application's release label.
         *
         * <p>For example {@code registry.internal/emr-serverless/hive/%s:20250425}. If unset, Floci uses the release's public Hive image on Amazon ECR Public.
         *
         * @param hiveImageTemplate the image a {@code docker} Hive job run uses when its application names no custom image, with {@code %s} standing for the application's release label, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder hiveImageTemplate(String hiveImageTemplate) {
            this.hiveImageTemplate = hiveImageTemplate;
            return this;
        }

        /**
         * Sets how many executors a {@code docker} Spark job run may have at once, each in a container of its own.
         *
         * <p>{@code 0} runs a job's executors in its driver's container, in Spark's local mode. The application's maximum capacity limits them too.
         *
         * @param executorContainers how many executors a {@code docker} Spark job run may have at once, each in a container of its own (default {@value DEFAULT_EXECUTOR_CONTAINERS})
         * @return this builder
         */
        public Builder executorContainers(int executorContainers) {
            this.executorContainers = executorContainers;
            return this;
        }

        /**
         * Sets whether a {@code docker} job run's driver and executors run for its application's architecture ({@code X86_64} on linux/amd64, {@code ARM64} on linux/arm64) instead of on the Docker host's own platform.
         *
         * <p>A foreign platform needs the Docker host to emulate it (Docker Desktop, or {@code binfmt_misc} with QEMU).
         *
         * @param honourArchitectures whether a {@code docker} job run's driver and executors run for its application's architecture ({@code X86_64} on linux/amd64, {@code ARM64} on linux/arm64) instead of on the Docker host's own platform (default {@value DEFAULT_HONOUR_ARCHITECTURES})
         * @return this builder
         */
        public Builder honourArchitectures(boolean honourArchitectures) {
            this.honourArchitectures = honourArchitectures;
            return this;
        }

        /**
         * Sets whether Floci removes, in the {@code docker} runner mode, the EMR Serverless drivers a previous run of the same Floci left on the Docker daemon before its first job run starts.
         *
         * <p>Turn it off when two Floci instances share a daemon with the same port and no resource namespace.
         *
         * @param reconcileContainersOnStartup whether Floci removes, in the {@code docker} runner mode, the EMR Serverless drivers a previous run of the same Floci left on the Docker daemon before its first job run starts (default {@value DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP})
         * @return this builder
         */
        public Builder reconcileContainersOnStartup(boolean reconcileContainersOnStartup) {
            this.reconcileContainersOnStartup = reconcileContainersOnStartup;
            return this;
        }

        /**
         * Creates an immutable {@link EmrServerlessConfig} from this builder.
         *
         * @return the EMR Serverless configuration
         */
        @Override
        public EmrServerlessConfig build() {
            return new EmrServerlessConfig(this);
        }
    }
}
