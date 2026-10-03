package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;
import java.util.List;

/**
 * Configuration for SageMaker-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SageMakerConfig config = SageMakerConfig.builder()
 *     .gpuEnabled(true)
 *     .gpuMode("count")
 *     .build();
 * }</pre>
 */
public class SageMakerConfig extends AbstractServiceConfig<SageMakerConfig.Builder> {

    private static final boolean DEFAULT_GPU_ENABLED = false;
    private static final String DEFAULT_GPU_MODE = "cdi";

    private final String dockerNetwork;
    private final boolean gpuEnabled;
    private final String gpuMode;
    private final List<String> gpuDevices;

    private SageMakerConfig(Builder builder) {
        super(builder.enabled);
        this.dockerNetwork = builder.dockerNetwork;
        this.gpuEnabled = builder.gpuEnabled;
        this.gpuMode = builder.gpuMode;
        this.gpuDevices = builder.gpuDevices;
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
     * Returns the Docker network training job containers are attached to.
     *
     * @return the Docker network training job containers are attached to, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDockerNetwork() {
        return Optional.ofNullable(dockerNetwork);
    }

    /**
     * Returns whether this host lends its GPUs to training job containers.
     *
     * <p>Off by default, so training containers are launched CPU-only. How many GPUs an instance type has is
     * taken from Floci's instance type catalog.
     *
     * @return whether this host lends its GPUs to training job containers
     */
    public boolean isGpuEnabled() {
        return gpuEnabled;
    }

    /**
     * Returns how the GPU device request is expressed to the container runtime: {@code cdi},
     * {@code device-ids} or {@code count}.
     *
     * <p>Defaults to {@code cdi}, the only form Podman resolves; use {@code count} or {@code device-ids}
     * against Docker.
     *
     * @return how the GPU device request is expressed to the container runtime: {@code cdi}, {@code device-ids} or {@code count}
     */
    public String getGpuMode() {
        return gpuMode;
    }

    /**
     * Returns the GPU devices Floci may hand out: CDI names (e.g. {@code nvidia.com/gpu=GPU-<uuid>}) in
     * {@code cdi} mode, or daemon device ids in {@code device-ids} mode.
     *
     * <p>Unset allows no device, so a training job in those modes fails rather than starting. Ignored in
     * {@code count} mode, where the daemon does the choosing.
     *
     * @return the GPU devices Floci may hand out: CDI names (e.g. {@code nvidia.com/gpu=GPU-<uuid>}) in {@code cdi} mode, or daemon device ids in {@code device-ids} mode, or {@link Optional#empty()} if not configured
     */
    public Optional<List<String>> getGpuDevices() {
        return Optional.ofNullable(gpuDevices);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SAGEMAKER_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_SAGEMAKER_DOCKER_NETWORK", dockerNetwork);
            }

            container.withEnv("FLOCI_SERVICES_SAGEMAKER_GPU_ENABLED", String.valueOf(gpuEnabled));
            container.withEnv("FLOCI_SERVICES_SAGEMAKER_GPU_MODE", gpuMode);

            if (gpuDevices != null && !gpuDevices.isEmpty()) {
                container.withEnv("FLOCI_SERVICES_SAGEMAKER_GPU_DEVICES", String.join(",", gpuDevices));
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        // Training jobs run in sibling containers
        return isEnabled();
    }

    /**
     * Builder for {@link SageMakerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SageMakerConfig> {

        private String dockerNetwork;
        private boolean gpuEnabled = DEFAULT_GPU_ENABLED;
        private String gpuMode = DEFAULT_GPU_MODE;
        private List<String> gpuDevices;

        private Builder() {
            // Allow instantiation only via SageMakerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SageMakerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SageMakerConfig instance) {
            super(instance);
            this.dockerNetwork = instance.getDockerNetwork().orElse(null);
            this.gpuEnabled = instance.isGpuEnabled();
            this.gpuMode = instance.getGpuMode();
            this.gpuDevices = instance.getGpuDevices().orElse(null);
        }

        /**
         * Sets the Docker network training job containers are attached to.
         *
         * @param dockerNetwork the Docker network training job containers are attached to, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Sets whether this host lends its GPUs to training job containers.
         *
         * <p>Off by default, so training containers are launched CPU-only. How many GPUs an instance type has
         * is taken from Floci's instance type catalog.
         *
         * @param gpuEnabled whether this host lends its GPUs to training job containers (default {@value DEFAULT_GPU_ENABLED})
         * @return this builder
         */
        public Builder gpuEnabled(boolean gpuEnabled) {
            this.gpuEnabled = gpuEnabled;
            return this;
        }

        /**
         * Sets how the GPU device request is expressed to the container runtime: {@code cdi},
         * {@code device-ids} or {@code count}.
         *
         * <p>Defaults to {@code cdi}, the only form Podman resolves; use {@code count} or {@code device-ids}
         * against Docker.
         *
         * @param gpuMode how the GPU device request is expressed to the container runtime: {@code cdi}, {@code device-ids} or {@code count} (default {@value DEFAULT_GPU_MODE})
         * @return this builder
         */
        public Builder gpuMode(String gpuMode) {
            this.gpuMode = gpuMode;
            return this;
        }

        /**
         * Sets the GPU devices Floci may hand out: CDI names (e.g. {@code nvidia.com/gpu=GPU-<uuid>}) in
         * {@code cdi} mode, or daemon device ids in {@code device-ids} mode.
         *
         * <p>Unset allows no device, so a training job in those modes fails rather than starting. Ignored in
         * {@code count} mode, where the daemon does the choosing.
         *
         * @param gpuDevices the GPU devices Floci may hand out: CDI names (e.g. {@code nvidia.com/gpu=GPU-<uuid>}) in {@code cdi} mode, or daemon device ids in {@code device-ids} mode, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder gpuDevices(List<String> gpuDevices) {
            this.gpuDevices = gpuDevices;
            return this;
        }

        /**
         * Creates an immutable {@link SageMakerConfig} from this builder.
         *
         * @return the SageMaker configuration
         */
        @Override
        public SageMakerConfig build() {
            return new SageMakerConfig(this);
        }
    }
}
