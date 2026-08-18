package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CodeBuild-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CodeBuildConfig config = CodeBuildConfig.builder()
 *     .build();
 * }</pre>
 */
public class CodeBuildConfig extends AbstractServiceConfig<CodeBuildConfig.Builder> {

    private final String dockerNetwork;

    private CodeBuildConfig(Builder builder) {
        super(builder.enabled);
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
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the Docker network used for CodeBuild containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEBUILD_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled() && dockerNetwork != null) {
            container.withEnv("FLOCI_SERVICES_CODEBUILD_DOCKER_NETWORK", dockerNetwork);
        }
    }

    /**
     * Builder for {@link CodeBuildConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodeBuildConfig> {

        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via CodeBuildConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CodeBuildConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CodeBuildConfig instance) {
            super(instance);
            this.dockerNetwork = instance.getDockerNetwork();
        }

        /**
         * Sets the Docker network that CodeBuild containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use default network
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link CodeBuildConfig} from this builder.
         *
         * @return the CodeBuild configuration
         */
        @Override
        public CodeBuildConfig build() {
            return new CodeBuildConfig(this);
        }
    }
}
