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

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEPIPELINE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CodePipelineConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodePipelineConfig> {


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
