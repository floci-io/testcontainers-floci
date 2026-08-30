package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CodeGuru Reviewer-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CodeGuruReviewerConfig config = CodeGuruReviewerConfig.builder()
 *     .build();
 * }</pre>
 */
public class CodeGuruReviewerConfig extends AbstractServiceConfig<CodeGuruReviewerConfig.Builder> {

    private CodeGuruReviewerConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_CODEGURUREVIEWER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link CodeGuruReviewerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodeGuruReviewerConfig> {

        private Builder() {
            // Allow instantiation only via CodeGuruReviewerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CodeGuruReviewerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CodeGuruReviewerConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link CodeGuruReviewerConfig} from this builder.
         *
         * @return the CodeGuru Reviewer configuration
         */
        @Override
        public CodeGuruReviewerConfig build() {
            return new CodeGuruReviewerConfig(this);
        }
    }
}
