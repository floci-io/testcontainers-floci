package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Transfer Family-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TransferConfig config = TransferConfig.builder()
 *     .build();
 * }</pre>
 */
public class TransferFamilyConfig extends AbstractServiceConfig<TransferFamilyConfig.Builder> {

    private TransferFamilyConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_TRANSFER_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link TransferFamilyConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TransferFamilyConfig> {


        private Builder() {
            // Allow instantiation only via TransferConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TransferFamilyConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TransferFamilyConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link TransferFamilyConfig} from this builder.
         *
         * @return the Transfer Family configuration
         */
        @Override
        public TransferFamilyConfig build() {
            return new TransferFamilyConfig(this);
        }
    }
}
