package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;
import org.testcontainers.images.builder.Transferable;

import java.util.Optional;
import java.util.UUID;

/**
 * Configuration for Step Functions-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StepFunctionsConfig config = StepFunctionsConfig.builder()
 *     .allowPlaintextHttp(false)
 *     .build();
 * }</pre>
 */
public class StepFunctionsConfig extends AbstractServiceConfig<StepFunctionsConfig.Builder> {

    private static final boolean DEFAULT_ALLOW_PLAINTEXT_HTTP = true;

    private static final String MOCK_CONFIG_FILE_PREFIX = "/tmp/floci-sfn-mock-config-";
    private static final String MOCK_CONFIG_FILE_SUFFIX = ".json";
    private static final int DEFAULT_MAX_WAIT_SECONDS = 30;

    private final boolean allowPlaintextHttp;
    private final String mockConfigFile;
    private final String mockConfig;
    private final int maxWaitSeconds;

    private StepFunctionsConfig(Builder builder) {
        super(builder.enabled);
        this.allowPlaintextHttp = builder.allowPlaintextHttp;
        this.mockConfigFile = builder.mockConfigFile;
        this.mockConfig = builder.mockConfig;
        this.maxWaitSeconds = builder.maxWaitSeconds;
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
     * Returns whether plain HTTP endpoints may be invoked by Step Functions state machines. By
     * default, AWS only allows HTTPS.
     *
     * @return {@code true} if plain HTTP endpoints are allowed
     */
    public boolean isAllowPlaintextHttp() {
        return allowPlaintextHttp;
    }

    /**
     * Returns the path, inside the container, of the Step Functions Local compatible mock
     * configuration file ({@code MockConfigFile.json}). When set, {@code StartExecution} on
     * {@code <stateMachineArn>#<testCaseName>} runs the state machine with that test case's mocked
     * service integration responses.
     *
     * <p>The path is either the one passed to {@link Builder#mockConfigFile(String)} verbatim, or a
     * generated path pointing at the file whose content was passed to {@link Builder#mockConfig(String)}.
     *
     * @return the container path of the mock configuration file, or {@link Optional#empty()} if none
     *         is configured
     */
    public Optional<String> getMockConfigFile() {
        return Optional.ofNullable(mockConfigFile);
    }

    /**
     * Returns the raw mock configuration file content supplied via {@link Builder#mockConfig(String)},
     * if any. When present, this content is copied into the container at {@link #getMockConfigFile()}.
     *
     * @return the mock configuration content, or {@link Optional#empty()} if the mock configuration
     *         was not configured by content
     */
    public Optional<String> getMockConfig() {
        return Optional.ofNullable(mockConfig);
    }

    /**
     * Returns the ceiling, in seconds, on a Wait state pause and a Retry backoff.
     *
     * <p>AWS allows waits far longer than this, but the emulator caps them to keep runs fast. Raise it to
     * exercise longer waits.
     *
     * @return the ceiling, in seconds, on a Wait state pause and a Retry backoff
     */
    public int getMaxWaitSeconds() {
        return maxWaitSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", String.valueOf(allowPlaintextHttp));

            if (mockConfigFile != null) {
                container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_MOCK_CONFIG_FILE", mockConfigFile);
            }

            container.withEnv("FLOCI_SERVICES_STEPFUNCTIONS_MAX_WAIT_SECONDS", String.valueOf(maxWaitSeconds));
        }
    }

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && mockConfigFile != null && mockConfig != null) {
            container.withCopyToContainer(Transferable.of(mockConfig), mockConfigFile);
        }
    }

    /**
     * Builder for {@link StepFunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, StepFunctionsConfig> {

        private boolean allowPlaintextHttp = DEFAULT_ALLOW_PLAINTEXT_HTTP;
        private String mockConfigFile;
        private String mockConfig;
        private int maxWaitSeconds = DEFAULT_MAX_WAIT_SECONDS;

        private Builder() {
            // Allow instantiation only via StepFunctionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link StepFunctionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(StepFunctionsConfig instance) {
            super(instance);
            this.allowPlaintextHttp = instance.isAllowPlaintextHttp();
            this.mockConfigFile = instance.mockConfigFile;
            this.mockConfig = instance.mockConfig;
            this.maxWaitSeconds = instance.getMaxWaitSeconds();
        }

        /**
         * Sets whether plain HTTP endpoints may be invoked by Step Functions state machines. By
         * default, AWS only allows HTTPS.
         *
         * @param allowPlaintextHttp {@code true} to allow plain HTTP endpoints (default {@value DEFAULT_ALLOW_PLAINTEXT_HTTP})
         * @return this builder
         */
        public Builder allowPlaintextHttp(boolean allowPlaintextHttp) {
            this.allowPlaintextHttp = allowPlaintextHttp;
            return this;
        }

        /**
         * Sets the path, inside the container, of a Step Functions Local compatible mock
         * configuration file ({@code MockConfigFile.json}) that already exists in the container (for
         * example one added through a volume or another {@code withCopy*} call).
         *
         * <p>Use {@link #mockConfig(String)} instead to hand over just the file content and let
         * {@link StepFunctionsConfig} take care of placing the file into the container.
         *
         * <p>Calling this method clears any content previously set via {@link #mockConfig(String)}.
         *
         * @param mockConfigFile the container path of the mock configuration file, or {@code null} for none
         * @return this builder
         */
        public Builder mockConfigFile(String mockConfigFile) {
            this.mockConfigFile = mockConfigFile;
            this.mockConfig = null;
            return this;
        }

        /**
         * Sets the content of a Step Functions Local compatible mock configuration file
         * ({@code MockConfigFile.json}).
         *
         * <p>The content is copied into the container under a generated, randomized path, which is
         * then used as {@link StepFunctionsConfig#getMockConfigFile()}. Callers therefore do not need
         * to manage any files themselves.
         *
         * <p>Calling this method clears any path previously set via {@link #mockConfigFile(String)}.
         *
         * @param mockConfig the mock configuration file content, or {@code null} for none
         * @return this builder
         */
        public Builder mockConfig(String mockConfig) {
            this.mockConfig = mockConfig;
            this.mockConfigFile = null;
            return this;
        }

        /**
         * Sets the ceiling, in seconds, on a Wait state pause and a Retry backoff.
         *
         * <p>AWS allows waits far longer than this, but the emulator caps them to keep runs fast. Raise it to
         * exercise longer waits.
         *
         * @param maxWaitSeconds the ceiling, in seconds, on a Wait state pause and a Retry backoff (default {@value DEFAULT_MAX_WAIT_SECONDS})
         * @return this builder
         */
        public Builder maxWaitSeconds(int maxWaitSeconds) {
            this.maxWaitSeconds = maxWaitSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link StepFunctionsConfig} from this builder.
         *
         * @return the Step Functions configuration
         */
        @Override
        public StepFunctionsConfig build() {
            if (mockConfig != null && mockConfigFile == null) {
                this.mockConfigFile = MOCK_CONFIG_FILE_PREFIX + UUID.randomUUID() + MOCK_CONFIG_FILE_SUFFIX;
            }
            return new StepFunctionsConfig(this);
        }
    }
}
