package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Batch-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BatchConfig config = BatchConfig.builder()
 *     .enabled(true)
 *     .runnerMode("deferred")
 *     .dockerNetwork("my-batch-network")
 *     .build();
 * }</pre>
 */
public class BatchConfig extends AbstractServiceConfig<BatchConfig.Builder> {

    private static final String DEFAULT_RUNNER_MODE = "immediate";

    private final String runnerMode;
    private final String dockerNetwork;

    private BatchConfig(Builder builder) {
        super(builder.enabled);
        this.runnerMode = builder.runnerMode;
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the runner mode used for Batch jobs.
     *
     * @return the runner mode
     */
    public String getRunnerMode() {
        return runnerMode;
    }

    /**
     * Returns the Docker network used for Batch containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BATCH_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_BATCH_RUNNER_MODE", runnerMode);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_BATCH_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    /**
     * Builder for {@link BatchConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BatchConfig> {

        private String runnerMode = DEFAULT_RUNNER_MODE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via BatchConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BatchConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BatchConfig instance) {
            super(instance);
            this.runnerMode = instance.getRunnerMode();
            this.dockerNetwork = instance.getDockerNetwork();
        }

        /**
         * Sets the runner mode used for Batch jobs.
         *
         * @param runnerMode the runner mode (default {@value DEFAULT_RUNNER_MODE})
         * @return this builder
         */
        public Builder runnerMode(String runnerMode) {
            this.runnerMode = runnerMode;
            return this;
        }

        /**
         * Sets the Docker network that Batch containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link BatchConfig} from this builder.
         *
         * @return the Batch configuration
         */
        public BatchConfig build() {
            return new BatchConfig(this);
        }
    }
}
