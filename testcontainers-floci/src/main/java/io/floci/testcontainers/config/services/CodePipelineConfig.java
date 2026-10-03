package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CodePipeline-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CodePipelineConfig config = CodePipelineConfig.builder()
 *     .build();
 * }</pre>
 */
public class CodePipelineConfig extends AbstractServiceConfig<CodePipelineConfig.Builder> {

    private static final long DEFAULT_SOURCE_POLL_INTERVAL_MS = 500L;

    private final long sourcePollIntervalMs;

    private CodePipelineConfig(Builder builder) {
        super(builder.enabled);
        this.sourcePollIntervalMs = builder.sourcePollIntervalMs;
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
     * Returns how often, in milliseconds, S3 sources are polled for a new object revision.
     *
     * @return how often, in milliseconds, S3 sources are polled for a new object revision
     */
    public long getSourcePollIntervalMs() {
        return sourcePollIntervalMs;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEPIPELINE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CODEPIPELINE_SOURCE_POLL_INTERVAL_MS", String.valueOf(sourcePollIntervalMs));
        }
    }

    /**
     * Builder for {@link CodePipelineConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodePipelineConfig> {

        private long sourcePollIntervalMs = DEFAULT_SOURCE_POLL_INTERVAL_MS;

        private Builder() {
            // Allow instantiation only via CodePipelineConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CodePipelineConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CodePipelineConfig instance) {
            super(instance);
            this.sourcePollIntervalMs = instance.getSourcePollIntervalMs();
        }

        /**
         * Sets how often, in milliseconds, S3 sources are polled for a new object revision.
         *
         * @param sourcePollIntervalMs how often, in milliseconds, S3 sources are polled for a new object revision (default {@value DEFAULT_SOURCE_POLL_INTERVAL_MS})
         * @return this builder
         */
        public Builder sourcePollIntervalMs(long sourcePollIntervalMs) {
            this.sourcePollIntervalMs = sourcePollIntervalMs;
            return this;
        }

        /**
         * Creates an immutable {@link CodePipelineConfig} from this builder.
         *
         * @return the CodePipeline configuration
         */
        @Override
        public CodePipelineConfig build() {
            return new CodePipelineConfig(this);
        }
    }
}
