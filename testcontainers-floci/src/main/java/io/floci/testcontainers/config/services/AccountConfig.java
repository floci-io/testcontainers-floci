package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Account Management-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AccountConfig config = AccountConfig.builder()
 *     .build();
 * }</pre>
 */
public class AccountConfig extends AbstractServiceConfig<AccountConfig.Builder> {

    private AccountConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_ACCOUNT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AccountConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AccountConfig> {

        private Builder() {
            // Allow instantiation only via AccountConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AccountConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AccountConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AccountConfig} from this builder.
         *
         * @return the Account Management configuration
         */
        @Override
        public AccountConfig build() {
            return new AccountConfig(this);
        }
    }
}
