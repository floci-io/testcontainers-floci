package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Secrets Manager-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SecretsManagerConfig config = SecretsManagerConfig.builder()
 *     .defaultRecoveryWindowDays(7)
 *     .build();
 * }</pre>
 */
public class SecretsManagerConfig extends AbstractServiceConfig<SecretsManagerConfig.Builder> {

    private static final int DEFAULT_RECOVERY_WINDOW_DAYS = 30;

    private final int defaultRecoveryWindowDays;

    private SecretsManagerConfig(Builder builder) {
        super(builder.enabled);
        this.defaultRecoveryWindowDays = builder.defaultRecoveryWindowDays;
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
     * Returns the default recovery window in days.
     *
     * @return the default recovery window in days
     */
    public int getDefaultRecoveryWindowDays() {
        return defaultRecoveryWindowDays;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_SECRETSMANAGER_DEFAULT_RECOVERY_WINDOW_DAYS", String.valueOf(defaultRecoveryWindowDays));
        }
    }

    /**
     * Builder for {@link SecretsManagerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SecretsManagerConfig> {

        private int defaultRecoveryWindowDays = DEFAULT_RECOVERY_WINDOW_DAYS;

        private Builder() {
            // Allow instantiation only via SecretsManagerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SecretsManagerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SecretsManagerConfig instance) {
            super(instance);
            this.defaultRecoveryWindowDays = instance.getDefaultRecoveryWindowDays();
        }

        /**
         * Sets the default recovery window in days for deleted secrets.
         *
         * @param defaultRecoveryWindowDays the default recovery window in days for deleted secrets (default {@value DEFAULT_RECOVERY_WINDOW_DAYS})
         * @return this builder
         */
        public Builder defaultRecoveryWindowDays(int defaultRecoveryWindowDays) {
            this.defaultRecoveryWindowDays = defaultRecoveryWindowDays;
            return this;
        }

        /**
         * Creates an immutable {@link SecretsManagerConfig} from this builder.
         *
         * @return the Secrets Manager configuration
         */
        public SecretsManagerConfig build() {
            return new SecretsManagerConfig(this);
        }
    }
}
