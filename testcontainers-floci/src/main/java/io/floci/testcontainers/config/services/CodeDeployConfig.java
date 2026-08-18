package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CodeDeploy-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CodeDeployConfig config = CodeDeployConfig.builder()
 *     .build();
 * }</pre>
 */
public class CodeDeployConfig extends AbstractServiceConfig<CodeDeployConfig.Builder> {


    private CodeDeployConfig(Builder builder) {
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
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEDEPLOY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CodeDeployConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodeDeployConfig> {


        private Builder() {
            // Allow instantiation only via CodeDeployConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CodeDeployConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CodeDeployConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CodeDeployConfig} from this builder.
         *
         * @return the CodeDeploy configuration
         */
        @Override
        public CodeDeployConfig build() {
            return new CodeDeployConfig(this);
        }
    }
}
