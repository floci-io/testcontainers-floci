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
public class CodePipelineConfig extends AbstractServiceConfig {

    private CodePipelineConfig(Builder builder) {
        super(builder.enabled);
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEPIPELINE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CodePipelineConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via CodePipelineConfig.builder()
        }

        /**
         * Enables or disables the CodePipeline service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Creates an immutable {@link CodePipelineConfig} from this builder.
         *
         * @return the CodePipeline configuration
         */
        public CodePipelineConfig build() {
            return new CodePipelineConfig(this);
        }
    }
}
