package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

/**
 * Configuration for lifecycle init hooks run by the Floci server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * InitHooksConfig config = InitHooksConfig.builder()
 *     .shellExecutable("/bin/bash")
 *     .timeoutSeconds(60)
 *     .build();
 * }</pre>
 */
public class InitHooksConfig {

    private static final String DEFAULT_SHELL_EXECUTABLE = "/bin/sh";
    private static final long DEFAULT_SHUTDOWN_GRACE_PERIOD_SECONDS = 2;
    private static final long DEFAULT_TIMEOUT_SECONDS = 30;

    private final String shellExecutable;
    private final long shutdownGracePeriodSeconds;
    private final long timeoutSeconds;

    private InitHooksConfig(Builder builder) {
        this.shellExecutable = builder.shellExecutable;
        this.shutdownGracePeriodSeconds = builder.shutdownGracePeriodSeconds;
        this.timeoutSeconds = builder.timeoutSeconds;
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
     * Returns the shell executable used to run init hook scripts.
     *
     * @return the shell executable path
     */
    public String getShellExecutable() {
        return shellExecutable;
    }

    /**
     * Returns the grace period, in seconds, given to shutdown hooks before they are killed.
     *
     * @return the shutdown grace period in seconds
     */
    public long getShutdownGracePeriodSeconds() {
        return shutdownGracePeriodSeconds;
    }

    /**
     * Returns the timeout, in seconds, allowed for an init hook to complete.
     *
     * @return the hook timeout in seconds
     */
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * Applies this init hooks configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_INIT_HOOKS_SHELL_EXECUTABLE", shellExecutable);
        container.withEnv("FLOCI_INIT_HOOKS_SHUTDOWN_GRACE_PERIOD_SECONDS", String.valueOf(shutdownGracePeriodSeconds));
        container.withEnv("FLOCI_INIT_HOOKS_TIMEOUT_SECONDS", String.valueOf(timeoutSeconds));
    }

    /**
     * Builder for {@link InitHooksConfig}.
     */
    public static class Builder {

        private String shellExecutable = DEFAULT_SHELL_EXECUTABLE;
        private long shutdownGracePeriodSeconds = DEFAULT_SHUTDOWN_GRACE_PERIOD_SECONDS;
        private long timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via InitHooksConfig.builder()
        }

        private Builder(InitHooksConfig instance) {
            this.shellExecutable = instance.shellExecutable;
            this.shutdownGracePeriodSeconds = instance.shutdownGracePeriodSeconds;
            this.timeoutSeconds = instance.timeoutSeconds;
        }

        /**
         * Sets the shell executable used to run init hook scripts.
         *
         * @param shellExecutable the shell executable path (default {@value DEFAULT_SHELL_EXECUTABLE})
         * @return this builder
         */
        public Builder shellExecutable(String shellExecutable) {
            this.shellExecutable = shellExecutable;
            return this;
        }

        /**
         * Sets the grace period, in seconds, given to shutdown hooks before they are killed.
         *
         * @param shutdownGracePeriodSeconds the shutdown grace period in seconds
         *                                   (default {@value DEFAULT_SHUTDOWN_GRACE_PERIOD_SECONDS})
         * @return this builder
         */
        public Builder shutdownGracePeriodSeconds(long shutdownGracePeriodSeconds) {
            this.shutdownGracePeriodSeconds = shutdownGracePeriodSeconds;
            return this;
        }

        /**
         * Sets the timeout, in seconds, allowed for an init hook to complete.
         *
         * @param timeoutSeconds the hook timeout in seconds (default {@value DEFAULT_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder timeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link InitHooksConfig} from this builder.
         *
         * @return the init hooks configuration
         */
        public InitHooksConfig build() {
            return new InitHooksConfig(this);
        }
    }
}
