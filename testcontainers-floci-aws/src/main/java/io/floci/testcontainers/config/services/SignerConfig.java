package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Signer-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SignerConfig config = SignerConfig.builder()
 *     .mockSigningJobs(true)
 *     .build();
 * }</pre>
 */
public class SignerConfig extends AbstractServiceConfig<SignerConfig.Builder> {

    private static final boolean DEFAULT_MOCK_SIGNING_JOBS = false;

    private final boolean mockSigningJobs;

    private SignerConfig(Builder builder) {
        super(builder);
        this.mockSigningJobs = builder.mockSigningJobs;
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
     * Returns whether signing jobs are recorded as succeeded without producing an artifact, for callers that only read job metadata.
     *
     * <p>Off by default: a job that reports success must have something to download, and Floci does not sign code.
     *
     * @return whether signing jobs are recorded as succeeded without producing an artifact, for callers that only read job metadata
     */
    public boolean isMockSigningJobs() {
        return mockSigningJobs;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SIGNER_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SIGNER_MOCK_SIGNING_JOBS", String.valueOf(mockSigningJobs));
        }
    }

    /**
     * Builder for {@link SignerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SignerConfig> {

        private boolean mockSigningJobs = DEFAULT_MOCK_SIGNING_JOBS;

        private Builder() {
            // Allow instantiation only via SignerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SignerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SignerConfig instance) {
            super(instance);
            this.mockSigningJobs = instance.isMockSigningJobs();
        }

        /**
         * Sets whether signing jobs are recorded as succeeded without producing an artifact, for callers that only read job metadata.
         *
         * <p>Off by default: a job that reports success must have something to download, and Floci does not sign code.
         *
         * @param mockSigningJobs whether signing jobs are recorded as succeeded without producing an artifact, for callers that only read job metadata (default {@value DEFAULT_MOCK_SIGNING_JOBS})
         * @return this builder
         */
        public Builder mockSigningJobs(boolean mockSigningJobs) {
            this.mockSigningJobs = mockSigningJobs;
            return this;
        }

        /**
         * Creates an immutable {@link SignerConfig} from this builder.
         *
         * @return the Signer configuration
         */
        @Override
        public SignerConfig build() {
            return new SignerConfig(this);
        }
    }
}
