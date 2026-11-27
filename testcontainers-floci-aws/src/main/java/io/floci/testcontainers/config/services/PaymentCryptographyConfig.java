package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Payment Cryptography-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PaymentCryptographyConfig config = PaymentCryptographyConfig.builder()
 *     .build();
 * }</pre>
 */
public class PaymentCryptographyConfig extends AbstractServiceConfig<PaymentCryptographyConfig.Builder> {

    private PaymentCryptographyConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_SERVICES_PAYMENTCRYPTOGRAPHY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link PaymentCryptographyConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PaymentCryptographyConfig> {

        private Builder() {
            // Allow instantiation only via PaymentCryptographyConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PaymentCryptographyConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PaymentCryptographyConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link PaymentCryptographyConfig} from this builder.
         *
         * @return the Payment Cryptography configuration
         */
        @Override
        public PaymentCryptographyConfig build() {
            return new PaymentCryptographyConfig(this);
        }
    }
}
